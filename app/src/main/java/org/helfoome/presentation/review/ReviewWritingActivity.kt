package org.helfoome.presentation.review

import android.Manifest

import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ScrollView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import org.helfoome.R
import org.helfoome.databinding.ActivityReviewWritingBinding
import org.helfoome.presentation.type.ReviewImageType
import org.helfoome.util.ItemDecorationUtil
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.closeKeyboard

@AndroidEntryPoint
class ReviewWritingActivity : BindingActivity<ActivityReviewWritingBinding>(R.layout.activity_review_writing) {
    private val viewModel: RestaurantReviewWritingViewModel by viewModels()
    private val galleryImageAdapter = GalleryImageAdapter(::showGalleryImageDialog)

    private val requestStorage =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                TedImagePicker.with(this)
                    .showCameraTile(false)
                    .max(3, "사진첨부는 최대 3장만 가능합니다")
                    .startMultiImage { uriList -> viewModel.setSelectedGalleryImages(uriList) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        initView()
        initListeners()
        initObservers()
    }

    private fun initView() {
        binding.rvPhotoList.apply {
            adapter = galleryImageAdapter
            addItemDecoration(ItemDecorationUtil.ItemDecoration(0f, padding = 18 , isVertical = false))
        }
    }

    private fun initListeners() {
        binding.etReview.setOnClickListener {
            binding.layoutScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.etReview.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.layoutScrollView.smoothScrollBy(0, 1200)
            }
        }
    }

    private fun initObservers() {
        viewModel.selectedImageList.observe(this) {
            galleryImageAdapter.imageList = listOf(null) + it
        }
    }

    private fun showGalleryImageDialog() {
        GalleryBottomDialogFragment(::setOnclickListener).show(supportFragmentManager, "GalleryBottomDialogFragment")
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

    private fun setOnclickListener(reviewImageType: ReviewImageType) {
        when (reviewImageType) {
            ReviewImageType.GALLERY -> {
                requestStorage.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            ReviewImageType.PHOTO_SHOOT -> {
                // TODO
            }
        }
    }
}
