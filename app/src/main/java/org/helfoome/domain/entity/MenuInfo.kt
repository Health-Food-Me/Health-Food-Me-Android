package org.helfoome.domain.entity

data class MenuInfo(
    val id: String,
    val name: String,
    val image: String?,
    val price: Int,
    val calorie: Int?,
    val gramPerPerson: Int?,
    val isHealfoomePick: Boolean = false,
)
