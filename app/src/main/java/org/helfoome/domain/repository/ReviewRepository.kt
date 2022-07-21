package org.helfoome.domain.repository

import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseMyReviewEdit
import org.helfoome.data.model.response.ResponseMyReviewList
import org.helfoome.data.model.response.ResponseReviewCheck
import org.helfoome.domain.entity.HFMReviewInfo

interface ReviewRepository {
    suspend fun fetchHFMReview(restaurantId: String): Result<List<HFMReviewInfo>?>
    suspend fun getMyReviewList(userId: String): BaseResponse<List<ResponseMyReviewList>>
    suspend fun deleteReview(reviewId: String): Result<Boolean>
    suspend fun putMyReviewEdit(reviewId: String): BaseResponse<ResponseMyReviewEdit>
    suspend fun getReviewCheck(reviewId: String, restaurantId: String): BaseResponse<ResponseReviewCheck>
}
