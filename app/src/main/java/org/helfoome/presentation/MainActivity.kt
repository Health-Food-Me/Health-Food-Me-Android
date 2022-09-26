package org.helfoome.presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.helfoome.R
import org.helfoome.databinding.ActivityMainBinding
import org.helfoome.presentation.detail.RestaurantDetailFragment
import org.helfoome.presentation.drawer.MyReviewActivity
import org.helfoome.presentation.drawer.ProfileModifyActivity
import org.helfoome.presentation.drawer.SettingActivity
import org.helfoome.presentation.login.GuestLoginFragmentDialog
import org.helfoome.presentation.scrap.MyScrapActivity
import org.helfoome.presentation.search.SearchActivity
import org.helfoome.presentation.type.AlertType
import org.helfoome.presentation.type.FoodType
import org.helfoome.presentation.alert.AlertFragmentDialog
import org.helfoome.util.ChipFactory
import org.helfoome.util.ResolutionMetrics
import org.helfoome.util.SnackBarTopDown
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main), OnMapReadyCallback {
    @Inject
    lateinit var resolutionMetrics: ResolutionMetrics

    private val viewModel: MainViewModel by viewModels()
    private var category: String? = null
    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            viewModel.setExpendedBottomSheetDialog(newState == BottomSheetBehavior.STATE_EXPANDED)
            behavior.isDraggable = true
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                markerList.forEach {
                    it.first.icon = OverlayImage.fromResource(
                        if (it.second) R.drawable.ic_marker_green_small
                        else R.drawable.ic_marker_red_small
                    )
                }
            }
        }

        override fun onSlide(bottomSheetView: View, slideOffset: Float) = Unit
    }

    private var markerList: List<Pair<Marker, Boolean>> = listOf()
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    private val requestModifyNickname =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                SnackBarTopDown.makeSnackBarTopDown(this, binding.snvProfileModify, "닉네임이 변경되었습니다")
                viewModel.getProfile()
            }
        }

    private val controlHamburger =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                binding.layoutDrawer.close()
            }
        }

    private val String.toChip: Chip
        get() = ChipFactory.create(layoutInflater).also { it.text = this }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.layoutDrawerHeader.drawerViewModel = viewModel
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        viewModel.setLocationSource(locationSource)

        initView()
        initNaverMap()
        initListeners()
        initObservers()

        replace<RestaurantDetailFragment>(R.id.fragment_container_detail)
    }

    private fun provideChipClickListener(chip: Chip) =
        View.OnClickListener {
            if (!chip.isChecked) {
                binding.cgFoodTag.clearCheck()
                viewModel.getMapInfo(naverMap.cameraPosition.target)
            } else {
                binding.cgFoodTag.clearCheck()
                chip.isChecked = true
                viewModel.getMapInfo(naverMap.cameraPosition.target, chip.text.toString())
            }
        }

    override fun onStart() {
        super.onStart()
        behavior.addBottomSheetCallback(bottomSheetCallback)
    }

    override fun onStop() {
        super.onStop()
        behavior.removeBottomSheetCallback(bottomSheetCallback)
    }

    private fun initView() {
        behavior = BottomSheetBehavior.from(binding.layoutBottomSheet)
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.layoutDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        initChip()
    }

    private fun initChip() {
        for (i in FoodType.values().indices) {
            with(binding.cgFoodTag) {
                addView(
                    stringListFrom(R.array.main_chip_group)[i].toChip.apply {
                        setRippleColorResource(android.R.color.transparent)
                        setOnClickListener(provideChipClickListener(this))
                        setChipIconResource(FoodType.values()[i].icon)
                        setChipIconTintResource(FoodType.values()[i].iconTint)
                        setChipBackgroundColorResource(FoodType.values()[i].color)
                    }
                )
            }
        }
    }

    private fun startScrapEvent(isSelected: Boolean) {
        if (isSelected)
            viewModel.getScrapList()
        else
            viewModel.getMapInfo(naverMap.cameraPosition.target)
    }

    private fun initListeners() {
        binding.btnBookmarkMain.setOnClickListener {
            if (viewModel.getIsGuestLogin()) {
                supportGuestLogin()
            } else {
                it.isSelected = !it.isSelected
                startScrapEvent(it.isSelected)
            }
        }

        binding.btnHamburger.setOnClickListener {
            viewModel.getProfile()
            binding.layoutDrawer.open()
        }

        binding.layoutSearch.setOnClickListener {
            startActivity<SearchActivity>(MARKER_INFO to viewModel.defaultLocation)
        }

        with(binding.layoutDrawer) {
            addDrawerListener(object : DrawerLayout.DrawerListener {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) = Unit
                override fun onDrawerOpened(drawerView: View) = setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                override fun onDrawerClosed(drawerView: View) = setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                override fun onDrawerStateChanged(newState: Int) = Unit
            })
        }
        with(binding.layoutDrawerHeader) {
            btnEdit.setOnClickListener {
                requestModifyNickname.launch(Intent(this@MainActivity, ProfileModifyActivity::class.java))
            }
            ivLogin.setOnClickListener {
                supportGuestLogin()
            }
            tvReview.setOnClickListener {
                if (viewModel.getIsGuestLogin()) {
                    supportGuestLogin()
                } else {
                    controlHamburger.launch(Intent(this@MainActivity, MyReviewActivity::class.java))
                }
            }
            tvScrap.setOnClickListener {
                if (viewModel.getIsGuestLogin()) {
                    supportGuestLogin()
                } else {
                    controlHamburger.launch(
                        Intent(this@MainActivity, MyScrapActivity::class.java).apply {
                            putExtras(bundleOf(MARKER_INFO to viewModel.defaultLocation))
                        }
                    )
                }
            }
            tvReport.setOnClickListener {
                sendGmail()
            }
            tvModifyReport.setOnClickListener {
                sendGmail()
            }
            tvSetting.setOnClickListener {
                controlHamburger.launch(Intent(this@MainActivity, SettingActivity::class.java))
            }
            tvLogout.setOnClickListener {
                AlertFragmentDialog(AlertType.LOGOUT).show(supportFragmentManager, "AlertDialog")
            }
        }
    }

    private fun supportGuestLogin() {
        GuestLoginFragmentDialog().show(supportFragmentManager, "GuestLoginDialog")
    }

    private fun showScarpSnackBar(snackBarText: String) {
        val view = layoutInflater.inflate(R.layout.view_snackbar, null)
        view.findViewById<TextView>(R.id.tv_snackBar).text = snackBarText
        val snackBar = Snackbar.make(binding.layoutMain, "", 1000)
        val layout = snackBar.view as Snackbar.SnackbarLayout
        layout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        layout.setPadding(62, 0, 53, 10)
        layout.addView(view)
        snackBar.animationMode = ANIMATION_MODE_SLIDE
        snackBar.show()
    }

    private fun sendGmail() {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO,
            Uri.fromParts(
                "mailto", "abc@gmail.com", null
            )
        )
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body")
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    private fun initObservers() {
        viewModel.scrapList.observe(this) { markers ->
            if (markers.isEmpty())
                showScarpSnackBar("스크랩한 식당이 없습니다")
            else
                showScarpSnackBar("N개의 스크랩 식당이 있습니다")
            markerList.forEach {
                it.first.map = null
            }
            markerList = markers.map { marker ->
                Pair(
                    Marker().apply {
                        position = LatLng(marker.latitude, marker.longitude)
                        icon = OverlayImage.fromResource(
                            if (marker.isDietRestaurant) R.drawable.ic_marker_green_small
                            else R.drawable.ic_marker_red_small
                        )
                        map = naverMap

                        isHideCollidedCaptions = true

                        captionText = marker.name

                        setOnClickListener {
                            with(viewModel) {
                                setRestaurantId(marker.id)
                                getReviewCheck(marker.id)
                                fetchSelectedRestaurantDetailInfo(
                                    marker.id,
                                    locationSource.lastLocation?.latitude ?: marker.latitude,
                                    locationSource.lastLocation?.longitude ?: marker.longitude
                                )
                            }
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

        viewModel.location.observe(this) { markers ->
            markerList.forEach {
                it.first.map = null
            }
            markerList = markers.map { marker ->
                Pair(
                    Marker().apply {
                        position = LatLng(marker.latitude, marker.longitude)
                        icon = OverlayImage.fromResource(
                            if (marker.isDietRestaurant) R.drawable.ic_marker_green_small
                            else R.drawable.ic_marker_red_small
                        )
                        map = naverMap

                        captionText = marker.name

                        isHideCollidedCaptions = true

                        setOnClickListener {
                            with(viewModel) {
                                setRestaurantId(marker.id)
                                getReviewCheck(marker.id)
                                fetchSelectedRestaurantDetailInfo(
                                    marker.id,
                                    locationSource.lastLocation?.latitude ?: marker.latitude,
                                    locationSource.lastLocation?.longitude ?: marker.longitude
                                )
                            }

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
            this.locationSource = this@MainActivity.locationSource
            naverMap.locationTrackingMode = LocationTrackingMode.Follow

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
        viewModel.getMapInfo(naverMap.cameraPosition.target)
        binding.btnLocationMain.setOnClickListener {
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
        const val MARKER_INFO = "MARKER_INFO"
        const val GANGNAM_X = 37.498095
        const val GANGNAM_Y = 127.027610
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
