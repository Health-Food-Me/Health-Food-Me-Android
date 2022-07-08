package org.helfoome.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_data_table")
data class SearchData(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    @ColumnInfo(name = "keyword")
    val keyword: String
)
