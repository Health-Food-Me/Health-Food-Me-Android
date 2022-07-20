package org.helfoome.presentation.review

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.helfoome.R
import org.helfoome.databinding.ItemCameraBinding
import org.helfoome.databinding.ItemGalleryImageBinding
import org.helfoome.util.ext.getString

class GalleryImageAdapter(private val cameraOnClickListener: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var inflater: LayoutInflater
    private val imageList = mutableListOf<Uri?>()

    init {
        // 0번 인덱스에 커메라 아이템을 넣기 위함
        imageList.add(null)
        notifyItemInserted(0)
    }

    class GalleryImageViewHolder(private val binding: ItemGalleryImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: Uri?, deleteImage: (Uri) -> Unit) {
            binding.ivImage.load(imageUrl)
            binding.btnDelete.setOnClickListener {
                deleteImage(imageUrl ?: return@setOnClickListener)
            }
        }
    }

    class CameraViewHolder(private val binding: ItemCameraBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cameraOnClickListener: () -> Unit, currentImageCount: Int) {
            binding.tvImageCount.text = String.format(binding.tvImageCount.getString(R.string.format_image_count), currentImageCount)
            binding.layoutContainer.setOnClickListener {
                cameraOnClickListener()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            GalleryImageViewType.GALLERY_VIEW_TYPE.ordinal -> {
                val binding =
                    ItemGalleryImageBinding.inflate(inflater, parent, false)
                GalleryImageViewHolder(binding)
            }
            else -> {
                val binding =
                    ItemCameraBinding.inflate(inflater, parent, false)
                CameraViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is GalleryImageViewHolder -> viewHolder.bind(imageList[position], ::deleteImage)
            is CameraViewHolder -> viewHolder.bind(cameraOnClickListener, getCurrentImageCount())
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> GalleryImageViewType.CAMERA_VIEW_TYPE
            else -> GalleryImageViewType.GALLERY_VIEW_TYPE
        }.ordinal
    }

    override fun getItemCount(): Int = imageList.size

    private fun getCurrentImageCount(): Int = itemCount - 1

    fun getNumOfSelectableImages() = MAX_IMAGE_COUNT - getCurrentImageCount()

    fun setUriList(uriList: List<Uri?>) {
        val currentCount = imageList.size - 1
        if (currentCount + uriList.size > 3) return
        imageList.addAll(1, uriList)
        notifyDataSetChanged()
    }

    private fun deleteImage(url: Uri) {
        imageList.remove(url)
        notifyDataSetChanged()
    }

    enum class GalleryImageViewType {
        GALLERY_VIEW_TYPE, CAMERA_VIEW_TYPE
    }

    companion object {
        private const val MAX_IMAGE_COUNT = 3
    }
}
