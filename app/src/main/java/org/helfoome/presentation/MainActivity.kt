package org.helfoome.presentation

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
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
import org.helfoome.databinding.LogoutDialogBinding
import org.helfoome.presentation.drawer.MyReviewActivity
import org.helfoome.presentation.scrap.MyScrapActivity
import org.helfoome.presentation.drawer.ProfileModifyActivity
import org.helfoome.presentation.drawer.SettingActivity
import org.helfoome.presentation.restaurant.MapSelectionBottomDialogFragment
import org.helfoome.presentation.restaurant.adapter.RestaurantTabAdapter
import org.helfoome.presentation.review.ReviewWritingActivity
import org.helfoome.presentation.search.SearchActivity
import org.helfoome.presentation.type.FoodType
import org.helfoome.util.ChipFactory
import org.helfoome.util.DialogUtil
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.stringListFrom
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main), OnMapReadyCallback {
    private val String.toChip: Chip
        get() = ChipFactory.create(layoutInflater).also { it.text = this }
    private val viewModel: MainViewModel by viewModels()
    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private val restaurantDetailAdapter = RestaurantTabAdapter(this)
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private var mapSelectionBottomDialog: MapSelectionBottomDialogFragment? = null
    private val listener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            // 리뷰 탭에서만 리뷰 작성 버튼 보여주기
            binding.layoutRestaurantDialog.btnWriteReview.visibility = if (tab?.position == 2) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        initNaverMapLocationSource()
        initNaverMap()
        initView()
        initChip()
        initListeners()
        initObservers()
    }

    private fun initNaverMapLocationSource() {
        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun provideChipClickListener(chip: Chip) =
        View.OnClickListener {
            if (!chip.isChecked)
                binding.cgFoodTag.clearCheck()
            else {
                binding.cgFoodTag.clearCheck()
                chip.isChecked = true
            }
        }

    private fun initChip() {
        for (i in FoodType.values().indices) {
            with(binding.cgFoodTag) {
                addView(
                    stringListFrom(R.array.main_chip_group)[i].toChip.apply {
                        setOnClickListener(provideChipClickListener(this))
                        setChipIconResource(FoodType.values()[i].icon)
                        setChipIconTintResource(FoodType.values()[i].iconTint)
                        setChipBackgroundColorResource(FoodType.values()[i].color)
                    }
                )
            }
        }
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

    override fun onStart() {
        super.onStart()
        behavior.addBottomSheetCallback(bottomSheetCallback)
        binding.layoutRestaurantDialog.layoutRestaurantTabMenu.addOnTabSelectedListener(listener)
    }

    private fun initView() {
        behavior = BottomSheetBehavior.from(binding.layoutBottomSheet)
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        with(binding.layoutRestaurantDialog) {
            vpRestaurantDetail.adapter = restaurantDetailAdapter
            TabLayoutMediator(layoutRestaurantTabMenu, vpRestaurantDetail) { tab, position ->
                tab.text = resources.getStringArray(R.array.restaurant_detail_tab_titles)[position]
                binding.layoutRestaurantDialog.btnWriteReview.visibility = if (position == 2) View.VISIBLE else View.INVISIBLE
            }.attach()

            tvNumber.paintFlags = tvNumber.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            btnWriteReview.apply {
                visibility = View.INVISIBLE
                setOnClickListener { startActivity(Intent(this@MainActivity, ReviewWritingActivity::class.java)) }
            }
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

            binding.layoutSearch.setOnClickListener {
                startActivity(Intent(this@MainActivity, SearchActivity::class.java))
            }

            with(binding) {
                btnHamburger.setOnClickListener {
                    layoutDrawer.open()
                }
            }

            btnNavi.setOnClickListener {
                showMapSelectionBottomDialog()
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
                tvSetting.setOnClickListener {
                    startActivity(Intent(this@MainActivity, SettingActivity::class.java))
                }
                tvLogout.setOnClickListener {
                    val bind = LogoutDialogBinding.inflate(LayoutInflater.from(this@MainActivity))
                    val dialog = DialogUtil.makeDialog(this@MainActivity, bind, 288, 241)

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
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.selectedRestaurant.value?.location
        }

        viewModel.location.observe(this) {
            it.map { location ->
                Marker().apply {
                    position = location
                    if (viewModel.isDietRestaurant.value == true) {
                        icon = OverlayImage.fromResource(R.drawable.ic_marker_green)
                    } else {
                        icon = OverlayImage.fromResource(R.drawable.ic_marker_red)
                    }
                    this.map = naverMap
                }
            }.forEach { marker ->
                marker.setOnClickListener {
                    if (viewModel.isDietRestaurant.value == true) {
                        marker.icon = OverlayImage.fromResource(R.drawable.ic_marker_green_big)
                    } else {
                        marker.icon = OverlayImage.fromResource(R.drawable.ic_marker_red_big)
                    }
                    viewModel.markerId(marker.position)?.let { id ->
//                        bottomsheet(id)
                    }
                    true
                }
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
        binding.layoutDrawer.closeDrawers()
        behavior.removeBottomSheetCallback(bottomSheetCallback)
        binding.layoutRestaurantDialog.layoutRestaurantTabMenu.removeOnTabSelectedListener(listener)
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

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap.apply {
            locationSource = locationSource
            cameraPosition = CameraPosition(LatLng(37.5666102, 126.9783881), 11.0)
            uiSettings.isZoomControlEnabled = false
            binding.fabLocation.setOnClickListener {
                locationTrackingMode = LocationTrackingMode.Follow
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
