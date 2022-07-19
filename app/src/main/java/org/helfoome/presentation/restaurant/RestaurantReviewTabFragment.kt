package org.helfoome.presentation.restaurant

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.FragmentReviewBinding
import org.helfoome.presentation.MainViewModel
import org.helfoome.presentation.restaurant.adapter.RestaurantBlogReviewAdapter
import org.helfoome.presentation.restaurant.adapter.RestaurantGeneralReviewAdapter
import org.helfoome.util.ItemDecorationUtil
import org.helfoome.util.binding.BindingFragment

@AndroidEntryPoint
class RestaurantReviewTabFragment : BindingFragment<FragmentReviewBinding>(R.layout.fragment_review) {
    private val viewModel: MainViewModel by activityViewModels()
    private val restaurantGeneralReviewAdapter = RestaurantGeneralReviewAdapter()
    private val restaurantBlogReviewAdapter = RestaurantBlogReviewAdapter()
    private val listener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            // index가 아닌 enum class로 비교하기
            viewModel.setGeneralReview(tab?.position == 0)
        }
    }

    override fun onStart() {
        super.onStart()
        binding.layoutReviewTab.addOnTabSelectedListener(listener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchReviewList()
        initView()
        initObservers()
    }

    private fun initView() {
        binding.reviewList.apply {
            adapter = restaurantGeneralReviewAdapter
            addItemDecoration(ItemDecorationUtil.ItemDecoration(1f, 100f, context.getColor(R.color.gray_500), 100))
        }
        binding.layoutReviewTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.fetchReviewList()
                    else -> viewModel.fetchBlogReviewList()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun initObservers() {
        viewModel.hfmReviews.observe(viewLifecycleOwner) { reviews ->
            binding.reviewList.adapter = restaurantGeneralReviewAdapter
            restaurantGeneralReviewAdapter.submitList(reviews)
        }
        viewModel.blogReviews.observe(viewLifecycleOwner) { reviews ->
            binding.reviewList.adapter = restaurantBlogReviewAdapter
            restaurantBlogReviewAdapter.submitList(reviews)
        }
    }

    override fun onStop() {
        super.onStop()
        binding.layoutReviewTab.removeOnTabSelectedListener(listener)
    }
}
