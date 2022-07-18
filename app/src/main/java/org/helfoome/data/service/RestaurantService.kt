package org.helfoome.data.service

import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseRestaurantSummary
import org.helfoome.data.model.response.ResponseScrap
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
}
