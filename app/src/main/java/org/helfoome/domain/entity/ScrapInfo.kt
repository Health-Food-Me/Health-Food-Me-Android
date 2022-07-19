package org.helfoome.domain.entity

data class ScrapInfo(
    val id: String,
    val restaurantImg: String,
    val title: String,
    val location: String,
    val isBookmarked: Boolean
)
