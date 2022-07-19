package org.helfoome.data.model.response

import kotlinx.serialization.Serializable
import org.helfoome.domain.EatingOutTip
import org.helfoome.domain.entity.RestaurantInfo

@Serializable
data class ResponseEatingOutTip(
    val category: String,
    val content: Content,
) {
    @Serializable
    data class Content(
        val recommend: List<String>,
        val tip: List<String>,
    )
    fun toEatingOutTip(): EatingOutTip = EatingOutTip(category, content.recommend, content.tip)
}