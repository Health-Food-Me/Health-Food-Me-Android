package org.helfoome.presentation.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.helfoome.databinding.ItemPhotoSlideBinding
import org.helfoome.util.ItemDiffCallback

class PhotoSlideAdapter() : ListAdapter<String, PhotoSlideAdapter.PhotoSlideViewHolder>(
    ItemDiffCallback<String>(
        onContentsTheSame = { old, new -> old == new },
        onItemsTheSame = { old, new -> old == new }
    )
) {
    private lateinit var inflater: LayoutInflater

    class PhotoSlideViewHolder(private val binding: ItemPhotoSlideBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String) {
            binding.ivPhoto.load(imageUrl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoSlideViewHolder {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(parent.context)

        val binding =
            ItemPhotoSlideBinding.inflate(inflater, parent, false)
        return PhotoSlideViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: PhotoSlideViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }
}
