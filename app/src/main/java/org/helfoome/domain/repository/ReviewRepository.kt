package org.helfoome.domain.repository

import org.helfoome.domain.entity.HFMReviewInfo

interface ReviewRepository {
    suspend fun fetchHFMReview(restaurantId: String): Result<List<HFMReviewInfo>>
}
