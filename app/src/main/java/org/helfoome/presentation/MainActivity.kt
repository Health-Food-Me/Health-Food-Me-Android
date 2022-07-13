package org.helfoome.presentation

import android.Manifest
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.widget.NestedScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.ActivityMainBinding
import org.helfoome.presentation.drawer.MyReviewActivity
import org.helfoome.presentation.drawer.MyScrapActivity
import org.helfoome.presentation.drawer.ProfileModifyActivity
import org.helfoome.presentation.restaurant.RestaurantTabAdapter
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.showToast

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main), OnMapReadyCallback {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private val restaurantDetailAdapter = RestaurantTabAdapter(this)
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        binding.viewModel = viewModel
        initView()
        initListeners()
        initObservers()
        initNaverMap()
        requirePermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions,
                grantResults
            )
        ) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun requirePermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                }
                else -> {
                    showToast("위치 권한이 없어 현재 위치를 알 수 없습니다.")
                }
            }
        }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onStart() {
        super.onStart()
        behavior.addBottomSheetCallback(bottomSheetCallback)
    }

    private fun initView() {
        behavior = BottomSheetBehavior.from(binding.layoutBottomSheet)
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        with(binding.layoutRestaurantDialog) {
            vpRestaurantDetail.adapter = restaurantDetailAdapter
            TabLayoutMediator(layoutRestaurantTabMenu, vpRestaurantDetail) { tab, position ->
                tab.text = resources.getStringArray(R.array.restaurant_detail_tab_titles)[position]
            }.attach()

            tvNumber.paintFlags = tvNumber.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }
    }

    private fun initListeners() {
        with(binding.layoutRestaurantDialog) {
            layoutAppBar.setOnClickListener {
                if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            btnBack.setOnClickListener {
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            btnScrap.setOnClickListener {
                it.isSelected = !it.isSelected
                // TODO 스크랩 상태값 업데이트 api 요청
            }

            btnScrapToolbar.setOnClickListener {
                it.isSelected = !it.isSelected
            }

            tvNumber.setOnClickListener {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tvNumber.text)))
            }

            with(binding) {
                btnHamburger.setOnClickListener {
                    layoutDrawer.open()
                }
            }

            with(binding.layoutDrawerHeader) {
                btnEdit.setOnClickListener {
                    startActivity(Intent(this@MainActivity, ProfileModifyActivity::class.java))
                }
                tvReview.setOnClickListener {
                    startActivity(Intent(this@MainActivity, MyReviewActivity::class.java))
                }
                tvScrap.setOnClickListener {
                    startActivity(Intent(this@MainActivity, MyScrapActivity::class.java))
                }
                tvReport.setOnClickListener {
                    sendGmail()
                }
                tvModifyReport.setOnClickListener {
                    sendGmail()
                }
            }
        }
    }

    private fun sendGmail() {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "abc@gmail.com", null
            )
        )
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body")
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    private fun initObservers() {
        viewModel.selectedRestaurant.observe(this) {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
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
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            viewModel.setExpendedBottomSheetDialog(newState == BottomSheetBehavior.STATE_EXPANDED)
            behavior.isDraggable = newState != BottomSheetBehavior.STATE_EXPANDED
        }

        override fun onSlide(bottomSheetView: View, slideOffset: Float) {
        }
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

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource
        binding.fabLocation.map = naverMap

        val marker = Marker()
        marker.position = LatLng(37.5670135, 126.9783740)
        marker.icon = OverlayImage.fromResource(R.drawable.ic_marker_red)
        marker.map = map

        val marker2 = Marker()
        marker2.position = LatLng(37.5570135, 126.9783740)
        marker2.icon = OverlayImage.fromResource(R.drawable.ic_marker_green)
        marker2.map = map

        val initialPosition = LatLng(37.5670135, 126.9783740)
        val cameraUpdate = CameraUpdate.scrollTo(initialPosition)
        naverMap.moveCamera(cameraUpdate)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
