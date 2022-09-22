package org.helfoome.data.repository

import org.helfoome.data.datasource.RemoteRestaurantDataSource
import org.helfoome.data.service.RestaurantService
import org.helfoome.domain.entity.BlogReviewInfo
import org.helfoome.domain.entity.EatingOutTipInfo
import org.helfoome.domain.entity.HFMReviewInfo
import org.helfoome.domain.entity.RestaurantInfo
import org.helfoome.domain.repository.RestaurantRepository
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val restaurantService: RestaurantService, // TODO delete
    private val restaurantDataSource: RemoteRestaurantDataSource,
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

    override suspend fun fetchRestaurantDetail(
        restaurantId: String,
        userId: String,
        latitude: Double,
        longitude: Double
    ): Result<RestaurantInfo?> {
        return runCatching {
            restaurantService.getRestaurantDetail(restaurantId, userId, latitude, longitude)
        }.fold(
            {
                Result.success(it.body()?.data?.toRestaurantInfo())
            },
            {
                it.printStackTrace()
                Result.failure(it.fillInStackTrace())
            }
        )
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

    override suspend fun getEatingOutTips(restaurantId: String): List<EatingOutTipInfo>? {
        runCatching {
            restaurantService.getEatingOutTips(restaurantId)
        }.fold({
            return it.body()?.data?.map { it.toEatingOutTip() }
        }, {
            it.printStackTrace()
            return null
        })
    }

    override suspend fun fetchHFMReview(userId: String): Result<List<HFMReviewInfo>> {
        return runCatching {
            restaurantDataSource.getHFMReview(userId)
        }.fold({
            Result.success(it.data.map { review -> review.toReviewInfo() })
        }, {
            it.printStackTrace()
            Result.failure(it.fillInStackTrace())
        })
    }

    override suspend fun fetchBlogReview(restaurantId: String): Result<List<BlogReviewInfo>?> {
        return runCatching {
            restaurantDataSource.getBlogReview(restaurantId)
        }.fold({
            Result.success(it.data.toBlogReviewInfo())
        }, {
            it.printStackTrace()
            Result.failure(it.fillInStackTrace())
        })
    }
}
