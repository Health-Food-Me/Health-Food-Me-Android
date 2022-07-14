package org.helfoome.presentation.restaurant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.helfoome.databinding.ItemGeneralReviewBinding
import org.helfoome.domain.entity.ReviewInfo
import org.helfoome.util.ItemDiffCallback

class RestaurantGeneralReviewAdapter :
    ListAdapter<ReviewInfo, RestaurantGeneralReviewAdapter.ReviewViewHolder>(ItemDiffCallback<ReviewInfo>(
        onContentsTheSame = { old, new -> old == new },
        onItemsTheSame = { old, new -> old.id == new.id }
    )) {
    private lateinit var inflater: LayoutInflater

    class ReviewViewHolder(private val binding: ItemGeneralReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: ReviewInfo) {
            binding.review = menu
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(parent.context)

        val binding =
            ItemGeneralReviewBinding.inflate(inflater, parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ReviewViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }
}
