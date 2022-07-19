package org.helfoome.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestProfile(
    val userId: String
)
