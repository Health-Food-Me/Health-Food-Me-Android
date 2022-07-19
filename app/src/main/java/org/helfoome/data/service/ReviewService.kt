package org.helfoome.data.service

import org.helfoome.data.model.response.BaseResponse

import org.helfoome.data.model.response.ResponseHFMReview
import retrofit2.http.GET
import retrofit2.http.Path

interface ReviewService {
    // TODO 리뷰 작성 관련 서비스 파일로 변경 예정
    @GET("/review/restaurant/{restaurantId}")
    suspend fun getHFMReview(
        @Path("restaurantId") restaurantId: String,
    ): BaseResponse<List<ResponseHFMReview>>
}
