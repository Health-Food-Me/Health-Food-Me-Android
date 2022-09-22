package org.helfoome.domain.repository

import org.helfoome.domain.entity.BlogReviewInfo
import org.helfoome.domain.entity.EatingOutTipInfo
import org.helfoome.domain.entity.RestaurantInfo
import org.helfoome.domain.entity.HFMReviewInfo

interface RestaurantRepository {
    suspend fun fetchRestaurantSummary(restaurantId: String, userId: String): RestaurantInfo?
    suspend fun fetchRestaurantDetail(restaurantId: String, userId: String, latitude: Double, longitude: Double): Result<RestaurantInfo?>
    suspend fun updateRestaurantScrap(restaurantId: String, userId: String): Boolean?
    suspend fun getEatingOutTips(restaurantId: String): List<EatingOutTipInfo>?
    suspend fun fetchHFMReview(restaurantId: String): Result<List<HFMReviewInfo>?>
    suspend fun fetchBlogReview(name: String): Result<List<BlogReviewInfo>?>
}
