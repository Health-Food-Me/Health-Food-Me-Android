package org.helfoome.domain.entity

data class MyReviewListInfo(
    val id: String,
    val score: Float,
    val tags: String,
    val good: List<String>,
    val description: String,
    val photoList: List<ReviewImage>,
)