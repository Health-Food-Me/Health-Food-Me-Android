package org.helfoome.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.helfoome.data.local.dao.SearchDao
import org.helfoome.data.local.entity.SearchData

@Database(entities = [SearchData::class], version = 1, exportSchema = false)
abstract class HFMDatabase : RoomDatabase() {
    abstract fun searchDao(): SearchDao

    companion object {
        // TODO : fallbackToDestructiveMigration() 걷어내고 Migration 코드 작성
        fun getInstance(context: Context): HFMDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                HFMDatabase::class.java,
                "health_food_me.db"
            ).fallbackToDestructiveMigration()
                .build()
        }
    }
}
