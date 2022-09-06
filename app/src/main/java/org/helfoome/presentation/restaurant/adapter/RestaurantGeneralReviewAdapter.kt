package org.helfoome.presentation.restaurant.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.helfoome.databinding.ItemGeneralReviewBinding
import org.helfoome.domain.entity.HFMReviewInfo
import org.helfoome.presentation.type.HashtagViewType
import org.helfoome.util.ItemDecorationUtil
import org.helfoome.util.ItemDiffCallback

class RestaurantGeneralReviewAdapter :
    ListAdapter<HFMReviewInfo, RestaurantGeneralReviewAdapter.ReviewViewHolder>(
        ItemDiffCallback<HFMReviewInfo>(
            onContentsTheSame = { old, new -> old == new },
            onItemsTheSame = { old, new -> old.id == new.id }
        )
    ) {
    private lateinit var inflater: LayoutInflater

    class ReviewViewHolder(
        private val binding: ItemGeneralReviewBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: HFMReviewInfo) {
            binding.review = review
            binding.hashtag.setHashtag(review.tags, HashtagViewType.REVIEW_TAB_TYPE)
            val adapter = RestaurantImageAdapter().apply {
                imageList = review.photoList
            }
            binding.rvPhotoList.apply {
                this.adapter = adapter
                addItemDecoration(ItemDecorationUtil.ItemDecoration(padding = 20, isVertical = false))
            }
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
