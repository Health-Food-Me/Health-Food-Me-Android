package org.helfoome.presentation.restaurant

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.FragmentReviewBinding
import org.helfoome.presentation.restaurant.adapter.RestaurantBlogReviewAdapter
import org.helfoome.presentation.restaurant.adapter.RestaurantGeneralReviewAdapter
import org.helfoome.presentation.restaurant.viewmodel.RestaurantReviewViewModel
import org.helfoome.util.ItemDecorationUtil
import org.helfoome.util.binding.BindingFragment

@AndroidEntryPoint
class RestaurantReviewTabFragment : BindingFragment<FragmentReviewBinding>(R.layout.fragment_review) {
    private val viewModel: RestaurantReviewViewModel by viewModels()
    private val restaurantGeneralReviewAdapter = RestaurantGeneralReviewAdapter()
    private val restaurantBlogReviewAdapter = RestaurantBlogReviewAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObservers()
    }

    private fun initView() {
        binding.reviewList.apply {
            adapter = restaurantGeneralReviewAdapter
            addItemDecoration(ItemDecorationUtil.ItemDecoration(1f, 72f, context.getColor(R.color.gray_500), 28))
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
        viewModel.reviews.observe(viewLifecycleOwner) { reviews ->
            binding.reviewList.adapter = restaurantGeneralReviewAdapter
            restaurantGeneralReviewAdapter.submitList(reviews)
        }
        viewModel.blogReviews.observe(viewLifecycleOwner) { reviews ->
            binding.reviewList.adapter = restaurantBlogReviewAdapter
            restaurantBlogReviewAdapter.submitList(reviews)
        }
    }
}
