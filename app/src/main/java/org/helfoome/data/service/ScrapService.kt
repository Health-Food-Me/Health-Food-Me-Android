package org.helfoome.data.service

import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseScrapData
import retrofit2.http.GET
import retrofit2.http.Path

interface ScrapService {
    @GET("/user/{userId}/scrapList")
    suspend fun getScrapList(@Path("userId") userId: String): BaseResponse<List<ResponseScrapData>>
}
