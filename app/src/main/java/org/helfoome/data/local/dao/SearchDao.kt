package org.helfoome.data.local.dao

import androidx.room.*
import org.helfoome.data.local.entity.SearchData

@Dao
interface SearchDao {
    @Query("SELECT * FROM search_data_table")
    suspend fun getAll(): List<SearchData>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(searchData: SearchData)

    @Delete
    suspend fun delete(searchData: SearchData)
}
