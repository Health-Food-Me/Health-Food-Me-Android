package org.helfoome.data.model.response

data class ResponseRefresh(
    val data: Data,
    val message: String,
    val status: Int,
    val success: Boolean
) {
    data class Data(
        val accessToken: String,
        val refreshToken: String
    )
}
