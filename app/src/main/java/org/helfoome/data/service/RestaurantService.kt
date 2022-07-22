package org.helfoome.data.service

import org.helfoome.data.model.response.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RestaurantService {
    @GET("/restaurant/{restaurantId}/{userId}")
    suspend fun getRestaurantSummary(
        @Path("restaurantId") restaurantId: String,
        @Path("userId") userId: String,
    ): Response<BaseResponse<ResponseRestaurantSummary>>

    @GET("/restaurant/{restaurantId}/{userId}/menus")
    suspend fun getRestaurantDetail(
        @Path("restaurantId") restaurantId: String,
        @Path("userId") userId: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    ): Response<BaseResponse<ResponseRestaurantDetail>>

    @PUT("/user/{userId}/scrap/{restaurantId}")
    suspend fun updateRestaurantScrap(
        @Path("restaurantId") restaurantId: String,
        @Path("userId") userId: String,
    ): Response<BaseResponse<ResponseScrap>>

    @GET("/restaurant/{restaurantId}/prescription")
    suspend fun getEatingOutTips(
        @Path("restaurantId") restaurantId: String,
    ): Response<BaseResponse<ResponseEatingOutTip>>

    @GET("/review/restaurant/{userId}")
    suspend fun getHFMReview(
        @Path("userId") userId: String,
    ): BaseResponse<List<ResponseHFMReview>>

    @GET("review/restaurant/{name}/blog")
    suspend fun getBlogReview(
        @Path("name") name: String,
    ): BaseResponse<ResponseBlogReview>
}
