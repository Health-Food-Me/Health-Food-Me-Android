package org.helfoome.presentation.review

import android.Manifest
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ScrollView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.util.ToastUtil.context
import org.helfoome.R
import org.helfoome.databinding.ActivityReviewWritingBinding
import org.helfoome.domain.entity.MyReviewListInfo
import org.helfoome.presentation.type.ReviewImageType
import org.helfoome.util.HashtagUtil
import org.helfoome.util.ItemDecorationUtil
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.closeKeyboard
import org.helfoome.util.showToast
import java.io.File

@AndroidEntryPoint
class ReviewWritingActivity : BindingActivity<ActivityReviewWritingBinding>(R.layout.activity_review_writing) {
    private val viewModel: RestaurantReviewWritingViewModel by viewModels()
    private val galleryImageAdapter = GalleryImageAdapter(::showGalleryImageDialog)
    private var photoUri: Uri? = null
    private var hashtagUtil = HashtagUtil(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        initData()
        initView()
        initListeners()
        initObservers()
    }

    private fun initData() {
        // 새 리뷰 작성 시에는 레스토랑명 및 아이디를 전달 받음
        intent.getStringExtra(ARG_RESTAURANT_NAME)?.let {
            viewModel.setEditMode(false)
            viewModel.setRestaurantName(it)
        }
        intent.getStringExtra(ARG_RESTAURANT_ID)?.let {
            viewModel.setRestaurantId(it)
        }

        // 리뷰 편집 시 기존 리뷰 내용 전달 받기
        intent.getParcelableExtra<MyReviewListInfo>(ARG_REVIEW_INFO)?.let { reviewInfo ->
            viewModel.setEditMode(true)
            binding.ratingBar.rating = reviewInfo.score
            viewModel.setReviewInfo(reviewInfo, hashtagUtil.convertStrToTasteTag(reviewInfo.tags), reviewInfo.good.map { hashtagUtil.convertStrToGoodTag(it) })
            galleryImageAdapter.setUriList(reviewInfo.photoList.map { Uri.parse(it.url) })
        }
    }

    private fun initView() {
        binding.rvPhotoList.apply {
            adapter = galleryImageAdapter
            addItemDecoration(ItemDecorationUtil.ItemDecoration(0f, padding = 24, isVertical = false))
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
        binding.btnWriteReview.setOnClickListener {
            viewModel.checkReviewCompletion()
        }
    }

    private fun initObservers() {
        viewModel.isEnabledWritingCompleteButton.observe(this) { isEnabled ->
            if (isEnabled) {
                setResult(Activity.RESULT_OK)
                viewModel.uploadReview(context, binding.ratingBar.rating, galleryImageAdapter.imageList)
            } else {
                showToast(getString(R.string.review_writing_complete_condition_toast_text))
            }
        }
        viewModel.isCompletedReviewUpload.observe(this) {
            if (it) onBackPressed()
        }
        viewModel.isReviewModify.observe(this) {
            if (it) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun getFullImageCount(): Boolean {
        val isFull = galleryImageAdapter.getNumOfSelectableImages() <= 0
        if (isFull) showToast(getString(R.string.review_writing_image_upload_toast_text))
        return isFull
    }

    private fun showGalleryImageDialog() {
        if (getFullImageCount()) return
        GalleryBottomDialogFragment(::setOnclickListener).show(supportFragmentManager, "GalleryBottomDialogFragment")
    }

    private fun setOnclickListener(reviewImageType: ReviewImageType) {
        when (reviewImageType) {
            ReviewImageType.GALLERY -> requestStorage.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            ReviewImageType.PHOTO_SHOOT -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
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

    private fun openCamera() {
        val photoFile = File.createTempFile("IMG_", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES))
        photoUri = FileProvider.getUriForFile(this, "$packageName.provider", photoFile)
        cameraLauncher.launch(photoUri)
    }

    private val requestStorage =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                TedImagePicker.with(this)
                    .showCameraTile(false)
                    .max(galleryImageAdapter.getNumOfSelectableImages(), getString(R.string.review_writing_image_upload_toast_text))
                    .startMultiImage { uriList ->
                        galleryImageAdapter.setUriList(uriList)
                    }
            }
        }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            }
        }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess)
            galleryImageAdapter.setUriList(listOf(photoUri))
    }

    companion object {
        private const val ARG_RESTAURANT_ID = "restaurantId"
        private const val ARG_REVIEW_INFO = "reviewInfo"
        private const val ARG_RESTAURANT_NAME = "restaurantName"
    }
}
