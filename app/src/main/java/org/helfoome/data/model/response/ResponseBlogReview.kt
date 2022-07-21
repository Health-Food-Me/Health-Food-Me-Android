package org.helfoome.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.BlogReviewInfo

@Serializable
data class ResponseBlogReview(
    val start: Int,
    val display: Int,
    @SerialName("items")
    val reviews: List<Review>?,
) {
    @Serializable
    data class Review(
        val title: String,
        val link: String,
        val description: String,
        @SerialName("bloggername")
        val bloggerName: String,
        @SerialName("bloggerlink")
        val bloggerLink: String,
        val postdate: String,
    )

    fun toBlogReviewInfo() = reviews?.map { review -> BlogReviewInfo(review.title, review.description, review.link) }
}
