package org.helfoome.data.model.request

import java.io.File

data class RequestEditReview(
    val score: Double,
    val taste: String,
    val good: List<String>,
    val context: String,
    val image: List<File>,
    val nameList: List<String>
)
