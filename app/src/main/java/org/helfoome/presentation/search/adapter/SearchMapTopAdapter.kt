package org.helfoome.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.helfoome.databinding.LayoutLookMapBinding

class SearchMapTopAdapter(private val searchMapClickListener: (() -> Unit)) :
    RecyclerView.Adapter<SearchMapTopAdapter.SearchMapTopViewHolder>() {
    private var isCollapsed = false

    fun setVisible(isCollapsed: Boolean) {
        this.isCollapsed = isCollapsed
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchMapTopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutLookMapBinding.inflate(inflater, parent, false)

        binding.isCollapsed = isCollapsed

        return SearchMapTopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchMapTopViewHolder, position: Int) {
        holder.onBind(searchMapClickListener, isCollapsed)
    }

    override fun getItemCount() = 1

    class SearchMapTopViewHolder(
        private val binding: LayoutLookMapBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(searchMapClickListener: (() -> Unit), visibility: Boolean) {
            with(binding) {
                isCollapsed = visibility
                tvRecentWord.setOnClickListener {
                    searchMapClickListener.invoke()
                }
                ivPin.setOnClickListener {
                    searchMapClickListener.invoke()
                }
            }
        }
    }
}
