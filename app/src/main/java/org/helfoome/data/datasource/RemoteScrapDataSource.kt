package org.helfoome.data.datasource

import org.helfoome.data.service.RestaurantService
import org.helfoome.data.service.ScrapService
import javax.inject.Inject

class RemoteScrapDataSource @Inject constructor(
    private val scrapService: ScrapService,
    private val restaurantService: RestaurantService
) {
    suspend fun getScrapList(userId: String) = scrapService.getScrapList(userId)

    suspend fun updateRestaurantScrap(
        restaurantId: String,
        userId: String
    ) = restaurantService.updateRestaurantScrap(restaurantId, userId)
}
