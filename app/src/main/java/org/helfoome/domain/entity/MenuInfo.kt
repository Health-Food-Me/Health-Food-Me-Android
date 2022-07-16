package org.helfoome.domain.entity

data class MenuInfo(
    val id: Int,
    val name: String,
    val image: String?,
    val price: Int,
    val carb: Int,
    val protein: Int,
    val fat: Int,
    val calorie: Int?,
    val gramPerPerson: Int,
    val isHealfoomePick: Boolean = false,
    val isGeneralMenu: Boolean = false,
)
