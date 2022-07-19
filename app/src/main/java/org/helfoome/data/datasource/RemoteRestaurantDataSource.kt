package org.helfoome.data.datasource

import org.helfoome.data.service.ReviewService
import javax.inject.Inject

class RemoteRestaurantDataSource @Inject constructor(
    private val reviewService: ReviewService,
) {
    suspend fun getHFMReview(restaurantId: String) = reviewService.getHFMReview(restaurantId)
}
