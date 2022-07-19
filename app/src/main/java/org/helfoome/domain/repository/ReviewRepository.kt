package org.helfoome.domain.repository

import org.helfoome.domain.entity.ReviewInfo

interface ReviewRepository {
    suspend fun fetchHFMReview(restaurantId: String): Result<List<ReviewInfo>>
}
