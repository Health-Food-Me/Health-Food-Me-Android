package org.helfoome.presentation.scrap

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
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
import org.helfoome.R
import org.helfoome.databinding.ActivityMapSelectBinding
import org.helfoome.domain.entity.MarkerInfo
import org.helfoome.presentation.MainActivity
import org.helfoome.presentation.MainActivity.Companion.GANGNAM_X
import org.helfoome.presentation.MainActivity.Companion.GANGNAM_Y
import org.helfoome.presentation.MainViewModel
import org.helfoome.presentation.restaurant.MapSelectionBottomDialogFragment
import org.helfoome.presentation.restaurant.adapter.RestaurantTabAdapter
import org.helfoome.presentation.review.ReviewWritingActivity
import org.helfoome.presentation.type.HashtagViewType
import org.helfoome.util.ResolutionMetrics
import org.helfoome.util.SnackBarTopDown
import org.helfoome.util.binding.BindingActivity
import javax.inject.Inject

@AndroidEntryPoint
class MapSelectActivity : BindingActivity<ActivityMapSelectBinding>(R.layout.activity_map_select), OnMapReadyCallback {
    @Inject
    lateinit var resolutionMetrics: ResolutionMetrics
    private val viewModel: MainViewModel by viewModels()
    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private var mapSelectionBottomDialog: MapSelectionBottomDialogFragment? = null
    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) = Unit
        override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
        override fun onTabSelected(tab: TabLayout.Tab?) {
            // 리뷰 탭에서만 리뷰 작성 버튼 보여주기
            viewModel.setReviewTab(tab?.position == 2)
        }
    }
    private val appbarOffsetListener = AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
        binding.layoutRestaurantDialog.tvRestaurantNameInToolbar.visibility = if (verticalOffset == 0) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            binding.layoutRestaurantDialog.nvDetail.isNestedScrollingEnabled = false
            viewModel.setExpendedBottomSheetDialog(newState == BottomSheetBehavior.STATE_EXPANDED)
            behavior.isDraggable = true
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                binding.isFloatingNotVisible = false

                binding.layoutMapSelect.visibility = View.VISIBLE
            }
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                binding.isFloatingNotVisible = true

                binding.layoutMapSelect.visibility = View.VISIBLE
            }
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                binding.isMainNotVisible = false
                markerList.forEach {
                    it.first.icon = OverlayImage.fromResource(
                        if (it.second) R.drawable.ic_marker_green_small
                        else R.drawable.ic_marker_red_small
                    )
                }
            }
            if (newState == BottomSheetBehavior.STATE_EXPANDED)
                binding.layoutMapSelect.visibility = View.GONE
        }

        override fun onSlide(bottomSheetView: View, slideOffset: Float) {
        }
    }

    private var markerList: List<Pair<Marker, Boolean>> = listOf()
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    private val restaurantDetailAdapter = RestaurantTabAdapter(this)

    private val requestReviewWrite =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                viewModel.fetchHFMReviewList()
                SnackBarTopDown.makeSnackBarTopDown(this, binding.snvProfileModify, "리뷰가 작성되었습니다")
                val data = activityResult.data ?: return@registerForActivityResult
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        initNaverMap()
        initView()
        initListeners()
        initObservers()
    }

    override fun onStart() {
        super.onStart()
        behavior.addBottomSheetCallback(bottomSheetCallback)
        binding.layoutRestaurantDialog.layoutRestaurantTabMenu.addOnTabSelectedListener(tabSelectedListener)
        binding.layoutRestaurantDialog.layoutAppBar.addOnOffsetChangedListener(appbarOffsetListener)
    }

    private fun initView() {
        behavior = BottomSheetBehavior.from(binding.layoutBottomSheet)
        behavior.state = BottomSheetBehavior.STATE_HIDDEN

        with(binding.layoutRestaurantDialog) {
            vpRestaurantDetail.adapter = restaurantDetailAdapter
            TabLayoutMediator(layoutRestaurantTabMenu, vpRestaurantDetail) { tab, position ->
                tab.text = resources.getStringArray(R.array.restaurant_detail_tab_titles)[position]
                binding.layoutRestaurantDialog.btnWriteReview.visibility = if (position == 2) View.VISIBLE else View.INVISIBLE
            }.attach()

            tvNumber.paintFlags = tvNumber.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            btnWriteReview.apply {
                setOnClickListener {
                    requestReviewWrite.launch(
                        Intent(this@MapSelectActivity, ReviewWritingActivity::class.java)
                            .putExtra(ARG_RESTAURANT_ID, viewModel?.selectedRestaurant?.value?.id ?: return@setOnClickListener)
                            .putExtra("RESTAURANT_NAME", binding.layoutRestaurantDialog.tvRestaurantName.text.toString())
                    )
                }
            }
        }
    }

    private fun initListeners() {
        with(binding.layoutRestaurantDialog) {
            layoutAppBar.setOnClickListener {
                if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.isFloatingNotVisible = true
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

        binding.ibBack.setOnClickListener {
            finish()
        }

        binding.ibQuit.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java).apply {
                    // TODO : CLEAR_TOP 말고 다른 인텐트 필터 적용 필요
                    addFlags(FLAG_ACTIVITY_CLEAR_TOP)
                }
            )
        }
    }

    private fun showMapSelectionBottomDialog() {
        if (mapSelectionBottomDialog?.isAdded == true) return
        mapSelectionBottomDialog = MapSelectionBottomDialogFragment()
        mapSelectionBottomDialog?.show(supportFragmentManager, "MapSelectionBottomDialogFragment")
    }

    private fun initObservers() {
        viewModel.selectedRestaurant.observe(this) {
            with(binding.layoutRestaurantDialog) {
                layoutRestaurantTabMenu.selectTab(layoutRestaurantTabMenu.getTabAt(0))
                hashtag.setHashtag(it.tags, HashtagViewType.RESTAURANT_SUMMARY_TYPE)
            }
        }

        viewModel.isReviewTab.observe(this) {
            binding.layoutRestaurantDialog.layoutReviewBtnBackground.visibility =
                if (it.peekContent()) View.VISIBLE else View.INVISIBLE
        }

        viewModel.location.observe(this) { markers ->
            markerList.forEach {
                it.first.map = null
            }
            markerList = markers.map { marker ->
                Pair(
                    Marker().apply {
                        position = LatLng(marker.latitude, marker.longitude)
                        if (marker.id == intent.getStringExtra("RESTAURANT_ID")) {
                            icon = OverlayImage.fromResource(
                                if (marker.isDietRestaurant) R.drawable.ic_marker_green_big
                                else R.drawable.ic_marker_red_big
                            )

                            viewModel.getReviewCheck(marker.id)
                            viewModel.fetchSelectedRestaurantDetailInfo(
                                marker.id,
                                locationSource.lastLocation?.latitude ?: marker.latitude,
                                locationSource.lastLocation?.longitude ?: marker.longitude
                            )

                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            binding.isMainNotVisible = true
                        } else {
                            icon = OverlayImage.fromResource(
                                if (marker.isDietRestaurant) R.drawable.ic_marker_green_small
                                else R.drawable.ic_marker_red_small
                            )
                        }
                        map = naverMap

                        setOnClickListener {
                            viewModel.getReviewCheck(marker.id)
                            viewModel.fetchSelectedRestaurantDetailInfo(
                                marker.id,
                                locationSource.lastLocation?.latitude ?: marker.latitude,
                                locationSource.lastLocation?.longitude ?: marker.longitude
                            )

                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            binding.isMainNotVisible = true
                            markerList.forEach {
                                it.first.icon = OverlayImage.fromResource(
                                    if (it.second) R.drawable.ic_marker_green_small
                                    else R.drawable.ic_marker_red_small
                                )
                            }
                            icon = OverlayImage.fromResource(
                                if (marker.isDietRestaurant) R.drawable.ic_marker_green_big
                                else R.drawable.ic_marker_red_big
                            )
                            viewModel.markerId(this.position)?.let { id -> }
                            true
                        }
                    },
                    marker.isDietRestaurant
                )
            }
        }

        viewModel.checkReview.observe(this) {
            if (viewModel.checkReview.value == false) {
                binding.layoutRestaurantDialog.btnWriteReview.isEnabled = true
            } else if (viewModel.checkReview.value == true) {
                binding.layoutRestaurantDialog.btnWriteReview.isEnabled = false
            }
        }
    }

    override fun onBackPressed() {
        when (behavior.state) {
            BottomSheetBehavior.STATE_COLLAPSED -> behavior.state = BottomSheetBehavior.STATE_HIDDEN
            BottomSheetBehavior.STATE_EXPANDED -> behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            BottomSheetBehavior.STATE_HIDDEN -> super.onBackPressed()
        }
    }

    override fun onStop() {
        super.onStop()
        behavior.removeBottomSheetCallback(bottomSheetCallback)
        binding.layoutRestaurantDialog.layoutRestaurantTabMenu.removeOnTabSelectedListener(tabSelectedListener)
        binding.layoutRestaurantDialog.layoutAppBar.removeOnOffsetChangedListener(appbarOffsetListener)
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

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap.apply {
            uiSettings.isZoomControlEnabled = false
            setOnMapClickListener { _, _ ->
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            minZoom = 5.0
            maxZoom = 20.0
            this.locationSource = this.locationSource

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                cameraPosition = CameraPosition(
                    LatLng(GANGNAM_X, GANGNAM_Y), 12.0
                )
            } else {
                cameraPosition = CameraPosition(LatLng(GANGNAM_X, GANGNAM_Y), 12.0)
            }

            addOnCameraChangeListener { reason, _ ->
            }
        }

        intent.getParcelableArrayListExtra<MarkerInfo>(MainActivity.MARKER_INFO)?.let { viewModel.setMapInfo(it) }

        binding.fabLocation.setOnClickListener {
            naverMap.cameraPosition =
                CameraPosition(LatLng(GANGNAM_X, GANGNAM_Y), 14.0)
        }
        binding.fabLocationMain.setOnClickListener {
            naverMap.cameraPosition =
                CameraPosition(LatLng(GANGNAM_X, GANGNAM_Y), 14.0)
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
        private const val ARG_RESTAURANT_ID = "restaurantId"
    }
}
