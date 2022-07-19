package org.helfoome.data.model.response

import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.BlogReviewInfo

@Serializable
data class ResponseBlogReview(
    val reviews: List<Review>,
) {
    @Serializable
    data class Review(
        val title: String,
        val link: String,
        val description: String,
    )

    fun toBlogReviewInfo() = reviews.map { review -> BlogReviewInfo(review.title, review.description, review.link) }
}
