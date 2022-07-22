package org.helfoome.data.datasource

import org.helfoome.data.service.RestaurantService
import javax.inject.Inject

class RemoteRestaurantDataSource @Inject constructor(
    private val restaurantService: RestaurantService,
) {
    suspend fun getHFMReview(userId: String) = restaurantService.getHFMReview(userId)
    suspend fun getBlogReview(name: String) = restaurantService.getBlogReview(name)
}
