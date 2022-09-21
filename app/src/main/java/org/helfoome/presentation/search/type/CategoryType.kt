package org.helfoome.presentation.search.type

import org.helfoome.R

enum class CategoryType(val category: String, val icon: Int) {
    SALAD("샐러드", R.drawable.ic_salad),
    POKE("포케", R.drawable.ic_poke),
    SANDWICH("샌드위치", R.drawable.ic_sandwich),
    KIMBAB("키토킴밥", R.drawable.ic_kimbap),
    DOSIRAK("도시락", R.drawable.ic_dosirak),
    SHABUSHABU("샤브샤브", R.drawable.ic_shabushabu),
    BOSSAM("보쌈", R.drawable.ic_bossam),
    STEAK("스테이크", R.drawable.ic_steak),
    DUPBAP("덮밥", R.drawable.ic_dupbap),
    GOGI("고깃집", R.drawable.ic_meat)
}
