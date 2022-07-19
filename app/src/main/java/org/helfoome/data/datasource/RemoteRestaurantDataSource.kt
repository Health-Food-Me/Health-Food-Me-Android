package org.helfoome.data.datasource

import org.helfoome.data.service.RestaurantService
import javax.inject.Inject

class RemoteRestaurantDataSource @Inject constructor(
    private val restaurantService: RestaurantService,
) {
    suspend fun getHFMReview(restaurantId: String) = restaurantService.getHFMReview(restaurantId)
    suspend fun getBlogReview(restaurantId: String) = restaurantService.getBlogReview(restaurantId)
}
