package org.helfoome.presentation.drawer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.helfoome.databinding.ItemGeneralMyReviewBinding
import org.helfoome.domain.entity.MyReviewInfo
import org.helfoome.util.ItemDiffCallback

class MyReviewAdapter(private val itemClickListener: ((Int) -> Unit)) : ListAdapter<MyReviewInfo, MyReviewAdapter.MyReviewViewHolder>(
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
        return MyReviewViewHolder(
            binding.apply {
                tvEdit.setOnClickListener {
                    itemClickListener.invoke(EDIT)
                }
                tvNickname.setOnClickListener {
                    itemClickListener.invoke(ENLARGE)
                }
                tvDelete.setOnClickListener {
                    itemClickListener.invoke(DELETE)
                }
            }
        )
    }

    override fun onBindViewHolder(holder: MyReviewViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class MyReviewViewHolder(private val binding: ItemGeneralMyReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(myReviewData: MyReviewInfo) {
            with(binding) {
                data = myReviewData
            }
        }
    }

    companion object {
        const val EDIT = 0
        const val ENLARGE = 1
        const val DELETE = 2
    }
}
