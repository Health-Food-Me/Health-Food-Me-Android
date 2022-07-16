package org.helfoome.presentation.type

import org.helfoome.R

enum class FoodType(val color: Int, val icon: Int, val iconTint: Int) {
    FOOD_SALAD(R.color.selector_chip_bg_diet, R.drawable.ic_salad, R.color.selector_chip_icon_diet),
    FOOD_POKE(R.color.selector_chip_bg_diet, R.drawable.ic_poke, R.color.selector_chip_icon_diet),
    FOOD_QUITO(R.color.selector_chip_bg_diet, R.drawable.ic_kimbap, R.color.selector_chip_icon_diet),
    FOOD_SANDWICH(R.color.selector_chip_bg_normal, R.drawable.ic_sandwich, R.color.selector_chip_icon_normal),
    FOOD_SHABU(R.color.selector_chip_bg_normal, R.drawable.ic_shabushabu, R.color.selector_chip_icon_normal),
    FOOD_BOSAM(R.color.selector_chip_bg_normal, R.drawable.ic_bossam, R.color.selector_chip_icon_normal),
    FOOD_MEAT(R.color.selector_chip_bg_normal, R.drawable.ic_meat, R.color.selector_chip_icon_normal),
    FOOD_RICEBOWL(R.color.selector_chip_bg_normal, R.drawable.ic_dupbap, R.color.selector_chip_icon_normal)
}
