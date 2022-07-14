package org.helfoome.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.helfoome.databinding.ItemAutoCompleteWordBinding
import org.helfoome.domain.entity.AutoCompleteKeywordInfo
import org.helfoome.util.ItemDiffCallback

class AutoCompleteAdapter : ListAdapter<AutoCompleteKeywordInfo, AutoCompleteAdapter.AutoCompleteViewHolder>(
    ItemDiffCallback<AutoCompleteKeywordInfo>(
        onContentsTheSame = { old, new -> old == new },
        onItemsTheSame = { old, new -> old.id == new.id }
    )
) {
    private lateinit var inflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoCompleteViewHolder {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(parent.context)

        val binding = ItemAutoCompleteWordBinding.inflate(inflater, parent, false)
        return AutoCompleteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AutoCompleteViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class AutoCompleteViewHolder(private val binding: ItemAutoCompleteWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: AutoCompleteKeywordInfo) {
            binding.data = data
        }
    }
}
