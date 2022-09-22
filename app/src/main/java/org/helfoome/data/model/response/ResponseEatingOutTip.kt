package org.helfoome.data.model.response

import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.EatingOutTipInfo

@Serializable
data class ResponseEatingOutTip(
    val category: String,
    val prescription: Prescription?,
) {
    @Serializable
    data class Prescription(
        val recommend: List<String>,
        val tip: List<String>,
    )
    fun toEatingOutTip(): EatingOutTipInfo = EatingOutTipInfo(category, prescription?.recommend, prescription?.tip)
}
