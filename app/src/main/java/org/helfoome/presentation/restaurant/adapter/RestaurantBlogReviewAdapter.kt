package org.helfoome.presentation.restaurant.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.helfoome.databinding.ItemBlogReviewBinding
import org.helfoome.domain.entity.BlogReviewInfo
import org.helfoome.util.ItemDiffCallback

class RestaurantBlogReviewAdapter :
    ListAdapter<BlogReviewInfo, RestaurantBlogReviewAdapter.ReviewViewHolder>(ItemDiffCallback<BlogReviewInfo>(onContentsTheSame = { old, new -> old == new }, onItemsTheSame = { old, new -> old.id == new.id })) {
    private lateinit var inflater: LayoutInflater

    class ReviewViewHolder(private val binding: ItemBlogReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: BlogReviewInfo) {
            binding.review = menu
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(parent.context)

        val binding =
            ItemBlogReviewBinding.inflate(inflater, parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ReviewViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }
}
