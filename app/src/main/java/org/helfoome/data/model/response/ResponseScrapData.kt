package org.helfoome.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.ScrapInfo

@Serializable
data class ResponseScrapData(
    @SerialName("_id")
    val id: String,
    val name: String,
    val logo: String,
    val score: Double,
    val category: List<String>,
    val latitude: Double,
    val longtitude: Double,
    val address: String,
) {
    fun toScrapInfo() = ScrapInfo(
        id,
        logo,
        name,
        address,
        true,
        latitude,
        longtitude,
        score
    )
}
