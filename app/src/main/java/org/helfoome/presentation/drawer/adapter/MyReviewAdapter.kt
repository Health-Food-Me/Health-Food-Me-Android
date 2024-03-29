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
    private val startRestaurant: (String) -> Unit,
    private val deleteReview: (String) -> Unit,
    private val editReview: (MyReviewInfo) -> Unit,
    private val itemClickListener: (List<String>, Int) -> Unit,
) :
    ListAdapter<MyReviewInfo, MyReviewAdapter.MyReviewViewHolder>(
        ItemDiffCallback<MyReviewInfo>(
            onContentsTheSame = { old, new -> old == new },
            onItemsTheSame = { old, new -> old.id == new.id }
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
        holder.onBind(getItem(position), startRestaurant, deleteReview, editReview, itemClickListener)
    }

    class MyReviewViewHolder(private val binding: ItemGeneralMyReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(
            myReviewData: MyReviewInfo,
            startRestaurant: (String) -> Unit,
            deleteClickListener: (String) -> Unit,
            editClickListener: (MyReviewInfo) -> Unit,
            itemClickListener: (List<String>, Int) -> Unit,
        ) {
            with(binding) {
                val adapter = RestaurantImageAdapter(itemClickListener).apply {
                    imageList = myReviewData.photoList
                }
                rvPhotoList.apply {
                    this.adapter = adapter
                    addItemDecoration(ItemDecorationUtil.ItemDecoration(padding = 20, isVertical = false))
                }
                tvTitle.setOnClickListener {
                    startRestaurant(myReviewData.restaurantId)
                }
                data = myReviewData
                tvDelete.setOnClickListener {
                    deleteClickListener(myReviewData.id)
                }
                tvEdit.setOnClickListener {
                    editClickListener(myReviewData)
                }
                binding.hashtag.setHashtag(listOf(myReviewData.taste) + myReviewData.good, HashtagViewType.REVIEW_TAB_TYPE)
            }
        }
    }
}
