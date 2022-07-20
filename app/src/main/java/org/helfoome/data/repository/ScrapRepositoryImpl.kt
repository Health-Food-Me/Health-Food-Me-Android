package org.helfoome.data.repository

import org.helfoome.data.datasource.RemoteScrapDataSource
import org.helfoome.domain.entity.ScrapInfo
import org.helfoome.domain.repository.ScrapRepository
import javax.inject.Inject

class ScrapRepositoryImpl @Inject constructor(
    private val scrapDataSource: RemoteScrapDataSource,
) : ScrapRepository {
    override suspend fun getScrapList(userId: String): Result<List<ScrapInfo>> {
        return runCatching {
            scrapDataSource.getScrapList(userId).data.map {
                it.toScrapInfo()
            }
        }
    }

    override suspend fun updateRestaurantScrap(restaurantId: String, userId: String): List<String>? {
        runCatching {
            scrapDataSource.updateRestaurantScrap(restaurantId, userId)
        }.fold({
            return it.body()?.data?.restaurants
        }, {
            it.printStackTrace()
            return null
        })
    }
}
