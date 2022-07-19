package org.helfoome.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.MarkerInfo

@Serializable
data class ResponseMap(
    @SerialName("_id")
    val id: String,
    val isDietRestaurant: Boolean,
    val latitude: Double,
    val longitude: Double,
    val name: String
) {
    fun toMakerInfo(): MarkerInfo = MarkerInfo(id, isDietRestaurant, latitude, longitude, name)
}
