package org.helfoome.presentation.common

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.ActivityImageViewerBinding
import org.helfoome.util.binding.BindingActivity

@AndroidEntryPoint
class ImageViewerActivity : BindingActivity<ActivityImageViewerBinding>(R.layout.activity_image_viewer) {
    private val adapter = PhotoSlideAdapter()
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.getStringArrayExtra(ARG_IMAGE_LIST)?.let {
            adapter.submitList(it.toMutableList())
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
    }

    private fun setPosition(smoothScroll: Boolean) {
        val indicator = "${position + 1}/${adapter.itemCount}"
        binding.page.text = indicator
        binding.vpPhotoList.setCurrentItem(position, smoothScroll)
    }

    companion object {
        private const val ARG_IMAGE_LIST = "imageList"
    }
}
