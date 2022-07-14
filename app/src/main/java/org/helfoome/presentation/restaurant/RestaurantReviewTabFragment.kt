package org.helfoome.presentation.restaurant

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import org.helfoome.R
import org.helfoome.databinding.FragmentReviewBinding
import org.helfoome.util.binding.BindingFragment

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
        binding.reviewList.adapter = restaurantGeneralReviewAdapter
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
