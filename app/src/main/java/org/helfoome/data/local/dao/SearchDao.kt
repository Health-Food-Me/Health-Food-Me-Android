package org.helfoome.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.helfoome.data.local.entity.SearchData

@Dao
interface SearchDao {
    @Query("SELECT * FROM search_data_table")
    fun getAll(): Flow<List<SearchData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyword(searchData: SearchData)

    @Query("DELETE FROM search_data_table WHERE keyword = :keyword")
    suspend fun deleteKeyword(keyword: String)
}
