package org.helfoome.domain.entity

data class MyReviewInfo(
    val id: Int,
    val name: String,
    val score: Int,
    val hashtag: List<String>,
    val content: String,
    val image: List<String>,
)
