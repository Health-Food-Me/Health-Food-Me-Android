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
    private val itemClickListener: (Int) -> Unit,
    private val deleteClickListener: (String) -> Unit,
    private val editClickListener: (String) -> Unit
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
        return MyReviewViewHolder(
            binding.apply {
                tvTitle.setOnClickListener {
                    itemClickListener.invoke(ENLARGE)
                }
            }
        )
    }

    override fun onBindViewHolder(holder: MyReviewViewHolder, position: Int) {
        holder.onBind(getItem(position), deleteClickListener, editClickListener)
    }

    class MyReviewViewHolder(private val binding: ItemGeneralMyReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(
            myReviewData: MyReviewListInfo,
            deleteClickListener: (String) -> Unit,
            editClickListener: (String) -> Unit
        ) {
            with(binding) {
                val adapter = RestaurantImageAdapter().apply {
                    imageList = myReviewData.photoList
                }
                binding.rvPhotoList.apply {
                    this.adapter = adapter
                    addItemDecoration(ItemDecorationUtil.ItemDecoration(height = 0f, padding = 20, isVertical = false))
                }
                data = myReviewData
                tvDelete.setOnClickListener {
                    deleteClickListener.invoke(myReviewData.id)
                }
                tvEdit.setOnClickListener {
                    editClickListener.invoke(myReviewData.id)
                }
                binding.hashtag.setHashtag(listOf(myReviewData.tags, myReviewData.good.joinToString()), HashtagViewType.REVIEW_TAB_TYPE)
            }
        }
    }

    companion object {
        const val ENLARGE = 0
    }
}
