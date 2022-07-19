package org.helfoome.data.model.response

import com.google.gson.annotations.SerializedName

data class ResponseProfileModify(
    @SerializedName("_id")
    val id: String,
    val name: String
)
