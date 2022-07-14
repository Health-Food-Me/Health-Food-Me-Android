package org.helfoome.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.helfoome.databinding.LayoutRecentWordBinding

class SearchRecentTopAdapter :
    RecyclerView.Adapter<SearchRecentTopAdapter.SearchRecentTopViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchRecentTopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutRecentWordBinding.inflate(inflater, parent, false)

        return SearchRecentTopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchRecentTopViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount() = 1

    class SearchRecentTopViewHolder(
        private val binding: LayoutRecentWordBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind() {}
    }
}
