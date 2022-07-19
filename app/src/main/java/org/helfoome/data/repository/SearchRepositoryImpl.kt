package org.helfoome.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import org.helfoome.data.datasource.RemoteSearchDataSource
import org.helfoome.data.local.dao.SearchDao
import org.helfoome.data.local.entity.SearchData
import org.helfoome.domain.entity.RecentSearchInfo
import org.helfoome.domain.repository.SearchRepository

class SearchRepositoryImpl(
    private val searchDao: SearchDao,
    private val searchDataSource: RemoteSearchDataSource
) : SearchRepository {
    override fun getRecentKeyword(): Flow<List<RecentSearchInfo>> = flow {
        searchDao.getAll().buffer().collect {
            emit(
                it.map { data ->
                    RecentSearchInfo(data.keyword)
                }.reversed()
            )
        }
    }

    override suspend fun insertKeyword(item: RecentSearchInfo) = searchDao.insertKeyword(SearchData(item.keyword))

    override suspend fun removeKeyword(item: RecentSearchInfo) = searchDao.deleteKeyword(SearchData(item.keyword))

    override suspend fun getSearchAutoComplete(query: String) = runCatching {
        searchDataSource.getSearchAutoComplete(query).data.map {
            it.toAutoCompleteKeywordInfo()
        }
    }
}
