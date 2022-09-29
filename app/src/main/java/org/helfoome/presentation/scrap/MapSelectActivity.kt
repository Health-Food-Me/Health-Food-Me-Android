package org.helfoome.presentation.scrap

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
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
import org.helfoome.databinding.ActivityMapSelectBinding
import org.helfoome.domain.entity.MarkerInfo
import org.helfoome.presentation.MainActivity
import org.helfoome.presentation.MainActivity.Companion.GANGNAM_X
import org.helfoome.presentation.MainActivity.Companion.GANGNAM_Y
import org.helfoome.presentation.MainViewModel
import org.helfoome.presentation.detail.RestaurantDetailFragment
import org.helfoome.presentation.restaurant.adapter.RestaurantTabAdapter
import org.helfoome.util.ResolutionMetrics
import org.helfoome.util.SnackBarTopDown
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.replace
import org.helfoome.util.ext.startActivity
import javax.inject.Inject

@AndroidEntryPoint
class MapSelectActivity : BindingActivity<ActivityMapSelectBinding>(R.layout.activity_map_select), OnMapReadyCallback {
    @Inject
    lateinit var resolutionMetrics: ResolutionMetrics
    private val viewModel: MainViewModel by viewModels()
    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            viewModel.setExpendedBottomSheetDialog(newState == BottomSheetBehavior.STATE_EXPANDED)
            behavior.isDraggable = true
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                binding.isFloatingNotVisible = false

                binding.layoutMapSelect.visibility = View.VISIBLE
            }

            viewModel.setIsDetailCollapsed(newState == BottomSheetBehavior.STATE_COLLAPSED)

            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                binding.isFloatingNotVisible = true

                binding.layoutMapSelect.visibility = View.VISIBLE
            }
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
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

        replace<RestaurantDetailFragment>(R.id.fragment_container_detail)
        initNaverMap()
        initView()
        initListeners()
        initObservers()
    }

    override fun onStart() {
        super.onStart()
        behavior.addBottomSheetCallback(bottomSheetCallback)
    }

    private fun initView() {
        behavior = BottomSheetBehavior.from(binding.layoutBottomSheet)
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun initListeners() {
        binding.ibBack.setOnClickListener {
            finish()
        }

        binding.ibQuit.setOnClickListener {
            startActivity<MainActivity>()
        }
    }

    private fun initObservers() {
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
                        } else {
                            icon = OverlayImage.fromResource(
                                if (marker.isDietRestaurant) R.drawable.ic_marker_green_small
                                else R.drawable.ic_marker_red_small
                            )
                        }
                        map = naverMap
                        captionText = marker.name

                        isHideCollidedCaptions = true

                        setOnClickListener {
                            viewModel.getReviewCheck(marker.id)
                            viewModel.fetchSelectedRestaurantDetailInfo(
                                marker.id,
                                locationSource.lastLocation?.latitude ?: marker.latitude,
                                locationSource.lastLocation?.longitude ?: marker.longitude
                            )

                            replace<RestaurantDetailFragment>(R.id.fragment_container_detail)
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
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
        viewModel.isReviewWriteSuccess.flowWithLifecycle(lifecycle)
            .onEach {
                // TODO : 지금 너무 토스트 띄우는 부분 재사용이 어렵게 되어있습니다.. 넘 보일러 플레이트코드에여.. Event State로 분기처리하는 거 강력 추천합니다;;
                if (it)
                    SnackBarTopDown.makeSnackBarTopDown(this, binding.snvProfileModify, "리뷰가 작성되었습니다")
            }
            .launchIn(lifecycleScope)

        viewModel.behaviorState.flowWithLifecycle(lifecycle)
            .onEach {
                behavior.state = it
            }
            .launchIn(lifecycleScope)
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
//        binding.layoutRestaurantDialog.layoutRestaurantTabMenu.removeOnTabSelectedListener(tabSelectedListener)
//        binding.layoutRestaurantDialog.layoutAppBar.removeOnOffsetChangedListener(appbarOffsetListener)
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
            this.locationSource = this@MapSelectActivity.locationSource
            locationTrackingMode = LocationTrackingMode.NoFollow

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

        binding.fabLocationMain.setOnClickListener {
            naverMap.cameraPosition =
                CameraPosition(
                    LatLng(
                        locationSource.lastLocation?.latitude ?: GANGNAM_X,
                        locationSource.lastLocation?.longitude ?: GANGNAM_Y
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
