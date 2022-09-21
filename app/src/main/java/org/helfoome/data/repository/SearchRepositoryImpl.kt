package org.helfoome.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import org.helfoome.data.datasource.RemoteSearchDataSource
import org.helfoome.data.local.dao.SearchDao
import org.helfoome.data.local.entity.SearchData
import org.helfoome.domain.entity.RecentSearchInfo
import org.helfoome.domain.entity.SearchResultInfo
import org.helfoome.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchDao: SearchDao,
    private val searchDataSource: RemoteSearchDataSource,
) : SearchRepository {
    override fun getRecentKeyword(): Flow<List<RecentSearchInfo>> = searchDao.getAll().buffer().map {
        it.map { data ->
            RecentSearchInfo(data.keyword, data.isCategory)
        }.reversed()
    }

    override suspend fun insertKeyword(item: RecentSearchInfo) = searchDao.insertKeyword(SearchData(item.keyword, item.isCategory))

    override suspend fun removeKeyword(keyword: String) = searchDao.deleteKeyword(keyword)

    override suspend fun getSearchAutoComplete(longitude: Double, latitude: Double, query: String) = runCatching {
        searchDataSource.getSearchAutoComplete(
            longitude,
            latitude,
            query
        ).data.map {
            it.toAutoCompleteKeywordInfo()
        }
    }

    override suspend fun getSearchRestaurantCard(
        longtitude: Double,
        latitude: Double,
        keyword: String,
    ) = runCatching {
        searchDataSource.getSearchRestaurantCard(
            longtitude,
            latitude,
            keyword
        ).data.map {
            it.toSearchResultInfo()
        }
    }

    override suspend fun getSearchCategoryCard(longitude: Double, latitude: Double, keyword: String): Result<List<SearchResultInfo>> =
        runCatching {
            searchDataSource.getSearchRestaurantCard(
                longitude,
                latitude,
                keyword
            ).data.map {
                it.toSearchResultInfo()
            }
        }
}
