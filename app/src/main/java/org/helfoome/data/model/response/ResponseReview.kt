package org.helfoome.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.HFMReviewInfo
import org.helfoome.domain.entity.ReviewImage

@Serializable
data class ResponseReview(
    @SerialName("_id")
    val id: String,
    val restaurantId: String,
    val restaurant: String,
    val writerId: String,
    val writer: String,
    val score: Float,
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

    fun toReviewInfo() = HFMReviewInfo(
        id,
        writer,
        score,
        listOf(taste) + good,
        content,
        image.map { image -> ReviewImage(image.id, image.name, image.url) }
    )
}
