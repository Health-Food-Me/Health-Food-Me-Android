package org.helfoome.data.repository

import org.helfoome.data.service.RestaurantService
import org.helfoome.domain.entity.EatingOutTipInfo
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
            return null
        })

    override suspend fun fetchRestaurantDetail(restaurantId: String): RestaurantInfo? {
        TODO("Not yet implemented")
    }

    override suspend fun updateRestaurantScrap(restaurantId: String, userId: String): Boolean? {
        runCatching {
            restaurantService.updateRestaurantScrap(restaurantId, userId)
        }.fold({
            return it.body()?.data?.isScrap
        }, {
            it.printStackTrace()
            return null
        })
    }

    override suspend fun getEatingOutTips(restaurantId: String): EatingOutTipInfo? {
        runCatching {
            restaurantService.getEatingOutTips(restaurantId)
        }.fold({
            Timber.d("${it.body()?.data?.toEatingOutTip()}")
            return it.body()?.data?.toEatingOutTip()
        }, {
            Timber.d("${it.message}")
            it.printStackTrace()
            return null
        })
    }
}
