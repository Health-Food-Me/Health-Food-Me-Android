package org.helfoome.data.datasource

import org.helfoome.data.service.SearchService
import javax.inject.Inject

class RemoteSearchDataSource @Inject constructor(
    private val searchService: SearchService
) {
    suspend fun getSearchAutoComplete(query: String) = searchService.getSearchAutoComplete(query)
}