package org.helfoome.presentation.restaurant.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.helfoome.databinding.ItemReviewImageBinding

class RestaurantImageAdapter :
    RecyclerView.Adapter<RestaurantImageAdapter.ReviewImageViewHolder>() {
    private lateinit var inflater: LayoutInflater
    private val _imageList = mutableListOf<String>()
    var imageList: List<String> = _imageList
        set(value) {
            _imageList.clear()
            _imageList.addAll(value)
            notifyDataSetChanged()
        }

    class ReviewImageViewHolder(private val binding: ItemReviewImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String) {
            Log.i("TAG", "bind: $imageUrl")
            binding.ivImage.load(imageUrl)
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
        viewHolder.bind(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size
}
