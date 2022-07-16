package org.helfoome.presentation.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.helfoome.databinding.ItemCameraBinding
import org.helfoome.databinding.ItemGalleryImageBinding

class GalleryImageAdapter(private val cameraOnClickListener: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var inflater: LayoutInflater
    private val _imageList = mutableListOf<String>()
    var imageList: List<String> = _imageList
        set(value) {
            _imageList.clear()
            _imageList.addAll(value)
            notifyDataSetChanged()
        }

    class GalleryImageViewHolder(private val binding: ItemGalleryImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String) {
            binding.ivImage.load(imageUrl)
        }
    }

    class CameraViewHolder(private val binding: ItemCameraBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cameraOnClickListener: () -> Unit) {
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
            is GalleryImageViewHolder -> viewHolder.bind(imageList[position])
            is CameraViewHolder -> viewHolder.bind(cameraOnClickListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> GalleryImageViewType.CAMERA_VIEW_TYPE
            else -> GalleryImageViewType.GALLERY_VIEW_TYPE
        }.ordinal
    }

    override fun getItemCount(): Int = imageList.size

    enum class GalleryImageViewType {
        GALLERY_VIEW_TYPE, CAMERA_VIEW_TYPE
    }
}
