package org.helfoome.presentation.review

import android.os.Bundle
import android.widget.ScrollView
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.ActivityReviewWritingBinding
import org.helfoome.util.binding.BindingActivity

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
}
