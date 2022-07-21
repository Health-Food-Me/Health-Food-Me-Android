package org.helfoome.data.model.response

import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.EatingOutTipInfo

@Serializable
data class ResponseEatingOutTip(
    val category: String,
    val content: Content?,
) {
    @Serializable
    data class Content(
        val recommend: List<String>,
        val tip: List<String>,
    )
    fun toEatingOutTip(): EatingOutTipInfo = EatingOutTipInfo(category, content?.recommend, content?.tip)
}
