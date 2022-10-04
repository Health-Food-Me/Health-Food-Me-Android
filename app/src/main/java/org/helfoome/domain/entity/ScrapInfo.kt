package org.helfoome.domain.entity

data class ScrapInfo(
    val id: String,
    val restaurantImg: String,
    val title: String,
    val location: String,
    val isBookmarked: Boolean,
    val latitude: Double,
    val longitude: Double,
    val score: Double,
    val isDiet: Boolean
) {
    // TODO : 나중에 API 수정에 따라 매핑하는 부분 바뀔 필요있음
    fun toMakerInfo(): MarkerInfo = MarkerInfo(id, isDiet, latitude, longitude, title)
}
