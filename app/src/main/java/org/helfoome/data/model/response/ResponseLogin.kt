package org.helfoome.data.model.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ResponseLogin(
    @Contextual
    val user: User,
    val accessToken: String,
    val refreshToken: String
) {
    data class User(
        @SerializedName("_id")
        val id: String,
        val name: String,
        val socialId: String,
        val email: String,
        val scrapRestaurants: List<String>,
        val refreshToken: String
    )
}
