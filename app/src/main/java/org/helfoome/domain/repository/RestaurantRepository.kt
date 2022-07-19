package org.helfoome.domain.repository

import org.helfoome.domain.entity.RestaurantInfo

interface RestaurantRepository {
    suspend fun fetchRestaurantSummary(restaurantId: String, userId: String): RestaurantInfo?
    suspend fun fetchRestaurantDetail(restaurantId: String): RestaurantInfo?
    suspend fun updateRestaurantScrap(restaurantId: String, userId: String): Boolean?
}
