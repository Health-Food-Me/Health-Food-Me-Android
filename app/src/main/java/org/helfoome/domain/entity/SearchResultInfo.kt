package org.helfoome.domain.entity

data class SearchResultInfo(
    val id: Int,
    val image: String,
    val name: String,
    val category: String,
    val score: String,
    val distance: String
)
