package org.helfoome.data.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.helfoome.data.model.response.BaseResponse

import org.helfoome.data.model.response.ResponseHFMReview
import org.helfoome.data.model.response.ResponseMyReviewList
import org.helfoome.data.model.response.ResponseReview
import retrofit2.http.*

interface ReviewService {
    // TODO 리뷰 작성 관련 서비스 파일로 변경 예정
    @GET("/review/restaurant/{restaurantId}")
    suspend fun getHFMReview(
        @Path("restaurantId") restaurantId: String,
    ): BaseResponse<List<ResponseHFMReview>>

    @GET("/review/user/{userId}")
    suspend fun getMyReviewList(
        @Path("userId") userId: String,
    ): BaseResponse<List<ResponseMyReviewList>>

    @Multipart
    @POST("/review/user/{userId}/restaurant/{restaurantId}")
    suspend fun postHFMReview(
        @Path("userId") userId: String,
        @Path("restaurantId") restaurantId: String,
        @Part("score") score: RequestBody,
        @Part("taste") taste: RequestBody,
        @Part("good") good: RequestBody,
        @Part("content") content: RequestBody,
        @Part image: List<MultipartBody.Part>,
    ): BaseResponse<ResponseReview>
}
