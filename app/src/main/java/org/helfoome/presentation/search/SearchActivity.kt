package org.helfoome.presentation.search

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.helfoome.R
import org.helfoome.databinding.ActivitySearchBinding
import org.helfoome.domain.entity.MarkerInfo
import org.helfoome.presentation.MainActivity
import org.helfoome.presentation.MainActivity.Companion.MARKER_INFO
import org.helfoome.presentation.MainViewModel
import org.helfoome.presentation.restaurant.MapSelectionBottomDialogFragment
import org.helfoome.presentation.restaurant.adapter.RestaurantTabAdapter
import org.helfoome.presentation.review.ReviewWritingActivity
import org.helfoome.presentation.search.adapter.*
import org.helfoome.presentation.search.type.SearchMode
import org.helfoome.presentation.type.HashtagViewType
import org.helfoome.util.ResolutionMetrics
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.closeKeyboard
import org.helfoome.util.ext.markerFilter
import org.helfoome.util.ext.showKeyboard
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : BindingActivity<ActivitySearchBinding>(R.layout.activity_search), OnMapReadyCallback {
    // TODO : Inject 로직 수정 요망
    private var isAutoCompleteResult = false

    @Inject
    lateinit var resolutionMetrics: ResolutionMetrics
    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var naverMap: NaverMap

    private val mainViewModel: MainViewModel by viewModels()
    private var category: String? = null
    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) = Unit
        override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
        override fun onTabSelected(tab: TabLayout.Tab?) {
            // 리뷰 탭에서만 리뷰 작성 버튼 보여주기
            mainViewModel.setReviewTab(tab?.position == 2)
        }
    }
    private val appbarOffsetListener = AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
        binding.layoutRestaurantDialog.tvRestaurantNameInToolbar.visibility = if (verticalOffset == 0) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    private var markerList: List<Pair<Marker, Pair<String, Boolean>>> = listOf()

    private val requestReviewWrite =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val animation = AnimationUtils.loadAnimation(this, R.anim.anim_snackbar_top_down)
                binding.snvProfileModify.animation = animation
                binding.snvProfileModify.setText("리뷰가 작성되었습니다")
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) = Unit
                    override fun onAnimationEnd(animation: Animation?) {
                        val bottomTopAnimation = AnimationUtils.loadAnimation(this@SearchActivity, R.anim.anim_snackbar_bottom_top)
                        binding.snvProfileModify.animation = bottomTopAnimation
                        binding.snvProfileModify.setText("리뷰가 작성되었습니다")
                    }

                    override fun onAnimationRepeat(p0: Animation?) = Unit
                })
            }
        }

    private val restaurantDetailAdapter = RestaurantTabAdapter(this)

    private lateinit var locationSource: FusedLocationSource
    private var mapSelectionBottomDialog: MapSelectionBottomDialogFragment? = null
    private val autoCompleteAdapter = AutoCompleteAdapter { name, restaurantId ->
        isAutoCompleteResult = true
        searchViewModel.insertKeyword(name)
        searchViewModel.setDetail(true)
        searchViewModel.setSearchMode(SearchMode.RESULT)
        val location = mainViewModel.location.value?.filter {
            it.id == restaurantId
        }?.get(0)
        location?.let {
            markerList.forEach { marker ->
                if (marker.second.first == it.name) {
                    marker.first.icon = OverlayImage.fromResource(
                        if (marker.second.second) R.drawable.ic_marker_green_big
                        else R.drawable.ic_marker_red_big
                    )
                }
            }
            mainViewModel.fetchSelectedRestaurantDetailInfo(restaurantId,
                it.latitude,
                it.longitude)
        }
        searchViewModel.setDetail(true)
    }
    private val recentAdapter = RecentAdapter(
        {
            // TODO : 서버 통신 주의, 고정 값 위도
            searchViewModel.getSearchResultCardList(37.498095, 127.027610, it)
            binding.etSearch.setText(it)
            searchViewModel.setSearchMode(SearchMode.RESULT)
        },
    ) {
        searchViewModel.removeKeyword(it)
    }
    private val resultAdapter = ResultAdapter { restaurantId ->
        // TODO : 서버 통신 주의
        if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            mainViewModel.getReviewCheck(restaurantId)
            val location = mainViewModel.location.value?.filter {
                it.id == restaurantId
            }?.get(0)
            location?.let {
                markerList.forEach { marker ->
                    if (marker.second.first == it.name) {
                        marker.first.icon = OverlayImage.fromResource(
                            if (marker.second.second) R.drawable.ic_marker_green_big
                            else R.drawable.ic_marker_red_big
                        )
                    }
                }
                mainViewModel.fetchSelectedRestaurantDetailInfo(
                    restaurantId,
                    it.latitude,
                    it.longitude
                )
            }
            searchViewModel.setDetail(true)
        }
    }

    private val searchMapTopAdapter = SearchMapTopAdapter {
        if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        } else {
            behavior.peekHeight = resolutionMetrics.toPixel(203)
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            behavior.isDraggable = true
        }
    }
    private val searchRecentTopAdapter = SearchRecentTopAdapter()
    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            with(newState != BottomSheetBehavior.STATE_EXPANDED) {
                if (searchViewModel.isDetail.value && !this)
                    binding.layoutSearch.visibility = View.GONE
                else
                    binding.layoutSearch.visibility = View.VISIBLE
                searchMapTopAdapter.setVisible(this)
                behavior.isDraggable = this
                binding.isLineVisible = this

                binding.layoutRestaurantDialog.viewSpacing.visibility = when (this) {
                    true -> View.VISIBLE
                    else -> View.GONE
                }

                binding.layoutRestaurantDialog.nvDetail.isNestedScrollingEnabled = false
                mainViewModel.setExpendedBottomSheetDialog(newState == BottomSheetBehavior.STATE_EXPANDED)
                behavior.isDraggable = true
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    binding.isFloatingVisible = true
                if (newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_EXPANDED)
                    binding.isFloatingVisible = false
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    markerList.forEach {
                        it.first.icon = OverlayImage.fromResource(
                            if (it.second.second) R.drawable.ic_marker_green_small
                            else R.drawable.ic_marker_red_small
                        )
                    }
                    binding.isMainNotVisible = false
                }
            }
        }

        override fun onSlide(bottomSheetView: View, slideOffset: Float) {}
    }

    private val recentConcatAdapter = ConcatAdapter(
        searchRecentTopAdapter,
        recentAdapter
    )

    private val resultConcatAdapter = ConcatAdapter(
        searchMapTopAdapter,
        resultAdapter
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        initNaverMap()
        openKeyboard()
        initClickEvent()
        initFocusChangeListener()
        initKeyListeners()
        initTextChangeEvent()
        observeData()
        binding.viewModel = mainViewModel

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        initListeners()
        initObservers()
    }

    private fun openKeyboard() {
        binding.etSearch.requestFocus()
        showKeyboard(binding.etSearch)
    }

    private fun initView() {
        intent.getParcelableArrayListExtra<MarkerInfo>(MARKER_INFO)?.let { mainViewModel.setMapInfo(it) }
        binding.layoutRestaurantDialog.isSearch = true
        behavior = BottomSheetBehavior.from(binding.layoutBottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isDraggable = false

        with(binding.layoutRestaurantDialog) {
            vpRestaurantDetail.adapter = restaurantDetailAdapter
            TabLayoutMediator(layoutRestaurantTabMenu, vpRestaurantDetail) { tab, position ->
                tab.text = resources.getStringArray(R.array.restaurant_detail_tab_titles)[position]
                binding.layoutRestaurantDialog.btnWriteReview.visibility = if (position == 2) View.VISIBLE else View.INVISIBLE
            }.attach()

            tvNumber.paintFlags = tvNumber.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            btnWriteReview.apply {
                setOnClickListener {
                    requestReviewWrite.launch(Intent(this@SearchActivity, ReviewWritingActivity::class.java))
                }
            }
        }
    }

    private fun initClickEvent() {
        with(binding) {
            with(searchViewModel) {
                btnBack.setOnClickListener {
                    startSearchModeBackEvent(searchMode.value)
                }

                layoutRestaurantDialog.btnBack.setOnClickListener {
                    startSearchModeBackEvent(searchMode.value)
                }

                btnDelete.setOnClickListener {
                    when (searchMode.value) {
                        SearchMode.AUTO_COMPLETE -> {
                            etSearch.text.clear()
                        }
                        else -> finish()
                    }
                }
            }
        }
    }

    private fun initFocusChangeListener() {
        binding.etSearch.setOnFocusChangeListener { _, isFocus ->
            if (isFocus && searchViewModel.searchMode.value == SearchMode.RESULT) {
                binding.layoutRestaurantListDialog.isResultEmpty = false
                searchViewModel.setSearchMode(SearchMode.AUTO_COMPLETE)
                searchViewModel.setDetail(false)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun startSearchModeBackEvent(value: SearchMode) {
        binding.layoutRestaurantListDialog.isResultEmpty = false
        if (isAutoCompleteResult) {
            if (behavior.state == BottomSheetBehavior.STATE_EXPANDED && searchViewModel.isDetail.value)
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else {
                searchViewModel.setDetail(false)
                searchViewModel.setSearchMode(SearchMode.RECENT)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                isAutoCompleteResult = false
            }
        } else {
            when (value) {
                SearchMode.RESULT -> {
                    if (searchViewModel.isDetail.value) {
                        if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                            binding.isLineVisible = true
                            behavior.peekHeight = resolutionMetrics.toPixel(236)
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        } else {
                            searchViewModel.setDetail(false)
                            searchViewModel.setSearchMode(SearchMode.RESULT)
                            behavior.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    } else {
                        if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED)
                            searchViewModel.setSearchMode(SearchMode.RESULT)
                        else
                            searchViewModel.setSearchMode(SearchMode.RECENT)
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
                SearchMode.AUTO_COMPLETE -> {
                    searchViewModel.setSearchMode(SearchMode.RECENT)
                }
                else -> {
                    finish()
                }
            }
        }
    }

    private fun initTextChangeEvent() {
        with(binding.etSearch) {
            addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    searchViewModel.setSearchMode(SearchMode.RECENT)
                } else {
                    searchViewModel.setSearchMode(SearchMode.AUTO_COMPLETE)
                    searchViewModel.getAutoCompleteKeyword(it.toString())
                }
            }
        }
    }

    private fun initKeyListeners() {
        with(binding.etSearch) {
            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                    // TODO : 최근 검색어 추가 서버 통신 시 수정
                    searchViewModel.getSearchResultCardList(37.498095, 127.027610, text.toString())
                    closeKeyboard(this)
                    binding.etSearch.clearFocus()
                    searchViewModel.setSearchMode(SearchMode.RESULT)
                }
                false
            }
        }
    }

    private fun observeData() {
        with(binding) {
            searchViewModel.searchMode.flowWithLifecycle(lifecycle)
                .onEach {
                    when (it) {
                        SearchMode.RECENT -> {
                            behavior.isDraggable = false
                            isDeleteButtonVisible = false
                            etSearch.text.clear()
                            layoutRestaurantListDialog.rvSearch.adapter = recentConcatAdapter
                        }
                        SearchMode.AUTO_COMPLETE -> {
                            // TODO : 자동완성 서버 통신
                            behavior.isDraggable = false
                            btnDelete.isSelected = false
                            isDeleteButtonVisible = true
                            layoutRestaurantListDialog.rvSearch.adapter = autoCompleteAdapter
                        }
                        else -> {
                            // TODO : 서버 통신 받아온 리스트 띄우기
                            binding.etSearch.clearFocus()
                            btnDelete.isSelected = true
                            isDeleteButtonVisible = true
                            behavior.isDraggable = true
                            layoutRestaurantListDialog.rvSearch.adapter = resultConcatAdapter
                            closeKeyboard(binding.etSearch)
                        }
                    }
                }.launchIn(lifecycleScope)

            searchViewModel.searchUiState.flowWithLifecycle(lifecycle)
                .onEach {
                    when (it) {
                        is SearchViewModel.SearchUiState.RecentSearch -> {
                            recentAdapter.submitList(it.data)
                        }
                        is SearchViewModel.SearchUiState.AutoCompleteSearch -> {
                            with(autoCompleteAdapter) {
                                setKeywordListener {
                                    it.data.first
                                }
                                submitList(it.data.second)
                                // TODO : 추후 더 좋은 데이터 갱신 요망
                                autoCompleteAdapter.notifyDataSetChanged()
                            }
                        }
                        is SearchViewModel.SearchUiState.Result -> {
                            binding.layoutRestaurantListDialog.isResultEmpty = it.data.isEmpty()
                            if (it.data.isNotEmpty())
                                searchViewModel.insertKeyword(binding.etSearch.text.toString())
                            resultAdapter.submitList(it.data)

                            markerList.forEach {
                                it.first.map = null
                            }
                            mainViewModel.location.value?.let { locationList ->
                                markerList = locationList.markerFilter(
                                    it.data.map { data ->
                                        data.id
                                    }
                                ).map { marker ->
                                    Pair(
                                        Marker().apply {
                                            position = LatLng(marker.latitude, marker.longitude)
                                            icon = OverlayImage.fromResource(
                                                if (marker.isDietRestaurant) R.drawable.ic_marker_green_small
                                                else R.drawable.ic_marker_red_small
                                            )
                                            map = naverMap

                                            setOnClickListener {
                                                searchViewModel.setDetail(true)
                                                mainViewModel.getReviewCheck(marker.id)
                                                mainViewModel.fetchSelectedRestaurantDetailInfo(
                                                    marker.id,
                                                    marker.latitude,
                                                    marker.longitude
                                                )

                                                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                                                binding.isMainNotVisible = true
                                                markerList.forEach {
                                                    it.first.icon = OverlayImage.fromResource(
                                                        if (it.second.second) R.drawable.ic_marker_green_small
                                                        else R.drawable.ic_marker_red_small
                                                    )
                                                }
                                                icon = OverlayImage.fromResource(
                                                    if (marker.isDietRestaurant) R.drawable.ic_marker_green_big
                                                    else R.drawable.ic_marker_red_big
                                                )
                                                mainViewModel.markerId(this.position)?.let { id -> }
                                                true
                                            }
                                        },
                                        Pair(
                                            marker.name,
                                            marker.isDietRestaurant
                                        )
                                    )
                                }
                            }
                        }
                        else -> {
                            // TODO : Loading, Error 추후 구현
                        }
                    }
                }.launchIn(lifecycleScope)

            // 최근 검색어 받아오기
            searchViewModel.keywordList.flowWithLifecycle(lifecycle)
                .onEach {
                    recentAdapter.submitList(it)
                }
                .launchIn(lifecycleScope)

            searchViewModel.isDetail.flowWithLifecycle(lifecycle)
                .onEach {
                    when (it) {
                        true -> {
                            binding.etSearch.isEnabled = false
                            binding.isDetail = true
                            behavior.peekHeight = resolutionMetrics.toPixel(236)
                            behavior.isDraggable = true
                            behavior.isHideable = true
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                        else -> {
                            binding.etSearch.isEnabled = true
                            behavior.isHideable = false
                            binding.isDetail = false
                        }
                    }
                }
                .launchIn(lifecycleScope)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus

        if (view != null && ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE && view is EditText && !view.javaClass.name.startsWith(
                "android.webkit."
            )
        ) {
            val locationList = IntArray(2)
            view.getLocationOnScreen(locationList)
            val x = ev.rawX + view.left - locationList[0]
            val y = ev.rawY + view.top - locationList[1]
            if (x < view.left || x > view.right || y < view.top || y > view.bottom) {
                closeKeyboard(view)
                view.clearFocus()
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        // TODO : Collpased 높이 이상한거랑
        // TODO : hide 안되는 거
        startSearchModeBackEvent(searchViewModel.searchMode.value)
    }

    private fun initNaverMap() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fragment_naver_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.commit {
                    add<MapFragment>(R.id.fragment_naver_map)
                    setReorderingAllowed(true)
                }
            }
        mapFragment.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()
        behavior.addBottomSheetCallback(bottomSheetCallback)
        binding.layoutRestaurantDialog.layoutRestaurantTabMenu.addOnTabSelectedListener(tabSelectedListener)
        binding.layoutRestaurantDialog.layoutAppBar.addOnOffsetChangedListener(appbarOffsetListener)
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.getProfile()
    }

    override fun onRestart() {
        super.onRestart()
        mainViewModel.getProfile()
    }

    private fun initListeners() {
        with(binding.layoutRestaurantDialog) {
            layoutAppBar.setOnClickListener {
                if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.isFloatingVisible = true
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            btnBack.setOnClickListener {
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            tvNumber.setOnClickListener {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tvNumber.text)))
            }

            btnNavi.setOnClickListener {
                showMapSelectionBottomDialog()
            }
        }
    }

    private fun showMapSelectionBottomDialog() {
        if (mapSelectionBottomDialog?.isAdded == true) return
        mapSelectionBottomDialog = MapSelectionBottomDialogFragment()
        mapSelectionBottomDialog?.show(supportFragmentManager, "MapSelectionBottomDialogFragment")
    }

    private fun initObservers() {
        mainViewModel.selectedRestaurant.observe(this) {
            with(binding.layoutRestaurantDialog) {
                layoutRestaurantTabMenu.selectTab(layoutRestaurantTabMenu.getTabAt(0))
                hashtag.setHashtag(it.tags, HashtagViewType.RESTAURANT_SUMMARY_TYPE)
            }
        }

        mainViewModel.isReviewTab.observe(this) {
            binding.layoutRestaurantDialog.layoutReviewBtnBackground.visibility =
                if (it.peekContent()) View.VISIBLE else View.INVISIBLE
        }

        mainViewModel.checkReview.observe(this) {
            if (mainViewModel.checkReview.value == false) {
                binding.layoutRestaurantDialog.btnWriteReview.isEnabled = true
            } else if (mainViewModel.checkReview.value == true) {
                binding.layoutRestaurantDialog.btnWriteReview.isEnabled = false
            }
        }
    }

    override fun onStop() {
        super.onStop()
        behavior.removeBottomSheetCallback(bottomSheetCallback)
        binding.layoutRestaurantDialog.layoutRestaurantTabMenu.removeOnTabSelectedListener(tabSelectedListener)
        binding.layoutRestaurantDialog.layoutAppBar.removeOnOffsetChangedListener(appbarOffsetListener)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap.apply {
            uiSettings.isZoomControlEnabled = false
            setOnMapClickListener { _, _ ->
                if (searchViewModel.isDetail.value)
                    behavior.state = BottomSheetBehavior.STATE_HIDDEN
                markerList.forEach {
                    it.first.icon = OverlayImage.fromResource(
                        if (it.second.second) R.drawable.ic_marker_green_small
                        else R.drawable.ic_marker_red_small
                    )
                }
            }

            minZoom = 5.0
            maxZoom = 20.0
            this.locationSource = this.locationSource

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                cameraPosition = CameraPosition(
                    LatLng(MainActivity.GANGNAM_X, MainActivity.GANGNAM_Y), 12.0
                )
                locationTrackingMode = LocationTrackingMode.Follow
            } else {
                cameraPosition = CameraPosition(LatLng(MainActivity.GANGNAM_X, MainActivity.GANGNAM_Y), 12.0)
            }

            addOnCameraChangeListener { reason, _ ->
            }
        }

        binding.btnLocation.setOnClickListener {
            naverMap.cameraPosition =
                CameraPosition(LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude), 14.0)
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
        }
        binding.btnLocationMain.setOnClickListener {
            naverMap.cameraPosition =
                CameraPosition(LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude), 14.0)
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
