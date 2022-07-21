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
import org.helfoome.presentation.type.ReviewImageType
import org.helfoome.util.ItemDecorationUtil
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.closeKeyboard
import org.helfoome.util.showToast
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class ReviewWritingActivity : BindingActivity<ActivityReviewWritingBinding>(R.layout.activity_review_writing) {
    private val viewModel: RestaurantReviewWritingViewModel by viewModels()
    private val galleryImageAdapter = GalleryImageAdapter(::showGalleryImageDialog)
    private var photoUri: Uri? = null
    private var reviewId: String? = null
    private var topTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        initData()
        initView()
        initListeners()
        initObservers()
    }

    private fun initData() {
        Timber.d("${intent.getStringExtra("REVIEW_ID")}")
        Timber.d("${intent.getBooleanExtra("REVIEW_TITLE", false)}")
        viewModel.setReviewId(intent.getStringExtra("REVIEW_ID").toString())
        viewModel.setEditMode(intent.getBooleanExtra("REVIEW_TITLE", false))
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
                Timber.d("initObservers: 리뷰 작성 요건 충족")
                viewModel.uploadReview(context, binding.ratingBar.rating, galleryImageAdapter.imageList)
            } else {
                Timber.d("initObservers: 리뷰 작성 요건 충족 못함")
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
}
