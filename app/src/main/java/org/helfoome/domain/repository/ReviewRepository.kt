package org.helfoome.domain.repository

import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseMyReviewList
import org.helfoome.domain.entity.HFMReviewInfo

interface ReviewRepository {
    suspend fun fetchHFMReview(restaurantId: String): Result<List<HFMReviewInfo>>
    suspend fun getMyReviewList(userId: String): BaseResponse<List<ResponseMyReviewList>>
}