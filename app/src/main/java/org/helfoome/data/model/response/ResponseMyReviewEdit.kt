package org.helfoome.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseMyReviewEdit(
    @SerialName("_id")
    val id: String,
    val restaurantId: String,
    val restaurant: String,
    val writerId: String,
    val writer: String,
    val score: Double,
    val content: String,
    val image: List<Image>,
    val taste: String,
    val good: List<String>,
) {
    @Serializable
    data class Image(
        @SerialName("_id")
        val id: String,
        val name: String,
        val url: String,
    )
}
