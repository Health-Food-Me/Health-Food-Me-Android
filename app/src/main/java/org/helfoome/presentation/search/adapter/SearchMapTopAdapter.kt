package org.helfoome.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.helfoome.databinding.LayoutLookMapBinding

class SearchMapTopAdapter :
    RecyclerView.Adapter<SearchMapTopAdapter.SearchMapTopViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchMapTopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutLookMapBinding.inflate(inflater, parent, false)

        return SearchMapTopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchMapTopViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount() = 1

    class SearchMapTopViewHolder(
        private val binding: LayoutLookMapBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind() {}
    }
}
