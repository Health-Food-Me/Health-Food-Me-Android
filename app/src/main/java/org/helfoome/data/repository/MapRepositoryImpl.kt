package org.helfoome.data.repository

import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseMap
import org.helfoome.data.service.MapService
import org.helfoome.domain.repository.MapRepository
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(private val mapService: MapService) : MapRepository {
    override suspend fun getMap(longitude: Double, latitude: Double, zoom: Long, category: String?): BaseResponse<List<ResponseMap>> {
        return mapService.getMapLocation(longitude, latitude, zoom, category)
    }
}
