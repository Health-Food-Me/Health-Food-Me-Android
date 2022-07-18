package org.helfoome.data.model.response

import com.google.gson.annotations.SerializedName
import org.helfoome.domain.entity.AutoCompleteKeywordInfo

data class ResponseAutoComplete(
    @SerializedName("_id")
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
