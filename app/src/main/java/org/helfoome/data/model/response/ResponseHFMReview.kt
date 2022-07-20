package org.helfoome.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.HFMReviewInfo

@Serializable
data class ResponseHFMReview(
    @SerialName("_id")
    val id: String,
    val writer: String,
    val score: Float,
    val content: String,
    val name: String,
    val imageList: List<ReviewImage>,
    val taste: List<String>,
    val good: List<String>,
) {
    @Serializable
    data class ReviewImage(
        @SerialName("_id")
        val id: String,
        val name: String,
        val url: String,
    )

    fun toReviewInfo() = HFMReviewInfo(
        id,
        name,
        score,
        taste + good,
        content,
        imageList.map { image -> HFMReviewInfo.ReviewImage(image.id, image.name, image.url) }
    )
}
