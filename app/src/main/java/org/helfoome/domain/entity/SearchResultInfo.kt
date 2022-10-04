package org.helfoome.domain.entity

data class SearchResultInfo(
    val id: String,
    val image: String,
    val name: String,
    val category: String,
    val score: Float,
    val longitude: Double,
    val latitude: Double,
    val distance: String,
    val isDiet: Boolean
) {
    // TODO : 나중에 API 수정에 따라 매핑하는 부분 바뀔 필요있음
    fun toMakerInfo(): MarkerInfo = MarkerInfo(id, isDiet, latitude, longitude, name)
}
