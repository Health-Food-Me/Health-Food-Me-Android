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
    private val _imageList = mutableListOf<Uri?>()

    var imageList: List<Uri?> = _imageList
        set(value) {
            _imageList.clear()
            _imageList.addAll(value)
            notifyDataSetChanged()
        }

    init {
        // 0번 인덱스에 커메라 아이템을 넣기 위함
        _imageList.add(null)
        notifyItemInserted(0)
    }

    class GalleryImageViewHolder(private val binding: ItemGalleryImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: Uri?, position: Int, deleteImage: (Int) -> Unit) {
            binding.ivImage.load(imageUrl)
            binding.btnDelete.setOnClickListener {
                deleteImage(position)
            }
        }
    }

    class CameraViewHolder(private val binding: ItemCameraBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cameraOnClickListener: () -> Unit, imageCount: Int) {
            // imageCount - 1 : 카메라 아이콘을 제외한 순수 이미지 갯수를 계산하기 위함
            binding.tvImageCount.text = String.format(binding.tvImageCount.getString(R.string.format_image_count), imageCount - 1)
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
            is GalleryImageViewHolder -> viewHolder.bind(imageList[position], position, ::deleteImage)
            is CameraViewHolder -> viewHolder.bind(cameraOnClickListener, _imageList.size)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> GalleryImageViewType.CAMERA_VIEW_TYPE
            else -> GalleryImageViewType.GALLERY_VIEW_TYPE
        }.ordinal
    }

    override fun getItemCount(): Int = imageList.size

    private fun deleteImage(position: Int) {
        _imageList.removeAt(position)
        notifyDataSetChanged()
    }

    enum class GalleryImageViewType {
        GALLERY_VIEW_TYPE, CAMERA_VIEW_TYPE
    }
}
