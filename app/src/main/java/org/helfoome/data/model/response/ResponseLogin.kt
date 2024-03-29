package org.helfoome.data.model.response

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseLogin(
    @Contextual
    val user: User,
    val accessToken: String,
    val refreshToken: String
) {
    @Serializable
    data class User(
        @SerialName("_id")
        val id: String,
        val name: String,
        val email: String? = null,
        val agent: String,
    )
}
