package org.helfoome.presentation.restaurant

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.FragmentReviewBinding
import org.helfoome.domain.entity.BlogReviewInfo
import org.helfoome.presentation.MainViewModel
import org.helfoome.presentation.restaurant.adapter.RestaurantBlogReviewAdapter
import org.helfoome.presentation.restaurant.adapter.RestaurantGeneralReviewAdapter
import org.helfoome.presentation.type.ReviewType
import org.helfoome.util.ItemDecorationUtil
import org.helfoome.util.binding.BindingFragment

@AndroidEntryPoint
class RestaurantReviewTabFragment : BindingFragment<FragmentReviewBinding>(R.layout.fragment_review) {
    private val viewModel: MainViewModel by activityViewModels()
    private val restaurantGeneralReviewAdapter = RestaurantGeneralReviewAdapter()
    private val restaurantBlogReviewAdapter = RestaurantBlogReviewAdapter(::moveToBlog)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchHFMReviewList()
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
                    0 -> viewModel.setReviewType(ReviewType.HFM_REVIEW) // viewModel.fetchHFMReviewList()
                    else -> viewModel.setReviewType(ReviewType.BLOG_REVIEW) // viewModel.fetchBlogReviewList()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun initObservers() {
        viewModel.reviewType.observe(viewLifecycleOwner) { reviewType ->
            when (reviewType.peekContent()) {
                ReviewType.HFM_REVIEW -> binding.reviewList.adapter = restaurantGeneralReviewAdapter
                ReviewType.BLOG_REVIEW -> binding.reviewList.adapter = restaurantBlogReviewAdapter
            }
        }

        viewModel.hfmReviews.observe(viewLifecycleOwner) { reviews ->
            restaurantGeneralReviewAdapter.submitList(reviews)
            showReviewEmptyView(reviews.isEmpty())
        }
        viewModel.blogReviews.observe(viewLifecycleOwner) { reviews ->
            restaurantBlogReviewAdapter.submitList(reviews)
            showReviewEmptyView(reviews.isEmpty())
        }
    }

    private fun showReviewEmptyView(isShown: Boolean) {
        if (isShown) {
            binding.reviewList.visibility = View.INVISIBLE
            binding.layoutEmptyView.layoutContainer.visibility = View.VISIBLE
        } else {
            binding.reviewList.visibility = View.VISIBLE
            binding.layoutEmptyView.layoutContainer.visibility = View.INVISIBLE
        }
    }

    private fun moveToBlog(review: BlogReviewInfo) {
        if (Patterns.WEB_URL.matcher(review.url).matches()) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(review.url)))
                return
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
    }
}
