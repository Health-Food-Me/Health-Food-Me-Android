package org.helfoome.data.repository

import org.helfoome.data.service.RestaurantService
import org.helfoome.domain.entity.RestaurantInfo
import org.helfoome.domain.repository.RestaurantRepository
import timber.log.Timber
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val restaurantService: RestaurantService,
) : RestaurantRepository {
    override suspend fun fetchRestaurantSummary(restaurantId: String, userId: String): RestaurantInfo? =
        runCatching {
            restaurantService.getRestaurantSummary(restaurantId, userId)
        }.fold({
            return it.body()?.data?.toRestaurantInfo()
        }, {
            it.printStackTrace()
            Timber.d(it.message)
            return null
        })

    override suspend fun fetchRestaurantDetail(restaurantId: String): RestaurantInfo? {
        TODO("Not yet implemented")
    }
}
