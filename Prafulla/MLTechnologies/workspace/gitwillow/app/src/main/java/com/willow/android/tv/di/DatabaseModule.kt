package com.willow.android.tv.di

import android.app.Application
import androidx.room.Room
import com.willow.android.tv.data.room.db.AppDatabase
import com.willow.android.tv.data.room.db.VideoProgressDao
import com.willow.android.tv.utils.config.GlobalTVConfig
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by eldhosepaul on 14/02/23.
 */
@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, GlobalTVConfig.DB_NAME).build()
    }

    @Provides
    @Singleton
    fun provideVideoProgressDao(appDatabase: AppDatabase): VideoProgressDao {
        return appDatabase.videoProgressDao()
    }
}