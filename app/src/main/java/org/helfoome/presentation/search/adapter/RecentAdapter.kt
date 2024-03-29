package org.helfoome.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.helfoome.databinding.ItemRecentWordBinding
import org.helfoome.domain.entity.RecentSearchInfo
import org.helfoome.util.ItemDiffCallback

class RecentAdapter(
    private val keywordClickListener: ((String, Boolean) -> Unit),
    private val deleteButtonClickListener: ((String) -> Unit)
) :
    ListAdapter<RecentSearchInfo, RecentAdapter.RecentViewHolder>(
        ItemDiffCallback<RecentSearchInfo>(
            onContentsTheSame = { old, new -> old == new },
            onItemsTheSame = { old, new -> old.keyword == new.keyword }
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
        holder.onBind(keywordClickListener, deleteButtonClickListener, getItem(position))
    }

    class RecentViewHolder(private val binding: ItemRecentWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(
            keywordClickListener: (String, Boolean) -> Unit,
            deleteButtonClickListener: (String) -> Unit,
            recentSearchData: RecentSearchInfo
        ) {
            with(binding) {
                with(recentSearchData) {
                    data = this
                    btnDelete.setOnClickListener {
                        deleteButtonClickListener.invoke(keyword)
                    }
                    tvRecentWord.setOnClickListener {
                        keywordClickListener.invoke(keyword, isCategory)
                    }
                }
            }
        }
    }
}
