package org.helfoome.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseMyReviewEdit(
    @SerialName("__v")
    val v: Int,
    @SerialName("_id")
    val id: String,
    val content: String,
    val good: List<String>,
    val image: List<Image>,
    val restaurant: String,
    val score: Double,
    val taste: String,
    val writer: String
) {
    @Serializable
    data class Image(
        @SerialName("_id")
        val id: String,
        val name: String,
        val url: String
    )
}
