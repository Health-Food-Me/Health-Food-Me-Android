package org.helfoome.data.model.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.RestaurantInfo

@Serializable
data class ResponseRestaurantSummary(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val logo: String,
    val category: String,
    val hashtag: List<String>,
    val score: Float,
    val isScrap: Boolean,
) {
    fun toRestaurantInfo(): RestaurantInfo = RestaurantInfo(id, logo, name, category, score, hashtag, isScrap)
}
