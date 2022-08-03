package org.helfoome.presentation.drawer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.ActivityMyReviewBinding
import org.helfoome.databinding.DialogMyReviewDeleteBinding
import org.helfoome.presentation.MainActivity
import org.helfoome.presentation.drawer.adapter.MyReviewAdapter
import org.helfoome.presentation.drawer.adapter.MyReviewAdapter.Companion.ENLARGE
import org.helfoome.presentation.review.ReviewWritingActivity
import org.helfoome.util.DialogUtil
import org.helfoome.util.ItemDecorationUtil
import org.helfoome.util.ResolutionMetrics
import org.helfoome.util.SnackBarTopDown
import org.helfoome.util.binding.BindingActivity
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
            }
        }

    private val myReviewAdapter = MyReviewAdapter(
        ::adapterClickListener, { reviewId ->
            val bind = DialogMyReviewDeleteBinding.inflate(LayoutInflater.from(this@MyReviewActivity))
            val dialog = DialogUtil.makeDialog(this, bind, resolutionMetrics.toPixel(288), resolutionMetrics.toPixel(223))

            bind.btnYes.setOnClickListener {
                viewModel.deleteReview(reviewId)
                dialog.dismiss()
            }
            bind.btnNo.setOnClickListener {
                dialog.dismiss()
            }
        },
        { reviewId ->
            val intent = Intent(this@MyReviewActivity, ReviewWritingActivity::class.java)
            intent.putExtra("REVIEW_ID", reviewId)
            intent.putExtra("REVIEW_TITLE", true)
            requestModifyReview.launch(intent)
        }
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

    private fun adapterClickListener(it: Int) {
        when (it) {
            ENLARGE -> {
                startActivity<MainActivity>()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        setResult(Activity.RESULT_OK)
        return super.onKeyDown(keyCode, event)
    }

    private fun initAdapter() {
        binding.rcvReview.apply {
            adapter = myReviewAdapter
            addItemDecoration(ItemDecorationUtil.ItemDecoration(3f, 100f, context.getColor(R.color.gray_100), 100))
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

    // 나중에 전체적으로 바꾸지 않게 하기
    override fun onResume() {
        super.onResume()
        viewModel.getMyReviewList()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getMyReviewList()
    }
}
