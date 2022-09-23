package org.helfoome.data.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.EmptyResponse
import org.helfoome.data.model.response.*

import retrofit2.http.*
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReviewService {
    // TODO 리뷰 작성 관련 서비스 파일로 변경 예정
    @GET("/review/restaurant/{restaurantId}")
    suspend fun getHFMReview(
        @Path("restaurantId") restaurantId: String,
    ): BaseResponse<List<ResponseHFMReview>?>

    @GET("/review/user/{userId}")
    suspend fun getMyReviewList(
        @Path("userId") userId: String,
    ): BaseResponse<List<ResponseMyReviewList>>

    @Multipart
    @POST("/review/{userId}/{restaurantId}")
    suspend fun postHFMReview(
        @Path("userId") userId: String,
        @Path("restaurantId") restaurantId: String,
        @Part("score") score: RequestBody,
        @Part("taste") taste: RequestBody,
        @Part good: List<MultipartBody.Part>,
        @Part("content") content: RequestBody,
        @Part image: List<MultipartBody.Part>,
    ): BaseResponse<ResponseReview>

    @DELETE("/review/{reviewId}")
    suspend fun deleteReview(
        @Path("reviewId") reviewId: String,
    ): EmptyResponse

    @Multipart
    @PUT("/review/{reviewId}")
    suspend fun putMyReviewEdit(
        @Path("reviewId") reviewId: String,
        @Part("score") score: RequestBody,
        @Part("taste") taste: RequestBody,
        @Part good: List<MultipartBody.Part>,
        @Part("content") content: RequestBody,
        @Part("nameList") nameList: RequestBody,
        @Part image: List<MultipartBody.Part>
    ): BaseResponse<ResponseMyReviewEdit>

    @GET("/user/check/{userId}/{restaurantId}")
    suspend fun getReviewCheck(
        @Path("userId") userId: String,
        @Path("restaurantId") restaurantId: String
    ): BaseResponse<ResponseReviewCheck>
}
