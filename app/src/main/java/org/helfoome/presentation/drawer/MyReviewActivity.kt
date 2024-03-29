package org.helfoome.presentation.drawer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.ActivityMyReviewBinding
import org.helfoome.domain.entity.MyReviewInfo
import org.helfoome.presentation.MainActivity.Companion.EOUNJU_X
import org.helfoome.presentation.MainActivity.Companion.EOUNJU_Y
import org.helfoome.presentation.MainActivity.Companion.GO_EOUNJU
import org.helfoome.presentation.MainActivity.Companion.LATITUDE
import org.helfoome.presentation.MainActivity.Companion.LONGITUDE
import org.helfoome.presentation.MainViewModel
import org.helfoome.presentation.common.ImageViewerActivity
import org.helfoome.presentation.detail.RestaurantDetailFragment
import org.helfoome.presentation.drawer.adapter.MyReviewAdapter
import org.helfoome.presentation.review.ReviewWritingActivity
import org.helfoome.util.ItemDecorationUtil
import org.helfoome.util.ResolutionMetrics
import org.helfoome.util.SnackBarTopDown
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.replace
import javax.inject.Inject

@AndroidEntryPoint
class MyReviewActivity : BindingActivity<ActivityMyReviewBinding>(R.layout.activity_my_review) {
    @Inject
    lateinit var resolutionMetrics: ResolutionMetrics
    private val viewModel by viewModels<MyReviewViewModel>()
    private val mainViewModel: MainViewModel by viewModels()

    private val requestModifyReview =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                SnackBarTopDown.makeSnackBarTopDown(this, binding.snvReviewModify, "리뷰 편집이 완료되었습니다")
                viewModel.getMyReviewList()
            }
        }

    private val myReviewAdapter = MyReviewAdapter(
        ::startRestaurant, ::deleteReview, ::editReview, ::moveToImageViewer
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getMyReviewList()
        // 리뷰 아이템에서 레스토랑 명 클릭 시 상세화면으로 넘어갈 때 상단 회색 라인이 보이지 않도록 하기 위함. 해당 속성으로 상단 회색 라인의 visibility 조절하고 있기 때문
        mainViewModel.setExpendedBottomSheetDialog(true)

        initAdapter()
        initObservers()
        initListeners()
    }

    private fun initObservers() {
        viewModel.myReviewInfo.observe(this) {
            binding.isEmptyVisible = it.isEmpty()
            myReviewAdapter.submitList(it)
        }
    }

    private fun startRestaurant(restaurantId: String) {
        with(mainViewModel) {
            setIsReviewActivity(true)
            setRestaurantId(restaurantId)
            getReviewCheck(restaurantId)
            with(intent) {
                fetchSelectedRestaurantDetailInfo(
                    restaurantId,
                    getDoubleExtra(LATITUDE, EOUNJU_X),
                    getDoubleExtra(LONGITUDE, EOUNJU_Y)
                )
            }
        }
        replace<RestaurantDetailFragment>(R.id.fragment_container_detail)
    }

    private fun deleteReview(reviewId: String) {
        ReviewDeleteFragmentDialog(reviewId).show(supportFragmentManager, "ReviewDeleteDialog")
    }

    private fun editReview(review: MyReviewInfo) {
        requestModifyReview.launch(
            Intent(this@MyReviewActivity, ReviewWritingActivity::class.java).apply {
                putExtra(ARG_REVIEW_INFO, review)
            }
        )
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        setResult(Activity.RESULT_OK)
        return super.onKeyDown(keyCode, event)
    }

    private fun initAdapter() {
        binding.rcvReview.apply {
            adapter = myReviewAdapter
            addItemDecoration(
                ItemDecorationUtil.ItemDecoration(
                    resolutionMetrics.toDP(10),
                    resolutionMetrics.toDP(20),
                    context.getColor(R.color.gray_100),
                    0
                )
            )
        }
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
        binding.btnGoToStore.setOnClickListener {
            setResult(GO_EOUNJU)
            finish()
        }
    }

    private fun moveToImageViewer(menuBoardList: List<String>, position: Int) {
        Intent(this, ImageViewerActivity::class.java).apply {
            putExtra(ARG_IMAGE_LIST, menuBoardList.toTypedArray())
            putExtra(ARG_IMAGE_POSITION, position)
        }.also {
            startActivity(it)
        }
    }

    companion object {
        private const val ARG_REVIEW_INFO = "reviewInfo"
        private const val ARG_IMAGE_LIST = "imageList"
        private const val ARG_IMAGE_POSITION = "imagePosition"
    }
}
