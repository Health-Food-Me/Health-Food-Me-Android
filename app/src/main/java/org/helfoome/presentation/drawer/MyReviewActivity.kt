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
import org.helfoome.databinding.DialogMyReviewDeleteBinding
import org.helfoome.domain.entity.MyReviewListInfo
import org.helfoome.presentation.MainActivity
import org.helfoome.presentation.drawer.adapter.MyReviewAdapter
import org.helfoome.presentation.review.ReviewWritingActivity
import org.helfoome.util.DialogUtil
import org.helfoome.util.ItemDecorationUtil
import org.helfoome.util.ResolutionMetrics
import org.helfoome.util.SnackBarTopDown
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.getScreenSize
import org.helfoome.util.ext.startActivity
import javax.inject.Inject

@AndroidEntryPoint
class MyReviewActivity : BindingActivity<ActivityMyReviewBinding>(R.layout.activity_my_review) {
    @Inject
    lateinit var resolutionMetrics: ResolutionMetrics
    private val viewModel by viewModels<MyReviewViewModel>()

    private val requestModifyReview =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                SnackBarTopDown.makeSnackBarTopDown(this, binding.snvReviewModify, "리뷰 편집이 완료되었습니다")
                viewModel.getMyReviewList()
            }
        }

    private val myReviewAdapter = MyReviewAdapter(
        ::startRestaurant, ::deleteReview, ::editReview
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getMyReviewList()

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

    private fun startRestaurant() {
        startActivity<MainActivity>()
    }

    private fun deleteReview(reviewId: String) {
        val bind = DialogMyReviewDeleteBinding.inflate(layoutInflater)
        val dialog = DialogUtil.makeDialog(
            this,
            bind,
            resolutionMetrics.toPixel(getScreenSize(72).first),
            resolutionMetrics.toPixel(getScreenSize(417).second)
        )
        bind.btnYes.setOnClickListener {
            viewModel.deleteReview(reviewId)
            dialog.dismiss()
        }
        bind.btnNo.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun editReview(review: MyReviewListInfo) {
        requestModifyReview.launch(Intent(this@MyReviewActivity, ReviewWritingActivity::class.java).apply {
            putExtra(ARG_REVIEW_INFO, review)
        })
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
                    resolutionMetrics.toDP(1),
                    resolutionMetrics.toDP(20),
                    context.getColor(R.color.gray_100),
                    26
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
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    companion object {
        private const val ARG_REVIEW_INFO = "reviewInfo"
    }
}
