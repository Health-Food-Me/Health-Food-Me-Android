package org.helfoome.presentation.drawer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.helfoome.databinding.ItemGeneralMyReviewBinding
import org.helfoome.domain.entity.MyReviewListInfo
import org.helfoome.presentation.restaurant.adapter.RestaurantImageAdapter
import org.helfoome.presentation.type.HashtagViewType
import org.helfoome.util.ItemDecorationUtil
import org.helfoome.util.ItemDiffCallback

class MyReviewAdapter(
    private val itemClickListener: ((Int) -> Unit),
    private val deleteClickListener: (String) -> Unit,
    private val editClickListener: (MyReviewListInfo) -> Unit
) :
    ListAdapter<MyReviewListInfo, MyReviewAdapter.MyReviewViewHolder>(
        ItemDiffCallback<MyReviewListInfo>(
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
            myReviewData: MyReviewListInfo,
            startRestaurant: () -> Unit,
            deleteClickListener: (String) -> Unit,
            editClickListener: (MyReviewListInfo) -> Unit
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
                binding.hashtag.setHashtag(listOf(myReviewData.tags, myReviewData.good.joinToString()), HashtagViewType.REVIEW_TAB_TYPE)
            }
        }
    }
}
