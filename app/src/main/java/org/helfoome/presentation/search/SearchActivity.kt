package org.helfoome.presentation.search

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
import org.helfoome.presentation.MainActivity.Companion.EOUNJU_X
import org.helfoome.presentation.MainActivity.Companion.EOUNJU_Y
import org.helfoome.presentation.MainViewModel
import org.helfoome.presentation.detail.RestaurantDetailFragment
import org.helfoome.presentation.search.adapter.*
import org.helfoome.presentation.search.type.SearchMode
import org.helfoome.util.ResolutionMetrics
import org.helfoome.util.SnackBarTopDown
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.*
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : BindingActivity<ActivitySearchBinding>(R.layout.activity_search), OnMapReadyCallback {
    // TODO : Inject 로직 수정 요망
    private var isAutoCompleteResult = false

    @Inject
    lateinit var resolutionMetrics: ResolutionMetrics
    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap

    private val mainViewModel: MainViewModel by viewModels()

    private var markerList: List<Pair<Marker, MarkerInfo>> = listOf()

    private val requestReviewWrite =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                SnackBarTopDown.makeSnackBarTopDown(this, binding.snvProfileModify, "리뷰가 작성되었습니다")
            }
        }

    private lateinit var locationSource: FusedLocationSource
    private val autoCompleteAdapter = AutoCompleteAdapter { name, restaurantId, isCategory ->
        isAutoCompleteResult = true
        searchViewModel.insertKeyword(name, isCategory)
        if (!isCategory)
            searchViewModel.setDetail(true)
        searchViewModel.setSearchMode(SearchMode.RESULT)

        mainViewModel.fetchSelectedRestaurantDetailInfo(
            restaurantId,
            locationSource.lastLocation?.latitude ?: EOUNJU_X,
            locationSource.lastLocation?.longitude ?: EOUNJU_Y
        )

        markerList.forEach { marker ->
            if (marker.second.id == restaurantId) {
                marker.first.icon = OverlayImage.fromResource(
                    if (marker.second.isDietRestaurant) R.drawable.ic_marker_green_big
                    else R.drawable.ic_marker_red_big
                )

                mainViewModel.fetchSelectedRestaurantDetailInfo(
                    restaurantId,
                    locationSource.lastLocation?.latitude ?: marker.second.latitude,
                    locationSource.lastLocation?.longitude ?: marker.second.longitude
                )
                mainViewModel.setSelectedLocationPoint(marker.second.latitude, marker.second.longitude)
            }
        }
    }

    private val recentAdapter = RecentAdapter(
        { keyword, isCategory ->
            // TODO : 서버 통신 주의, 고정 값 위도
            if (isCategory)
                searchViewModel.getSearchCategoryCardList(127.027610, 37.498095, keyword)
            else
                searchViewModel.getSearchResultCardList(127.027610, 37.498095, keyword)
            binding.etSearch.setText(keyword)
            searchViewModel.setSearchMode(SearchMode.RESULT)
        },
    ) {
        searchViewModel.removeKeyword(it)
    }
    private val resultAdapter = ResultAdapter { restaurantId, latitude, longitude ->
        // TODO : 서버 통신 주의
        if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            mainViewModel.getReviewCheck(restaurantId)

            mainViewModel.fetchSelectedRestaurantDetailInfo(
                restaurantId,
                locationSource.lastLocation?.latitude ?: latitude,
                locationSource.lastLocation?.longitude ?: longitude
            )
            mainViewModel.setSelectedLocationPoint(latitude, longitude)

            searchViewModel.setDetail(true)
        }
    }

    private val searchMapTopAdapter = SearchMapTopAdapter {
        if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            remove()
            binding.isFloatingVisible = false
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        } else {
            behavior.peekHeight = resolutionMetrics.toPixel(135)
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
                mainViewModel.setExpendedBottomSheetDialog(newState == BottomSheetBehavior.STATE_EXPANDED)
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    binding.isFloatingVisible = true
                if (newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_EXPANDED)
                    binding.isFloatingVisible = false
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    markerList.forEach {
                        it.first.icon = OverlayImage.fromResource(
                            if (it.second.isDietRestaurant) R.drawable.ic_marker_green_small
                            else R.drawable.ic_marker_red_small
                        )
                    }
                    binding.isMainNotVisible = false
                }

                mainViewModel.setIsDetailCollapsed(newState == BottomSheetBehavior.STATE_COLLAPSED)
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
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        initView()
        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        mainViewModel.setLocationSource(locationSource)
        initNaverMap()
        openKeyboard()
        initClickEvent()
        initFocusChangeListener()
        initKeyListeners()
        initTextChangeEvent()
        observeData()
        binding.viewModel = mainViewModel
        initObservers()
    }

    private fun openKeyboard() {
        binding.etSearch.requestFocus()
        showKeyboard(binding.etSearch)
    }

    private fun initView() {
        binding.layoutRestaurantListDialog.rvSearch.itemAnimator = null
        behavior = BottomSheetBehavior.from(binding.layoutBottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isDraggable = false
    }

    private fun initClickEvent() {
        with(binding) {
            with(searchViewModel) {
                btnBack.setOnClickListener {
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
                    if (stringListFrom(R.array.main_chip_group).contains(text.toString()))
                        searchViewModel.getSearchCategoryCardList(127.027610, 37.498095, text.toString())
                    else
                        searchViewModel.getSearchResultCardList(127.027610, 37.498095, text.toString())
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
                            remove()
                        }
                        is SearchViewModel.SearchUiState.AutoCompleteSearch -> {
                            with(autoCompleteAdapter) {
                                setKeywordListener {
                                    it.data.first
                                }
                                submitList(it.data.second)
                                // TODO : 추후 더 좋은 데이터 갱신 요망
                                autoCompleteAdapter.notifyDataSetChanged()
                                remove()
                            }
                        }
                        is SearchViewModel.SearchUiState.Result -> {
                            remove()
                            binding.layoutRestaurantListDialog.isResultEmpty = it.data.isEmpty()
                            if (it.data.isNotEmpty())
                                searchViewModel.insertKeyword(binding.etSearch.text.toString(), it.isCategory)
                            resultAdapter.submitList(it.data)

                            markerList.forEach { marker ->
                                marker.first.map = null
                            }

                            markerList = it.data.map { searchResultInfo -> searchResultInfo.toMakerInfo() }.map { markerInfo ->
                                Pair(
                                    Marker().apply {
                                        position = LatLng(markerInfo.latitude, markerInfo.longitude)
                                        icon = OverlayImage.fromResource(
                                            if (markerInfo.isDietRestaurant) R.drawable.ic_marker_green_small
                                            else R.drawable.ic_marker_red_small
                                        )
                                        map = naverMap

                                        isHideCollidedCaptions = true

                                        captionText = markerInfo.name

                                        setOnClickListener {
                                            searchViewModel.setDetail(true)
                                            mainViewModel.getReviewCheck(markerInfo.id)
                                            mainViewModel.fetchSelectedRestaurantDetailInfo(
                                                markerInfo.id,
                                                locationSource.lastLocation?.latitude ?: markerInfo.latitude,
                                                locationSource.lastLocation?.longitude ?: markerInfo.longitude
                                            )
                                            mainViewModel.setSelectedLocationPoint(markerInfo.latitude, markerInfo.longitude)

                                            replace<RestaurantDetailFragment>(R.id.fragment_container_detail)
                                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                                            binding.isMainNotVisible = true
                                            markerList.forEach { marker ->
                                                marker.first.icon = OverlayImage.fromResource(
                                                    if (marker.second.isDietRestaurant) R.drawable.ic_marker_green_small
                                                    else R.drawable.ic_marker_red_small
                                                )
                                            }
                                            icon = OverlayImage.fromResource(
                                                if (markerInfo.isDietRestaurant) R.drawable.ic_marker_green_big
                                                else R.drawable.ic_marker_red_big
                                            )
                                            mainViewModel.markerId(this.position)?.let { id -> }
                                            true
                                        }
                                    },
                                    markerInfo
                                )
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
                            behavior.peekHeight = resolutionMetrics.toPixel(168)
                            behavior.isDraggable = true
                            behavior.isHideable = true
                            replace<RestaurantDetailFragment>(R.id.fragment_container_detail)
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                        else -> {
                            binding.etSearch.isEnabled = true
                            behavior.isHideable = false
                            binding.isDetail = false
                            remove()
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
        mapView.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        behavior.addBottomSheetCallback(bottomSheetCallback)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        mainViewModel.getProfile()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onRestart() {
        super.onRestart()
        mainViewModel.getProfile()
    }

    private fun initObservers() {
        mainViewModel.isReviewWriteSuccess.flowWithLifecycle(lifecycle)
            .onEach {
                // TODO : 지금 너무 토스트 띄우는 부분 재사용이 어렵게 되어있습니다.. 넘 보일러 플레이트코드에여.. Event State로 분기처리하는 거 강력 추천합니다;;
                if (it)
                    SnackBarTopDown.makeSnackBarTopDown(this, binding.snvProfileModify, "리뷰가 작성되었습니다")
            }
            .launchIn(lifecycleScope)

        mainViewModel.behaviorState.flowWithLifecycle(lifecycle)
            .onEach {
                behavior.state = it
            }
            .launchIn(lifecycleScope)
    }

    override fun onStop() {
        super.onStop()
        behavior.removeBottomSheetCallback(bottomSheetCallback)
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap.apply {
            uiSettings.isZoomControlEnabled = false
            setOnMapClickListener { _, _ ->
                if (searchViewModel.isDetail.value)
                    behavior.state = BottomSheetBehavior.STATE_HIDDEN
                markerList.forEach {
                    it.first.icon = OverlayImage.fromResource(
                        if (it.second.isDietRestaurant) R.drawable.ic_marker_green_small
                        else R.drawable.ic_marker_red_small
                    )
                }
            }

            minZoom = 5.0
            maxZoom = 20.0
            this.locationSource = this@SearchActivity.locationSource

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                cameraPosition = CameraPosition(
                    LatLng(EOUNJU_X, EOUNJU_Y), 12.0
                )
                locationTrackingMode = LocationTrackingMode.NoFollow
            } else {
                cameraPosition = CameraPosition(LatLng(EOUNJU_X, EOUNJU_Y), 12.0)
            }

            addOnCameraChangeListener { reason, _ ->
            }
        }

        binding.btnLocation.setOnClickListener {
            naverMap.cameraPosition =
                CameraPosition(
                    LatLng(
                        locationSource.lastLocation?.latitude ?: EOUNJU_X,
                        locationSource.lastLocation?.longitude ?: EOUNJU_Y
                    ),
                    14.0
                )
        }

        binding.btnLocationMain.setOnClickListener {
            naverMap.cameraPosition =
                CameraPosition(
                    LatLng(
                        locationSource.lastLocation?.latitude ?: EOUNJU_X,
                        locationSource.lastLocation?.longitude ?: EOUNJU_Y
                    ),
                    14.0
                )
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
