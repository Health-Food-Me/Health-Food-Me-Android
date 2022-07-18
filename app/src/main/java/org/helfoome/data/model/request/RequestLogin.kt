package org.helfoome.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestLogin(
    val social: String,
    val token: String
)