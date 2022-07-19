package org.helfoome.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseProfileModify(
    @SerialName("_id")
    val id: String,
    val name: String
)
