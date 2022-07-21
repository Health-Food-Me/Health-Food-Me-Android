package org.helfoome.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.MenuInfo
import org.helfoome.domain.entity.RestaurantInfo

@Serializable
data class ResponseRestaurantDetail(
    @SerialName("menu")
    val menuList: List<Menu>,
    val restaurant: Restaurant,
) {
    @Serializable
    data class Restaurant(
        @SerialName("_id")
        val id: String,
        val address: String,
        val score: Float,
        val category: String,
        val contact: String,
        val distance: Int,
        val hashtag: List<String>,
        val isScrap: Boolean,
        val logo: String,
        val name: String,
        val workTime: List<String>,
    )

    @Serializable
    data class Menu(
        @SerialName("_id")
        val id: String,
        val name: String,
        val isPick: Boolean,
        val kcal: Int? = null,
        val image: String,
        val per: Int? = null,
        val price: Int,
    )

    fun toRestaurantInfo() = RestaurantInfo(restaurant.id,
        restaurant.logo,
        restaurant.name,
        restaurant.category,
        tags = restaurant.hashtag,
        isScrap = restaurant.isScrap,
        location = restaurant.address,
        time = restaurant.workTime,
        contact = restaurant.contact,
        distance = restaurant.distance,
        menuList = menuList.map { menu -> MenuInfo(menu.id, menu.name, menu.image, menu.price, menu.kcal, menu.per, menu.isPick)
        },
        score=restaurant.score)
}
