package org.helfoome.data.service

import org.helfoome.data.model.response.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface RestaurantService {
    @GET("/restaurant/{restaurantId}/{userId}")
    suspend fun getRestaurantSummary(
        @Path("restaurantId") restaurantId: String,
        @Path("userId") userId: String,
    ): Response<BaseResponse<ResponseRestaurantSummary>>

    @GET("/restaurant/{restaurantId}")
    suspend fun getRestaurantDetail(
        @Path("restaurantId") restaurantId: String,
    ): Response<BaseResponse<ResponseRestaurantSummary>>

    @PUT("/user/{userId}/scrap/{restaurantId}")
    suspend fun updateRestaurantScrap(
        @Path("restaurantId") restaurantId: String,
        @Path("userId") userId: String,
    ): Response<BaseResponse<ResponseScrap>>

    @GET("/restaurant/{restaurantId}/prescription")
    suspend fun getEatingOutTips(
        @Path("restaurantId") restaurantId: String,
    ): Response<BaseResponse<ResponseEatingOutTip>>

    @GET("/review/restaurant/{restaurantId}")
    suspend fun getHFMReview(
        @Path("restaurantId") restaurantId: String,
    ): BaseResponse<List<ResponseHFMReview>>
}
