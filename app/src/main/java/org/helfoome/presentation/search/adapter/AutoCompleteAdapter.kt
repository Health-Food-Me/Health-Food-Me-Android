package org.helfoome.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.helfoome.databinding.ItemAutoCompleteWordBinding
import org.helfoome.domain.entity.AutoCompleteKeywordInfo
import org.helfoome.util.ItemDiffCallback
import org.helfoome.util.ext.setAutoKeyword

class AutoCompleteAdapter(private val itemClickListener: (String, String) -> Unit) :
    ListAdapter<AutoCompleteKeywordInfo, AutoCompleteAdapter.AutoCompleteViewHolder>(
        ItemDiffCallback<AutoCompleteKeywordInfo>(
            onContentsTheSame = { old, new -> old == new },
            onItemsTheSame = { old, new -> old.id == new.id }
        )
    ) {
    private var setKeywordListener: (() -> String)? = null
    private lateinit var inflater: LayoutInflater

    fun setKeywordListener(listener: (() -> String)?) {
        setKeywordListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoCompleteViewHolder {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(parent.context)

        val binding = ItemAutoCompleteWordBinding.inflate(inflater, parent, false)
        return AutoCompleteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AutoCompleteViewHolder, position: Int) {
        holder.onBind(getItem(position), setKeywordListener, itemClickListener)
    }

    class AutoCompleteViewHolder(private val binding: ItemAutoCompleteWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: AutoCompleteKeywordInfo, setKeywordListener: (() -> String)?, itemClickListener: (String, String) -> Unit) {
            with(binding) {
                this.data = data
                tvKeyword.setAutoKeyword(data.name, setKeywordListener?.invoke())
                root.setOnClickListener {
                    itemClickListener.invoke(data.name, data.id)
                }
            }
        }
    }
}
