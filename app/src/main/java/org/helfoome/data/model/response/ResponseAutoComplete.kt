package org.helfoome.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.AutoCompleteKeywordInfo

@Serializable
data class ResponseAutoComplete(
    @SerialName("_id")
    val id: String,
    val name: String,
    val isDiet: Boolean,
    val isCategory: Boolean,
    val distance: Int,
    val longitude: Double,
    val latitude: Double
) {
    fun toAutoCompleteKeywordInfo() = AutoCompleteKeywordInfo(
        id,
        isDiet,
        name,
        isCategory
    )
}
