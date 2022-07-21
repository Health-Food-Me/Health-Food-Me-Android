package org.helfoome.data.repository

import org.helfoome.data.model.response.BaseResponse
import org.helfoome.data.model.response.ResponseMyReviewList
import org.helfoome.data.model.response.ResponseReviewCheck
import org.helfoome.data.service.ReviewService
import org.helfoome.domain.entity.HFMReviewInfo
import org.helfoome.domain.repository.ReviewRepository
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val reviewService: ReviewService,
) : ReviewRepository {
    override suspend fun fetchHFMReview(restaurantId: String): Result<List<HFMReviewInfo>?> {
        return runCatching {
            reviewService.getHFMReview(restaurantId)
        }.fold({
            Result.success(it.data?.map { review -> review.toReviewInfo() })
        }, {
            it.printStackTrace()
            Result.failure(it.fillInStackTrace())
        })
    }

    override suspend fun getMyReviewList(userId: String): BaseResponse<List<ResponseMyReviewList>> {
        return reviewService.getMyReviewList(userId)
    }

    override suspend fun deleteReview(reviewId: String) = runCatching {
        reviewService.deleteReview(reviewId).success
    }

    override suspend fun getReviewCheck(reviewId: String, restaurantId: String): BaseResponse<ResponseReviewCheck> {
        return reviewService.getReviewCheck(reviewId, restaurantId)
    }
}
