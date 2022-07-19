package org.helfoome.domain.repository

import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseMap

interface MapRepository {
    suspend fun getMap(
        longitude: Double,
        latitude: Double,
        zoom: Long,
        category: String?
    ): BaseResponse<List<ResponseMap>>
}
