package com.willow.android.tv.data.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.willow.android.tv.data.room.entities.VideoProgress

/**
 * Created by eldhosepaul on 14/02/23.
 */

@Database(entities = [VideoProgress::class], version = 1)
@TypeConverters(DateTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoProgressDao(): VideoProgressDao

    override fun clearAllTables() {
        videoProgressDao().deleteAll()
    }

}