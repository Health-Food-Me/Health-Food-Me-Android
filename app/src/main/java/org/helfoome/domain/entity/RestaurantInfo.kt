package org.helfoome.domain.entity

data class RestaurantInfo(
    val id: Int,
    val image: String,
    val name: String,
    val category: String, // TODO 카테고리 enum class 추가 및 적용 고려
    val score: Float,
    val tags: List<String>,
    val location: String,
    val time: List<String>, // TODO 서버 응답값 확인 후 변경할 예정
    val number: String,
)
