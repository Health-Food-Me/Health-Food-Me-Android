package org.helfoome.presentation.review

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import org.helfoome.util.showToast
import java.io.IOException

@AndroidEntryPoint
class ReviewWritingActivity : BindingActivity<ActivityReviewWritingBinding>(R.layout.activity_review_writing) {
    private val viewModel: RestaurantReviewWritingViewModel by viewModels()
    private val galleryImageAdapter = GalleryImageAdapter(::showGalleryImageDialog)

    private val requestStorage =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                TedImagePicker.with(this)
                    .showCameraTile(false)
                    .max(galleryImageAdapter.getNumOfSelectableImages(), "사진첨부는 최대 3장만 가능합니다")
                    .startMultiImage { uriList ->
                        val bitmapList = uriList.map { uri -> uriToBitmap(uri) }
                        galleryImageAdapter.setBitmapList(bitmapList)
                    }
            }
        }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraLauncher.launch(intent)
            }
        }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.extras?.get("data")?.let { imageBitmap ->
                galleryImageAdapter.setBitmapList(listOf(imageBitmap as Bitmap))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        initView()
        initListeners()
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
    }

    private fun getFullImageCount(): Boolean {
        val isFull = galleryImageAdapter.getNumOfSelectableImages() <= 0 // galleryImageAdapter.itemCount > 3 //
        if (isFull) showToast("사진첨부는 최대 3장만 가능합니다")
        return isFull
    }

    private fun showGalleryImageDialog() {
        if (getFullImageCount()) return
        GalleryBottomDialogFragment(::setOnclickListener).show(supportFragmentManager, "GalleryBottomDialogFragment")
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
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
                "android.webkit.")
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
