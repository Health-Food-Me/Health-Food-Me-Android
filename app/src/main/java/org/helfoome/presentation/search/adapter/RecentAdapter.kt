package org.helfoome.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.helfoome.databinding.ItemRecentWordBinding
import org.helfoome.domain.entity.RecentSearchInfo
import org.helfoome.util.ItemDiffCallback

class RecentAdapter : ListAdapter<RecentSearchInfo, RecentAdapter.RecentViewHolder>(
    ItemDiffCallback<RecentSearchInfo>(
        onContentsTheSame = { old, new -> old == new },
        onItemsTheSame = { old, new -> old.id == new.id }
    )
) {
    private lateinit var inflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(parent.context)

        val binding = ItemRecentWordBinding.inflate(inflater, parent, false)
        return RecentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class RecentViewHolder(private val binding: ItemRecentWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: RecentSearchInfo) {
            binding.data = data
        }
    }
}
