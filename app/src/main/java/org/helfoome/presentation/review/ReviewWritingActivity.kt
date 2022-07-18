package org.helfoome.presentation.review

import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ScrollView
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.ActivityReviewWritingBinding
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.closeKeyboard

@AndroidEntryPoint
class ReviewWritingActivity : BindingActivity<ActivityReviewWritingBinding>(R.layout.activity_review_writing) {
    private val viewModel: RestaurantReviewWritingViewModel by viewModels()
    private val galleryImageAdapter = GalleryImageAdapter(::showGalleryImageDialog)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        initView()
        initListeners()
    }

    private fun initView() {
        binding.rvPhotoList.adapter = galleryImageAdapter.apply {
            imageList = listOf("")
        }
    }

    private fun initListeners() {
        binding.etReview.setOnClickListener {
            binding.layoutScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showGalleryImageDialog() {
        GalleryBottomDialogFragment().show(supportFragmentManager, "GalleryBottomDialogFragment")
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus

        if (view != null && ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE && view is EditText && !view.javaClass.name.startsWith(
                "android.webkit."
            )
        ) {
            val locationList = IntArray(2)
            view.getLocationOnScreen(locationList)
            val x = ev.rawX + view.left - locationList[0]
            val y = ev.rawY + view.top - locationList[1]
            if (x < view.left || x > view.right || y < view.top || y > view.bottom) {
                closeKeyboard(view)
                view.clearFocus()
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}
