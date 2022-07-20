package org.helfoome.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseReviewCheck(
    val hasReview: Boolean
)
