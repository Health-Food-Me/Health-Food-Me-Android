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
import org.helfoome.presentation.alert.AlertFragmentDialog
import org.helfoome.presentation.alert.ConfirmFragmentDialog
import org.helfoome.presentation.detail.RestaurantDetailFragment
import org.helfoome.presentation.drawer.MyReviewActivity
import org.helfoome.presentation.drawer.ProfileModifyActivity
import org.helfoome.presentation.drawer.SettingActivity
import org.helfoome.presentation.login.GuestLoginFragmentDialog
import org.helfoome.presentation.login.LoginActivity
import org.helfoome.presentation.scrap.MyScrapActivity
import org.helfoome.presentation.search.SearchActivity
import org.helfoome.presentation.type.AlertType
import org.helfoome.presentation.type.ConfirmType
import org.helfoome.presentation.type.FoodType
import org.helfoome.util.ChipFactory
import org.helfoome.util.ResolutionMetrics
import org.helfoome.util.SnackBarTopDown
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.replace
import org.helfoome.util.ext.startActivity
import org.helfoome.util.ext.stringListFrom
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main), OnMapReadyCallback {
    @Inject
    lateinit var resolutionMetrics: ResolutionMetrics

    private var checkedChip: String? = null
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
            viewModel.setIsDetailCollapsed(newState == BottomSheetBehavior.STATE_COLLAPSED)
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
            } else {
                binding.layoutDrawer.close()
                naverMap.cameraPosition = CameraPosition(LatLng(EOUNJU_X, EOUNJU_Y), 12.0)
            }
        }

    private val String.toChip: Chip
        get() = ChipFactory.create(layoutInflater).also {
            it.text = this
            it.elevation = 10F
        }

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
    }

    override fun onResume() {
        super.onResume()
        viewModel.setIsGuestLogin()
    }

    private fun provideChipClickListener(chip: Chip) =
        View.OnClickListener {
            if (!chip.isChecked) {
                checkedChip = null
                binding.cgFoodTag.clearCheck()
                if (!binding.btnBookmarkMain.isSelected)
                    viewModel.getMapInfo(naverMap.cameraPosition.target)
                else
                    viewModel.getScrapList()
            } else {
                checkedChip = chip.text.toString()
                binding.cgFoodTag.clearCheck()
                chip.isChecked = true
                if (!binding.btnBookmarkMain.isSelected)
                    viewModel.getMapInfo(naverMap.cameraPosition.target, chip.text.toString())
                else
                    viewModel.getScrapList(checkedChip)
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        binding.layoutDrawer.close()
    }

    private fun startScrapEvent(isSelected: Boolean) {
        if (isSelected) {
            viewModel.getScrapList(checkedChip)
        } else {
            checkedChip = null
            binding.cgFoodTag.clearCheck()
            viewModel.getMapInfo(naverMap.cameraPosition.target)
        }
    }

    private fun initListeners() {
        binding.btnBookmarkMain.setOnClickListener {
            if (!viewModel.getIsLogin()) {
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
            tvGuestLogin.setOnClickListener {
                startActivity<LoginActivity>()
            }
            ivLogin.setOnClickListener {
                startActivity<LoginActivity>()
            }
            tvReview.setOnClickListener {
                if (!viewModel.getIsLogin()) {
                    supportGuestLogin()
                } else {
                    controlHamburger.launch(
                        Intent(this@MainActivity, MyReviewActivity::class.java).apply {
                            putExtras(
                                bundleOf(
                                    LATITUDE to locationSource.lastLocation?.latitude,
                                    LONGITUDE to locationSource.lastLocation?.longitude
                                )
                            )
                        }
                    )
                }
            }
            tvScrap.setOnClickListener {
                if (!viewModel.getIsLogin()) {
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
                sendGmail(R.string.main_drawer_report, R.string.mail_content_restuarant_report)
            }
            tvModifyReport.setOnClickListener {
                sendGmail(R.string.main_drawer_modify_report, R.string.mail_content_modify_report)
            }
            tvSetting.setOnClickListener {
                controlHamburger.launch(Intent(this@MainActivity, SettingActivity::class.java))
            }
            tvLogout.setOnClickListener {
                AlertFragmentDialog.newInstance(AlertType.LOGOUT).show(supportFragmentManager, AlertFragmentDialog.TAG)
            }
        }
    }

    private fun supportGuestLogin() {
        GuestLoginFragmentDialog().show(supportFragmentManager, "GuestLoginDialog")
    }

    private fun showScarpSnackBar(snackBarText: String, padding: Int) {
        val view = layoutInflater.inflate(R.layout.view_snackbar, null)
        view.findViewById<TextView>(R.id.tv_snackBar).text = snackBarText
        val snackBar = Snackbar.make(binding.layoutMain, snackBarText, 1000)
        with(snackBar.view as Snackbar.SnackbarLayout) {
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.transparent))
            setPadding(padding, 0, padding, 10)
            addView(view)
        }
        with(snackBar) {
            animationMode = ANIMATION_MODE_SLIDE
            show()
        }
    }

    // TODO need refactoring 설정 화면에서도 동일 함수 사용됨. 함수 분리 필요
    private fun sendGmail(titleResId: Int, contentResId: Int) {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO,
            Uri.fromParts(
                "mailto", getString(R.string.healfoome_mail), null
            )
        )
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(titleResId))
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(contentResId))
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    private fun initObservers() {
        viewModel.scrapList.observe(this) { markers ->
            if (markers.isEmpty()) {
                showScarpSnackBar("스크랩한 식당이 없습니다", 36)
                markerList.forEach {
                    it.first.map = null
                }
            } else {
                showScarpSnackBar("${markers.size}개의 스크랩 식당이 있습니다", 36)
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

                            isHideCollidedMarkers = true
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
                                    setSelectedLocationPoint(marker.latitude, marker.longitude)
                                }

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
                                setSelectedLocationPoint(marker.latitude, marker.longitude)
                            }

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
            naverMap.locationTrackingMode = LocationTrackingMode.NoFollow

            cameraPosition = if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                CameraPosition(LatLng(EOUNJU_X, EOUNJU_Y), 12.0)
            } else {
                CameraPosition(LatLng(EOUNJU_X, EOUNJU_Y), 12.0)
            }

            addOnCameraIdleListener {
                if (!binding.btnBookmarkMain.isSelected)
                    viewModel.getMapInfo(naverMap.cameraPosition.target, checkedChip)
            }
        }
        viewModel.getMapInfo(naverMap.cameraPosition.target)
        binding.btnLocationMain.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                naverMap.cameraPosition = CameraPosition(
                    LatLng(
                        locationSource.lastLocation?.latitude ?: EOUNJU_X,
                        locationSource.lastLocation?.longitude ?: EOUNJU_Y
                    ),
                    14.0
                )
            } else {
                ConfirmFragmentDialog.newInstance(ConfirmType.LOCATION_CONFIRM).show(supportFragmentManager, ConfirmFragmentDialog.TAG)
            }
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
        const val EOUNJU_X = 37.507317
        const val EOUNJU_Y = 127.033943
        const val GANGNAM_X = 37.498095
        const val GANGNAM_Y = 127.027610
        const val GO_EOUNJU = 100
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
