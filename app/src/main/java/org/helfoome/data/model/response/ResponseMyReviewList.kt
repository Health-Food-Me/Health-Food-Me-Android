package org.helfoome.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.MyReviewInfo
import org.helfoome.domain.entity.ReviewImage

@Serializable
data class ResponseMyReviewList(
    @SerialName("_id")
    val id: String,
    val content: String,
    val good: List<String>,
    val image: List<Image>,
    val restaurant: String,
    val score: Float,
    val taste: String,
    val restaurantId: String
) {
    @Serializable
    data class Image(
        @SerialName("_id")
        val id: String,
        val name: String,
        val url: String
    )
    fun toMyReviewListInfo() = MyReviewInfo(
        id,
        restaurantId,
        restaurant,
        score,
        taste,
        good,
        content,
        image.map { image -> ReviewImage(image.id, image.name, image.url) }
    )
}
