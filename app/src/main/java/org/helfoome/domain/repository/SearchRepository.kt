package org.helfoome.domain.repository

import kotlinx.coroutines.flow.Flow
import org.helfoome.domain.entity.RecentSearchInfo

interface SearchRepository {
    fun getRecentKeyword(): Flow<List<RecentSearchInfo>>

    suspend fun insertKeyword(item: RecentSearchInfo)

    suspend fun removeKeyword(item: RecentSearchInfo)
}