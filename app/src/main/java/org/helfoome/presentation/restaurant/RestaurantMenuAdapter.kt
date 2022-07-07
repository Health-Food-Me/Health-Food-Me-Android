package org.helfoome.presentation.restaurant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.helfoome.databinding.ItemMenuBinding
import org.helfoome.databinding.ItemMenuNutrientBinding
import org.helfoome.domain.entity.MenuInfo

class RestaurantMenuAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var menuCardViewType = MenuCardViewType.MENU_VIEW_TYPE
    private var menuList = listOf<MenuInfo>()

    class MenuViewHolder(private val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: MenuInfo) {
            binding.menu = menu
        }
    }

    class MenuNutrientViewHolder(private val binding: ItemMenuNutrientBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: MenuInfo) {
            binding.menu = menu
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MenuCardViewType.MENU_VIEW_TYPE.ordinal -> {
                val binding =
                    ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MenuViewHolder(binding)
            }
            else -> {
                val binding =
                    ItemMenuNutrientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MenuNutrientViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val data = menuList[position]
        when (viewHolder) {
            is MenuViewHolder -> viewHolder.bind(data)
            is MenuNutrientViewHolder -> viewHolder.bind(data)
        }
    }

    fun setMenuCardViewType(menuViewType: MenuCardViewType) {
        menuCardViewType = menuViewType
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return menuCardViewType.ordinal
    }

    override fun getItemCount(): Int = menuList.size

    fun setData(menuList: List<MenuInfo>) {
        this.menuList = menuList
        notifyDataSetChanged()
    }

    enum class MenuCardViewType {
        MENU_VIEW_TYPE, NUTRIENT_VIEW_TYPE
    }
}
