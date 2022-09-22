package org.helfoome.domain.repository

import kotlinx.coroutines.flow.Flow
import org.helfoome.domain.entity.AutoCompleteKeywordInfo
import org.helfoome.domain.entity.RecentSearchInfo
import org.helfoome.domain.entity.SearchResultInfo

interface SearchRepository {
    fun getRecentKeyword(): Flow<List<RecentSearchInfo>>

    suspend fun insertKeyword(item: RecentSearchInfo)

    suspend fun removeKeyword(keyword: String)

    suspend fun getSearchAutoComplete(longitude: Double, latitude: Double, query: String): Result<List<AutoCompleteKeywordInfo>>

    suspend fun getSearchRestaurantCard(
        longtitude: Double,
        latitude: Double,
        keyword: String
    ): Result<List<SearchResultInfo>>

    suspend fun getSearchCategoryCard(
        longitude: Double,
        latitude: Double,
        keyword: String
    ): Result<List<SearchResultInfo>>
}
