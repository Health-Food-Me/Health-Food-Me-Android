package org.helfoome.presentation.scrap.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.helfoome.databinding.ItemScrapBinding
import org.helfoome.domain.entity.ScrapInfo
import org.helfoome.util.ItemDiffCallback

class MyScrapAdapter(private val itemClickListener: (String) -> Unit, private val bookmarkClickListener: (String) -> Unit) :
    ListAdapter<ScrapInfo, MyScrapAdapter.MyScrapViewHolder>(
        ItemDiffCallback<ScrapInfo>(
            onContentsTheSame = { old, new -> old == new },
            onItemsTheSame = { old, new -> old.id == new.id }
        )
    ) {
    private lateinit var inflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyScrapViewHolder {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(parent.context)

        val binding = ItemScrapBinding.inflate(inflater, parent, false)
        return MyScrapViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyScrapViewHolder, position: Int) {
        holder.onBind(getItem(position), itemClickListener, bookmarkClickListener)
    }

    class MyScrapViewHolder(private val binding: ItemScrapBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(scrapData: ScrapInfo, itemClickListener: (String) -> Unit, bookmarkClickListener: (String) -> Unit) {
            with(binding) {
                ivScrap.isSelected = scrapData.isBookmarked
                data = scrapData

                clScrap.setOnClickListener {
                    itemClickListener.invoke(scrapData.id)
                }

                ivScrap.setOnClickListener {
                    it.isSelected = !it.isSelected
                    bookmarkClickListener.invoke(scrapData.id)
                }
            }
        }
    }
}
