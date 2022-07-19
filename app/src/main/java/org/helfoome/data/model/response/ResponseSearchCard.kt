package org.helfoome.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.SearchResultInfo

@Serializable
data class ResponseSearchCard(
    @SerialName("_id")
    val id: String,
    val category: String,
    val distance: Int,
    val logo: String,
    val name: String,
    val score: Double
) {
    fun toSearchResultInfo(): SearchResultInfo {
        val distance = when (distance >= 1000) {
            true -> "거리: ${String.format("%.1f", distance.toDouble() / 1000)}km"
            else -> "거리: ${distance}m"
        }
        return SearchResultInfo(
            id,
            logo,
            name,
            category,
            score.toFloat(),
            distance
        )
    }
}
