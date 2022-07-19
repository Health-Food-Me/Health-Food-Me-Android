package org.helfoome.data.repository

import org.helfoome.data.service.ReviewService
import org.helfoome.domain.entity.ReviewInfo
import org.helfoome.domain.repository.ReviewRepository
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val reviewService: ReviewService,
) : ReviewRepository {
    override suspend fun fetchHFMReview(restaurantId: String): Result<List<ReviewInfo>> {
        return runCatching {
            reviewService.getHFMReview(restaurantId)
        }.fold({
            Result.success(it.data.map { review -> review.toReviewInfo() })
        }, {
            it.printStackTrace()
            Result.failure(it.fillInStackTrace())
        })
    }
}
