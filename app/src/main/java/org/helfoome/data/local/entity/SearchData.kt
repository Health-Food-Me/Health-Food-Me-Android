package org.helfoome.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_data_table")
data class SearchData(
    @PrimaryKey
    val keyword: String,
    val isCategory: Boolean
)
