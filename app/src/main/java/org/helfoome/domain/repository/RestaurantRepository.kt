package org.helfoome.domain.repository

import org.helfoome.domain.entity.EatingOutTipInfo
import org.helfoome.domain.entity.RestaurantInfo
import org.helfoome.domain.entity.HFMReviewInfo

interface RestaurantRepository {
    suspend fun fetchRestaurantSummary(restaurantId: String, userId: String): RestaurantInfo?
    suspend fun fetchRestaurantDetail(restaurantId: String): RestaurantInfo?
    suspend fun updateRestaurantScrap(restaurantId: String, userId: String): Boolean?
    suspend fun getEatingOutTips(restaurantId: String): EatingOutTipInfo?
    suspend fun fetchHFMReview(restaurantId: String): Result<List<HFMReviewInfo>>
}
