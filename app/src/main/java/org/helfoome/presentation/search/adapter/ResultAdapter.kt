package org.helfoome.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.helfoome.databinding.ItemSearchResultBinding
import org.helfoome.domain.entity.SearchResultInfo
import org.helfoome.util.ItemDiffCallback

class ResultAdapter(private val cardClickListener: ((String, Double, Double) -> Unit)) : ListAdapter<SearchResultInfo, ResultAdapter.ResultViewHolder>(
    ItemDiffCallback<SearchResultInfo>(
        onContentsTheSame = { old, new -> old == new },
        onItemsTheSame = { old, new -> old.id == new.id }
    )
) {
    private lateinit var inflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(parent.context)

        val binding = ItemSearchResultBinding.inflate(inflater, parent, false)
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.onBind(cardClickListener, getItem(position))
    }

    class ResultViewHolder(private val binding: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(cardClickListener: (String, Double, Double) -> Unit, data: SearchResultInfo) {
            binding.data = data
            binding.layoutCard.setOnClickListener {
                cardClickListener.invoke(data.id, data.latitude, data.longitude)
            }
        }
    }
}
