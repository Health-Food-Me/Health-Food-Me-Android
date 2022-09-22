package org.helfoome.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.RestaurantInfo

@Serializable
data class ResponseRestaurantSummary(
    @SerialName("_id")
    val id: String,
    val name: String,
    val logo: String,
    val category: List<String>,
    val hashtag: List<String>,
    val score: Float,
    val isScrap: Boolean,
) {
    fun toRestaurantInfo(): RestaurantInfo = RestaurantInfo(id, logo, null, name, category, score, isScrap)
}
