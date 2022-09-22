package org.helfoome.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseScrap(
    val userId: String,
    val restaurantId: String,
    val isScrap: Boolean,
)
