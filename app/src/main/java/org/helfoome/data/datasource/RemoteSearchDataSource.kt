package org.helfoome.data.datasource

import org.helfoome.data.service.SearchService
import javax.inject.Inject

class RemoteSearchDataSource @Inject constructor(
    private val searchService: SearchService
) {
    suspend fun getSearchAutoComplete(
        longitude: Double,
        latitude: Double,
        query: String
    ) = searchService.getSearchAutoComplete(longitude, latitude, query)

    suspend fun getSearchRestaurantCard(
        longtitude: Double,
        latitude: Double,
        keyword: String
    ) = searchService.getSearchRestaurantCard(longtitude, latitude, keyword)

    suspend fun getSearchCategoryCard(
        longitude: Double,
        latitude: Double,
        keyword: String
    ) = searchService.getSearchCategoryCard(longitude, latitude, keyword)
}
