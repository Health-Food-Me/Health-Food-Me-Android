package org.helfoome.presentation.restaurant.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.helfoome.databinding.ItemMenuBoardBinding

class RestaurantMenuBoardAdapter : RecyclerView.Adapter<RestaurantMenuBoardAdapter.MenuBoardViewHolder>() {
    private lateinit var inflater: LayoutInflater
    private val _menuBoardList = mutableListOf<String>()
    var menuBoardList: List<String> = _menuBoardList
        set(value) {
            _menuBoardList.clear()
            _menuBoardList.addAll(value)
            notifyDataSetChanged()
        }

    class MenuBoardViewHolder(private val binding: ItemMenuBoardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menuBoardImgUrl: String) {
            binding.ivImage.load(menuBoardImgUrl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuBoardViewHolder {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(parent.context)

        val binding =
            ItemMenuBoardBinding.inflate(inflater, parent, false)
        return MenuBoardViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: MenuBoardViewHolder, position: Int) {
        viewHolder.bind(menuBoardList[position])
    }

    override fun getItemCount(): Int = menuBoardList.size
}
