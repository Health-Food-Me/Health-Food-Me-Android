package org.helfoome.domain.entity

data class HFMReviewInfo(
    val id: String,
    val nickname: String,
    val score: Float,
    val tags: List<String>,
    val description: String,
    val photoList: List<ReviewImage>,
)
