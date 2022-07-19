package org.helfoome.domain.entity

data class MarkerInfo(
    val id: String,
    val isDietRestaurant: Boolean,
    val latitude: Double,
    val longitude: Double,
    val name: String
)
