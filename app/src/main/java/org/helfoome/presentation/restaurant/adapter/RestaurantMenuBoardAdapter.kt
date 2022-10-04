package org.helfoome.presentation.restaurant.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.helfoome.databinding.ItemMenuBoardBinding

class RestaurantMenuBoardAdapter(private val itemClickListener: (List<String>) -> (Unit)) :
    RecyclerView.Adapter<RestaurantMenuBoardAdapter.MenuBoardViewHolder>() {
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
        fun bind(menuBoardImgUrl: String, menuBoardList: List<String>, itemClickListener: (List<String>) -> (Unit)) {
            binding.ivImage.load(menuBoardImgUrl)
            binding.root.setOnClickListener {
                itemClickListener(menuBoardList)
            }
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
        viewHolder.bind(menuBoardList[position], menuBoardList, itemClickListener)
    }

    override fun getItemCount(): Int = menuBoardList.size
}
