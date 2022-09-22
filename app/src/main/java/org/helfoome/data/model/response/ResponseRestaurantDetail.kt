package org.helfoome.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.MenuInfo
import org.helfoome.domain.entity.RestaurantInfo

@Serializable
data class ResponseRestaurantDetail(
    @SerialName("menu")
    val menuList: List<Menu>?,
    val restaurant: Restaurant,
) {
    @Serializable
    data class Restaurant(
        @SerialName("_id")
        val id: String,
        val distance: Int,
        val name: String,
        val logo: String,
        val isDiet: Boolean,
        val menuBoard: List<String>?,
        val category: List<String>,
        val address: String,
        val workTime: List<String>,
        val contact: String,
        val isScrap: Boolean,
        val score: Float,
    )

    @Serializable
    data class Menu(
        @SerialName("_id")
        val id: String,
        val name: String,
        val image: String,
        val price: Int,
        val isPick: Boolean,
        val kcal: Int? = null,
        val per: Int? = null,
    )

    fun toRestaurantInfo() = RestaurantInfo(
        restaurant.id,
        restaurant.logo,
        restaurant.menuBoard,
        restaurant.name,
        restaurant.category,
        isScrap = restaurant.isScrap,
        location = restaurant.address,
        time = restaurant.workTime,
        contact = restaurant.contact,
        distance = restaurant.distance,
        menuList = menuList?.map { menu ->
            MenuInfo(menu.id, menu.name, menu.image, menu.price, menu.kcal, menu.per, menu.isPick)
        },
        score = restaurant.score
    )
}
