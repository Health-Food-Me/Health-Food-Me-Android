package org.helfoome.domain.entity

data class RestaurantInfo(
    val id: String,
    val image: String,
    val name: String,
    val category: String, // TODO 카테고리 enum class 추가 및 적용 고려
    val score: Float,
    val tags: List<String>,
    var isScrap: Boolean? = false,
    val location: String? = null,
    val time: List<String>? = null,
    val contact: String? = null,
    val distance: Int? = null,
    val menuList: List<MenuInfo>? = null
)
