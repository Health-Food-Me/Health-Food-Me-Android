package org.helfoome.presentation.restaurant.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.helfoome.databinding.ItemReviewImageBinding
import org.helfoome.domain.entity.ReviewImage

class RestaurantImageAdapter(private val itemClickListener: (List<String>, Int) -> Unit) :
    RecyclerView.Adapter<RestaurantImageAdapter.ReviewImageViewHolder>() {
    private lateinit var inflater: LayoutInflater
    private val _imageList = mutableListOf<ReviewImage>()
    var imageList: List<ReviewImage> = _imageList
        set(value) {
            _imageList.clear()
            _imageList.addAll(value)
            notifyDataSetChanged()
        }

    class ReviewImageViewHolder(private val binding: ItemReviewImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String, images: List<String>, position: Int, itemClickListener: (List<String>, Int) -> Unit) {
            binding.ivImage.load(imageUrl)
            binding.root.setOnClickListener {
                itemClickListener(images, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewImageViewHolder {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(parent.context)

        val binding =
            ItemReviewImageBinding.inflate(inflater, parent, false)
        return ReviewImageViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ReviewImageViewHolder, position: Int) {
        viewHolder.bind(imageList[position].url, imageList.map { it.url }, position, itemClickListener)
    }

    override fun getItemCount(): Int = imageList.size
}
