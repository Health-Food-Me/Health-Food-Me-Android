package org.helfoome.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class EatingOutTipInfo(
    val category: String,
    val recommendTips: List<String>?,
    val eatingTips: List<String>?,
)
