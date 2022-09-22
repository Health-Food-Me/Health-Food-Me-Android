package org.helfoome.presentation.detail

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.helfoome.R
import org.helfoome.databinding.FragmentRestaurantDetailBinding
import org.helfoome.presentation.MainViewModel
import org.helfoome.presentation.login.GuestLoginFragmentDialog
import org.helfoome.presentation.restaurant.MapSelectionBottomDialogFragment
import org.helfoome.presentation.restaurant.adapter.RestaurantTabAdapter
import org.helfoome.presentation.review.ReviewWritingActivity
import org.helfoome.presentation.type.HashtagViewType
import org.helfoome.util.binding.BindingFragment

class RestaurantDetailFragment : BindingFragment<FragmentRestaurantDetailBinding>(R.layout.fragment_restaurant_detail) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private var selectedRestaurantId: String = ""
    private var mapSelectionBottomDialog: MapSelectionBottomDialogFragment? = null

    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) = Unit
        override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
        override fun onTabSelected(tab: TabLayout.Tab?) {
            // 리뷰 탭에서만 리뷰 작성 버튼 보여주기
            mainViewModel.setReviewTab(tab?.position == 2)
        }
    }

    private val requestReviewWrite =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                mainViewModel.fetchHFMReviewList()
                mainViewModel.getReviewCheck(mainViewModel.restaurantId.value ?: "")
                mainViewModel.setReviewWriteSuccess(true)
                val data = activityResult.data ?: return@registerForActivityResult
            }
        }

    private val appbarOffsetListener = AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
        binding.tvRestaurantNameInToolbar.visibility = if (verticalOffset == 0) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    private lateinit var restaurantDetailAdapter: RestaurantTabAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restaurantDetailAdapter = RestaurantTabAdapter(requireActivity())
        initView()
        initListeners()
        observeData()
    }

    private fun initView() {
        with(binding) {
            viewModel = mainViewModel
            binding.nvDetail.isNestedScrollingEnabled = false
            binding.viewModel = viewModel

            vpRestaurantDetail.adapter = restaurantDetailAdapter
            TabLayoutMediator(layoutRestaurantTabMenu, vpRestaurantDetail) { tab, position ->
                tab.text = resources.getStringArray(R.array.restaurant_detail_tab_titles)[position]
                binding.btnWriteReview.visibility = if (position == 2) View.VISIBLE else View.INVISIBLE
            }.attach()

            tvNumber.paintFlags = tvNumber.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            btnWriteReview.apply {
                setOnClickListener {
                    if (requireNotNull(viewModel).getIsGuestLogin()) {
                        supportGuestLogin()
                    } else {
                        requestReviewWrite.launch(
                            Intent(requireActivity(), ReviewWritingActivity::class.java)
                                .putExtra(ARG_RESTAURANT_ID, viewModel?.selectedRestaurant?.value?.id ?: return@setOnClickListener)
                                .putExtra(ARG_RESTAURANT_NAME, binding.tvRestaurantName.text.toString())
                        )
                    }
                }
            }
        }
    }

    private fun initListeners() {
        with(binding) {
            layoutAppBar.setOnClickListener {
                mainViewModel.setBehaviorState(BottomSheetBehavior.STATE_EXPANDED)
            }
            btnScrapToolbar.setOnClickListener {
                if (requireNotNull(viewModel).getIsGuestLogin()) {
                    supportGuestLogin()
                } else {
                    viewModel?.updateRestaurantScrap()
                }
            }

            btnScrap.setOnClickListener {
                if (requireNotNull(viewModel).getIsGuestLogin()) {
                    supportGuestLogin()
                } else {
                    viewModel?.updateRestaurantScrap()
                }
            }

            btnBack.setOnClickListener {
                mainViewModel.setBehaviorState(BottomSheetBehavior.STATE_COLLAPSED)
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
        mapSelectionBottomDialog = MapSelectionBottomDialogFragment().apply {
//            locationSource.lastLocation?.let {
//                viewModel.setCurrentLocationPoint(it.latitude, it.longitude)
//            }
        }

        mapSelectionBottomDialog?.show(parentFragmentManager, "MapSelectionBottomDialogFragment")
    }

    private fun supportGuestLogin() {
        GuestLoginFragmentDialog().show(
            parentFragmentManager, "GuestLoginDialog"
        )
    }

    private fun observeData() {
        mainViewModel.selectedRestaurant.observe(viewLifecycleOwner) {
            with(binding) {
                // 스크랩 시 selectedRestaurant의 스크랩 상태 isScrap을 업데이트하면서 selectedRestaurant가 갱신됨에 따라 스크랩 버튼만 눌러도 메뉴 탭으로 이동하는 버그를 방지하고자 함
                // TODO Config-Change에 따른 취약점 발생을 방지하고자 selectedRestaurantId 뷰모델에서 관리하도록 수정 필요
                if (selectedRestaurantId != it.id) {
                    selectedRestaurantId = it.id
                    layoutRestaurantTabMenu.selectTab(layoutRestaurantTabMenu.getTabAt(0))
                }
                hashtag.setHashtag(it.category, HashtagViewType.RESTAURANT_SUMMARY_TYPE)
            }
        }

        mainViewModel.isReviewTab.observe(viewLifecycleOwner) {
            binding.layoutReviewBtnBackground.visibility =
                if (it.peekContent()) View.VISIBLE else View.INVISIBLE
        }

        mainViewModel.checkReview.observe(viewLifecycleOwner) {
            if (mainViewModel.checkReview.value == false) {
                binding.btnWriteReview.isEnabled = true
            } else if (mainViewModel.checkReview.value == true) {
                binding.btnWriteReview.isEnabled = false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.layoutRestaurantTabMenu.addOnTabSelectedListener(tabSelectedListener)
        binding.layoutAppBar.addOnOffsetChangedListener(appbarOffsetListener)
    }

    override fun onStop() {
        super.onStop()
        binding.layoutRestaurantTabMenu.removeOnTabSelectedListener(tabSelectedListener)
        binding.layoutAppBar.removeOnOffsetChangedListener(appbarOffsetListener)
    }

    companion object {
        private const val ARG_RESTAURANT_ID = "restaurantId"
        private const val ARG_RESTAURANT_NAME = "restaurantName"
    }
}
