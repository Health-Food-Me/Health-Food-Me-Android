package org.helfoome.domain

import kotlinx.serialization.Serializable

@Serializable
data class EatingOutTip(
    val category: String,
    val recommendTips: List<String>,
    val eatingTips: List<String>,
)
