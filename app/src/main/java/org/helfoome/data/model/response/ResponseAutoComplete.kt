package org.helfoome.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.helfoome.domain.entity.AutoCompleteKeywordInfo

@Serializable
data class ResponseAutoComplete(
    @SerialName("_id")
    val id: String,
    val isDietRestaurant: Boolean,
    val name: String
) {
    fun toAutoCompleteKeywordInfo() = AutoCompleteKeywordInfo(
        id,
        isDietRestaurant,
        name
    )
}
