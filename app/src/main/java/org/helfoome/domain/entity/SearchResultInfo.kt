package org.helfoome.domain.entity

data class SearchResultInfo(
    val id: String,
    val image: String,
    val name: String,
    val category: String,
    val score: Float,
    val longitude: Double,
    val latitude: Double,
    val distance: String
)
