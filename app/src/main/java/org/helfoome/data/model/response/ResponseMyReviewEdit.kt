package org.helfoome.data.model.response

data class ResponseMyReviewEdit(
    val __v: Int,
    val _id: String,
    val content: String,
    val good: List<String>,
    val image: List<Image>,
    val restaurant: String,
    val score: Double,
    val taste: String,
    val writer: String
) {
    data class Image(
        val _id: String,
        val name: String,
        val url: String
    )
}
