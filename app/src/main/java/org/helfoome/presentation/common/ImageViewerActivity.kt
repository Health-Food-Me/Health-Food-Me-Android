package org.helfoome.presentation.common

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.helfoome.R
import org.helfoome.databinding.ActivityImageViewerBinding
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.showToast
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

@AndroidEntryPoint
class ImageViewerActivity : BindingActivity<ActivityImageViewerBinding>(R.layout.activity_image_viewer) {
    private val adapter = PhotoSlideAdapter()
    private var position = 0
    private var imageList: List<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.let {
            it.getStringArrayExtra(ARG_IMAGE_LIST)?.let {
                imageList = it.toList()
                adapter.submitList(it.toMutableList())
            }
            it.getIntExtra(ARG_IMAGE_POSITION, 0).let { position = it }
        }

        initView()
        initListeners()
    }

    private fun initView() {
        binding.vpPhotoList.run {
            adapter = this@ImageViewerActivity.adapter
            offscreenPageLimit = 1
        }

        binding.vpPhotoList.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                this@ImageViewerActivity.position = position
                setPosition(true)
            }
        })

        setPosition(false)
    }

    private fun initListeners() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.tvSave.setOnClickListener {
            lifecycleScope.launch {
                val bitmap = getBitmapFromURL(imageList?.get(position) ?: return@launch)
                bitmap?.let { saveMediaToStorage(bitmap) }
            }
        }
    }

    private fun setPosition(smoothScroll: Boolean) {
        val indicator = "${position + 1}/${adapter.itemCount}"
        binding.page.text = indicator
        binding.vpPhotoList.setCurrentItem(position, smoothScroll)
    }

    private suspend fun getBitmapFromURL(src: String): Bitmap? {
        val bitmap: Bitmap?
        withContext(Dispatchers.IO) {
            bitmap = try {
                val url = URL(src)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

        return bitmap
    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            showToast("사진이 저장되었습니다")
        }
    }

    companion object {
        private const val ARG_IMAGE_LIST = "imageList"
        private const val ARG_IMAGE_POSITION = "imagePosition"
    }
}
