package org.helfoome.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.ScrapInfo

@Serializable
data class ResponseScrapData(
    @SerialName("_id")
    val id: String,
    val category: String,
    val hashtag: List<String>,
    val latitude: Double,
    val logo: String,
    val longtitude: Double,
    val name: String,
    val score: Double,
    val address: String
) {
    fun toScrapInfo() = ScrapInfo(
        id,
        logo,
        name,
        address,
        true
    )
}
