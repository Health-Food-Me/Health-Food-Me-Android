package org.helfoome.data.model.request

import java.io.File

data class RequestReviewEdit(
    val score: Float,
    val taste: String,
    val good: List<String>,
    val content: String,
    val image: List<File>,
    val nameList: List<String>
)
