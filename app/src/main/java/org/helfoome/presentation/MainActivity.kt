package org.helfoome.presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kakao.sdk.user.UserApiClient
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.databinding.ActivityMainBinding
import org.helfoome.databinding.DialogLogoutBinding
import org.helfoome.presentation.drawer.MyReviewActivity
import org.helfoome.presentation.drawer.ProfileModifyActivity
import org.helfoome.presentation.drawer.SettingActivity
import org.helfoome.presentation.login.LoginActivity
import org.helfoome.presentation.restaurant.MapSelectionBottomDialogFragment
import org.helfoome.presentation.restaurant.adapter.RestaurantTabAdapter
import org.helfoome.presentation.review.ReviewWritingActivity
import org.helfoome.presentation.scrap.MyScrapActivity
import org.helfoome.presentation.search.SearchActivity
import org.helfoome.presentation.type.FoodType
import org.helfoome.presentation.type.HashtagViewType
import org.helfoome.util.ChipFactory
import org.helfoome.util.DialogUtil
import org.helfoome.util.ResolutionMetrics
import org.helfoome.util.SnackBarTopDown
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.makeTransparentStatusBar
import org.helfoome.util.ext.startActivity
import org.helfoome.util.ext.stringListFrom
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main), OnMapReadyCallback {
    @Inject
    lateinit var resolutionMetrics: ResolutionMetrics
    private val viewModel: MainViewModel by viewModels()
    private var category: String? = null
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
            if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                binding.isFloatingNotVisible = false
            if (newState == BottomSheetBehavior.STATE_DRAGGING)
                binding.isFloatingNotVisible = true
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                binding.isMainNotVisible = false
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
            }
        }

    private val controlHamburger =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                binding.layoutDrawer.close()
            }
        }

    private val requestReviewWrite =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                viewModel.fetchHFMReviewList()
                SnackBarTopDown.makeSnackBarTopDown(this, binding.snvProfileModify, "리뷰가 작성되었습니다")
                val data = activityResult.data ?: return@registerForActivityResult
            }
        }

    private val restaurantDetailAdapter = RestaurantTabAdapter(this)
    private val String.toChip: Chip
        get() = ChipFactory.create(layoutInflater).also { it.text = this }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.layoutDrawerHeader.drawerViewModel = viewModel

        window.makeTransparentStatusBar()

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        initNaverMap()
        initView()
        initListeners()
        initObservers()
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
        binding.layoutRestaurantDialog.layoutRestaurantTabMenu.addOnTabSelectedListener(tabSelectedListener)
        binding.layoutRestaurantDialog.layoutAppBar.addOnOffsetChangedListener(appbarOffsetListener)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getProfile()
        viewModel.getReviewCheck(viewModel.restaurantId.value ?: "")
    }

    override fun onStop() {
        super.onStop()
        behavior.removeBottomSheetCallback(bottomSheetCallback)
        binding.layoutRestaurantDialog.layoutRestaurantTabMenu.removeOnTabSelectedListener(tabSelectedListener)
        binding.layoutRestaurantDialog.layoutAppBar.removeOnOffsetChangedListener(appbarOffsetListener)
    }

    private fun initView() {
        behavior = BottomSheetBehavior.from(binding.layoutBottomSheet)
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.layoutDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

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
                        Intent(this@MainActivity, ReviewWritingActivity::class.java)
                            .putExtra(ARG_RESTAURANT_ID, viewModel?.selectedRestaurant?.value?.id ?: return@setOnClickListener)
                            .putExtra("RESTAURANT_NAME", binding.layoutRestaurantDialog.tvRestaurantName.text.toString())
                    )
                }
            }
        }
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
        binding.btnBookmark.setOnClickListener {
            it.isSelected = !it.isSelected
            startScrapEvent(it.isSelected)
        }

        binding.btnBookmarkMain.setOnClickListener {
            it.isSelected = !it.isSelected
            startScrapEvent(it.isSelected)
        }

        binding.btnHamburger.setOnClickListener {
            viewModel.getProfile()
            binding.layoutDrawer.open()
        }

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

            binding.layoutSearch.setOnClickListener {
                viewModel?.location?.value?.let {
                    startActivity<SearchActivity>(Pair(MARKER_INFO, it))
                }
            }

            btnNavi.setOnClickListener {
                showMapSelectionBottomDialog()
            }
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
            tvReview.setOnClickListener {
                controlHamburger.launch(Intent(this@MainActivity, MyReviewActivity::class.java))
            }
            tvScrap.setOnClickListener {
                controlHamburger.launch(Intent(this@MainActivity, MyScrapActivity::class.java))
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
                val bind = DialogLogoutBinding.inflate(LayoutInflater.from(this@MainActivity))
                val dialog =
                    DialogUtil.makeDialog(this@MainActivity, bind, resolutionMetrics.toPixel(288), resolutionMetrics.toPixel(241))

                bind.btnYes.setOnClickListener {
                    NaverIdLoginSDK.logout()
                    UserApiClient.instance.logout { error ->
                        if (error != null) {
                            Timber.e(error, "로그아웃 실패. SDK에서 토큰 삭제됨")
                        } else {
                            Timber.i("로그아웃 성공. SDK에서 토큰 삭제됨")
                        }
                    }
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
                bind.btnNo.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }

    private fun showScarpSnackBar() {
        val view = layoutInflater.inflate(R.layout.view_snackbar, null)
        view.findViewById<TextView>(R.id.tv_snackBar).text = "스크랩한 식당이 없습니다"
        val snackbar = Snackbar.make(binding.layoutMain, "", 1000)
        val layout = snackbar.view as Snackbar.SnackbarLayout
        layout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        layout.setPadding(56, 0, 56, 10)
        layout.addView(view)
        snackbar.animationMode = ANIMATION_MODE_SLIDE
        snackbar.show()
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

    private fun showMapSelectionBottomDialog() {
        if (mapSelectionBottomDialog?.isAdded == true) return
        mapSelectionBottomDialog = MapSelectionBottomDialogFragment().apply {
            locationSource.lastLocation?.let {
                viewModel.setCurrentLocationPoint(it.latitude, it.longitude)
            }
        }

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

        viewModel.scrapList.observe(this) { markers ->
            if (markers.isEmpty())
                showScarpSnackBar()
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
        binding.btnLocation.setOnClickListener {
            naverMap.cameraPosition =
                CameraPosition(LatLng(GANGNAM_X, GANGNAM_Y), 14.0)
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
        private const val ARG_RESTAURANT_ID = "restaurantId"
    }
}
