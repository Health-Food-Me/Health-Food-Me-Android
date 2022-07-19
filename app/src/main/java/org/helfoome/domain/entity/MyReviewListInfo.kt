package org.helfoome.domain.entity

data class MyReviewListInfo(
    val id: String,
    val score: Double,
    val tags: String,
    val good: List<String>,
    val description: String,
    val photoList: List<ReviewImage>,
) {
    data class ReviewImage(
        val id: String,
        val name: String,
        val url: String,
    )
}
