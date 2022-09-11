package org.helfoome.presentation.drawer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.helfoome.databinding.ItemGeneralMyReviewBinding
import org.helfoome.domain.entity.MyReviewInfo
import org.helfoome.presentation.restaurant.adapter.RestaurantImageAdapter
import org.helfoome.presentation.type.HashtagViewType
import org.helfoome.util.ItemDecorationUtil
import org.helfoome.util.ItemDiffCallback

class MyReviewAdapter(
    private val startRestaurant: (() -> Unit),
    private val deleteReview: (String) -> Unit,
    private val editReview: (MyReviewInfo) -> Unit,
) :
    ListAdapter<MyReviewInfo, MyReviewAdapter.MyReviewViewHolder>(
        ItemDiffCallback<MyReviewInfo>(
            onContentsTheSame = { old, new -> old == new },
            onItemsTheSame = { old, new -> old.restaurant == new.restaurant }
        )
    ) {
    private lateinit var inflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyReviewViewHolder {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(parent.context)

        val binding = ItemGeneralMyReviewBinding.inflate(inflater, parent, false)
        return MyReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyReviewViewHolder, position: Int) {
        holder.onBind(getItem(position), startRestaurant, deleteReview, editReview)
    }

    class MyReviewViewHolder(private val binding: ItemGeneralMyReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(
            myReviewData: MyReviewInfo,
            startRestaurant: () -> Unit,
            deleteClickListener: (String) -> Unit,
            editClickListener: (MyReviewInfo) -> Unit,
        ) {
            with(binding) {
                val adapter = RestaurantImageAdapter().apply {
                    imageList = myReviewData.photoList
                }
                rvPhotoList.apply {
                    this.adapter = adapter
                    addItemDecoration(ItemDecorationUtil.ItemDecoration(padding = 20, isVertical = false))
                }
                tvTitle.setOnClickListener {
                    startRestaurant.invoke()
                }
                data = myReviewData
                tvDelete.setOnClickListener {
                    deleteClickListener.invoke(myReviewData.id)
                }
                tvEdit.setOnClickListener {
                    editClickListener.invoke(myReviewData)
                }
                binding.hashtag.setHashtag(listOf(myReviewData.taste) + myReviewData.good, HashtagViewType.REVIEW_TAB_TYPE)
            }
        }
    }
}
