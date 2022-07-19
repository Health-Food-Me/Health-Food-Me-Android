package org.helfoome.data.service

import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseMap
import retrofit2.http.GET
import retrofit2.http.Query

interface MapService {

    @GET("/restaurant")
    suspend fun getMapLocation(
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("zoom") zoom: Long,
        @Query("category") category: String?
    ): BaseResponse<List<ResponseMap>>
}
