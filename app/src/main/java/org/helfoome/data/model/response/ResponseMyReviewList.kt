package org.helfoome.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.MyReviewListInfo

@Serializable
data class ResponseMyReviewList(
    val content: String,
    val good: List<String>,
    val id: String,
    val image: List<Image>,
    val restaurant: String,
    val score: Float,
    val taste: String
) {
    @Serializable
    data class Image(
        @SerialName("_id")
        val id: String,
        val name: String,
        val url: String
    )

    fun toMyReviewListInfo() = MyReviewListInfo(
        id,
        score,
        taste,
        good,
        content,
        image.map { image -> MyReviewListInfo.ReviewImage(image.id, image.name, image.url) }
    )
}
