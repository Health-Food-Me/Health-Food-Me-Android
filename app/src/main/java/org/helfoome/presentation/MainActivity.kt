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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
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
import org.helfoome.databinding.ActivityMainBinding
import org.helfoome.databinding.DialogLogoutBinding
import org.helfoome.presentation.drawer.MyReviewActivity
import org.helfoome.presentation.drawer.ProfileModifyActivity
import org.helfoome.presentation.drawer.SettingActivity
import org.helfoome.presentation.login.LoginActivity
import org.helfoome.presentation.restaurant.MapSelectionBottomDialogFragment
import org.helfoome.presentation.restaurant.adapter.RestaurantMenuAdapter
import org.helfoome.presentation.restaurant.adapter.RestaurantTabAdapter
import org.helfoome.presentation.review.ReviewWritingActivity
import org.helfoome.presentation.scrap.MyScrapActivity
import org.helfoome.presentation.search.SearchActivity
import org.helfoome.presentation.type.FoodType
import org.helfoome.presentation.type.HashtagViewType
import org.helfoome.util.ChipFactory
import org.helfoome.util.DialogUtil
import org.helfoome.util.ResolutionMetrics
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.stringListFrom
import org.helfoome.util.makeTransparentStatusBar
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
            if (newState == BottomSheetBehavior.STATE_HIDDEN)
                binding.isMainNotVisible = false
        }

        override fun onSlide(bottomSheetView: View, slideOffset: Float) {
        }
    }

    private val restaurantMenuAdapter = RestaurantMenuAdapter()
    private var markerList: List<Pair<Marker, Boolean>> = listOf()
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    private val requestModifyNickname =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val animation = AnimationUtils.loadAnimation(this, R.anim.anim_snackbar_top_down)
                binding.snvProfileModify.animation = animation
                binding.snvProfileModify.setText("닉네임이 변경되었습니다")
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) = Unit
                    override fun onAnimationEnd(animation: Animation?) {
                        val bottomTopAnimation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.anim_snackbar_bottom_top)
                        binding.snvProfileModify.animation = bottomTopAnimation
                        binding.snvProfileModify.setText("닉네임이 변경되었습니다")
                    }

                    override fun onAnimationRepeat(p0: Animation?) = Unit
                })
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
        viewModel.getProfile()

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
                setOnClickListener { startActivity(Intent(this@MainActivity, ReviewWritingActivity::class.java)) }
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

    override fun onResume() {
        super.onResume()
        viewModel.getProfile()
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.getProfile()
    }

    private fun initListeners() {
        binding.fabBookmark.setOnClickListener {
            it.isSelected = !it.isSelected
        }

        binding.fabBookmarkMain.setOnClickListener {
            it.isSelected = !it.isSelected
        }

        binding.btnHamburger.setOnClickListener {
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
                startActivity(Intent(this@MainActivity, SearchActivity::class.java))
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
            tvSetting.setOnClickListener {
                startActivity(Intent(this@MainActivity, SettingActivity::class.java))
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
        mapSelectionBottomDialog = MapSelectionBottomDialogFragment()
        mapSelectionBottomDialog?.show(supportFragmentManager, "MapSelectionBottomDialogFragment")
    }

    private fun initObservers() {
        viewModel.selectedRestaurant.observe(this) {
            with(binding.layoutRestaurantDialog) {
                hashtag.setHashtag(it.tags, HashtagViewType.RESTAURANT_SUMMARY_TYPE)
            }
        }

        viewModel.menu.observe(this) { menuList ->
            restaurantMenuAdapter.menuList = menuList
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
                        icon = OverlayImage.fromResource(
                            if (marker.isDietRestaurant) R.drawable.ic_marker_green_small
                            else R.drawable.ic_marker_red_small
                        )
                        map = naverMap

                        setOnClickListener {
                            viewModel.getReviewCheck(marker.id)
                            viewModel.fetchSelectedRestaurantDetailInfo(marker.id, marker.latitude, marker.longitude)

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
                markerList.forEach {
                    it.first.icon = OverlayImage.fromResource(
                        if (it.second) R.drawable.ic_marker_green_small
                        else R.drawable.ic_marker_red_small
                    )
                }
            }
            this.locationSource = this@MainActivity.locationSource

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                cameraPosition = CameraPosition(
                    LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude), 11.0)
                locationTrackingMode = LocationTrackingMode.Follow
            } else {
                cameraPosition = CameraPosition(LatLng(37.498095, 127.027610), 11.0)
            }
        }

        binding.fabLocation.setOnClickListener {
            naverMap.cameraPosition = CameraPosition(LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude), 11.0)
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
        }
        viewModel.getMapInfo(naverMap.cameraPosition.target, category)
        binding.fabLocationMain.setOnClickListener {
            naverMap.cameraPosition = CameraPosition(LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude), 11.0)
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
        }
        viewModel.getMapInfo(
            LatLng(
                naverMap.cameraPosition.target.latitude,
                naverMap.cameraPosition.target.longitude
            ),
            category
        )
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
