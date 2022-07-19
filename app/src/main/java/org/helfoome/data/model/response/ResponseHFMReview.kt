package org.helfoome.data.model.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.HFMReviewInfo

@Serializable
data class ResponseHFMReview(
    @SerializedName("_id")
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
        @SerializedName("_id")
        val id: String,
        val name: String,
        val url: String,
    )

    fun toReviewInfo() = HFMReviewInfo(id, name, score, taste + good, content, imageList.map { image -> HFMReviewInfo.ReviewImage(image.id, image.name, image.url) })
}
