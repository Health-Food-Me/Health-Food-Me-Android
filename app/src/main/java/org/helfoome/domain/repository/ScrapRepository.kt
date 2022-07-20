package org.helfoome.domain.repository

import org.helfoome.domain.entity.ScrapInfo

interface ScrapRepository {
    suspend fun getScrapList(userId: String): Result<List<ScrapInfo>>
    suspend fun updateRestaurantScrap(restaurantId: String, userId: String): List<String>?
}
