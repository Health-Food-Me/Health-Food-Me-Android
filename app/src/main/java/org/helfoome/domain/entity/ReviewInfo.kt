package org.helfoome.domain.entity

data class ReviewInfo(
    val id: Int,
    val nickname: String,
    val score: Float,
    val tags: List<String>,
    val description: String,
    val photoList: List<String>,
)