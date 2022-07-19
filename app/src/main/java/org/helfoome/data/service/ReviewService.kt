package org.helfoome.data.service

import org.helfoome.data.model.response.BaseResponse

import org.helfoome.data.model.response.ResponseHFMReview
import retrofit2.http.GET
import retrofit2.http.Path

interface ReviewService {
    @GET("/review/restaurant/{restaurantId}")
    suspend fun getHFMReview(
        @Path("restaurantId") restaurantId: String,
    ): BaseResponse<List<ResponseHFMReview>>
}
