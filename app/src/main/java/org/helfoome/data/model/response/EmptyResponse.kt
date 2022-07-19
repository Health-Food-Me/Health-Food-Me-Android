package org.helfoome.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class EmptyResponse(
    val status: Int,
    val success: Boolean,
    val message: String,
)
