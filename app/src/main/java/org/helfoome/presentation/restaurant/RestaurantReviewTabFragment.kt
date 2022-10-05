package org.helfoome.presentation.restaurant

import android.content.Intent
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
import org.helfoome.presentation.common.ImageViewerActivity
import org.helfoome.presentation.common.WebViewActivity
import org.helfoome.presentation.restaurant.adapter.RestaurantBlogReviewAdapter
import org.helfoome.presentation.restaurant.adapter.RestaurantGeneralReviewAdapter
import org.helfoome.presentation.type.ReviewType
import org.helfoome.util.ItemDecorationUtil
import org.helfoome.util.ResolutionMetrics
import org.helfoome.util.binding.BindingFragment
import javax.inject.Inject

@AndroidEntryPoint
class RestaurantReviewTabFragment : BindingFragment<FragmentReviewBinding>(R.layout.fragment_review) {
    @Inject
    lateinit var resolutionMetrics: ResolutionMetrics
    private val viewModel: MainViewModel by activityViewModels()
    private val restaurantGeneralReviewAdapter = RestaurantGeneralReviewAdapter(::moveToImageViewer)
    private val restaurantBlogReviewAdapter = RestaurantBlogReviewAdapter(::moveToBlog)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObservers()
    }

    private fun initView() {
        binding.reviewList.apply {
            adapter = restaurantGeneralReviewAdapter
            addItemDecoration(
                ItemDecorationUtil.ItemDecoration(
                    resolutionMetrics.toDP(3),
                    resolutionMetrics.toDP(100),
                    context.getColor(R.color.gray_100),
                    100
                )
            )
        }
        binding.layoutReviewTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.reviewList.adapter = restaurantGeneralReviewAdapter
                        viewModel.setReviewType(ReviewType.HFM_REVIEW)
                        viewModel.fetchHFMReviewList()
                    }
                    else -> {
                        binding.reviewList.adapter = restaurantBlogReviewAdapter
                        viewModel.setReviewType(ReviewType.BLOG_REVIEW)
                        viewModel.fetchBlogReviewList()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun initObservers() {
        viewModel.hfmReviews.observe(viewLifecycleOwner) { reviews ->
            reviews?.let {
                restaurantGeneralReviewAdapter.submitList(it)
                showReviewEmptyView(it.isEmpty())
            }
            binding.layoutReviewTab.selectTab(binding.layoutReviewTab.getTabAt(0))
        }
        viewModel.blogReviews.observe(viewLifecycleOwner) { reviews ->
            reviews.isNullOrEmpty().let { isEmpty ->
                showReviewEmptyView(isEmpty)
                if (isEmpty) return@observe
            }

            restaurantBlogReviewAdapter.submitList(reviews.toMutableList())
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
        if (!Patterns.WEB_URL.matcher(review.url).matches()) return
        startActivity(
            Intent(requireContext(), WebViewActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                putExtra(ARG_WEB_VIEW_LINK, review.url)
            }
        )
    }

    private fun moveToImageViewer(reviewImageList: List<String>, position: Int) {
        Intent(requireContext(), ImageViewerActivity::class.java).apply {
            putExtra(ARG_IMAGE_LIST, reviewImageList.toTypedArray())
            putExtra(ARG_IMAGE_POSITION, position)
        }.also {
            startActivity(it)
        }
    }

    companion object {
        private const val ARG_WEB_VIEW_LINK = "link"
        private const val ARG_IMAGE_LIST = "imageList"
        private const val ARG_IMAGE_POSITION = "imagePosition"
    }
}
