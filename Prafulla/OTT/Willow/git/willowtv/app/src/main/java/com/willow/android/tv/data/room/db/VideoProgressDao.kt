package com.willow.android.tv.data.room.db

import androidx.room.*
import com.willow.android.tv.data.room.entities.VideoProgress
import java.util.*

/**
 * Created by eldhosepaul on 14/02/23.
 */

@Dao
interface VideoProgressDao {

    @Query("SELECT * FROM VideoProgress WHERE videoId = :videoId")
    suspend fun getVideoProgressByVideoId(videoId: Int): VideoProgress

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateVideoProgress(videoProgress: VideoProgress)

    @Query("DELETE FROM VideoProgress WHERE videoId = :videoId")
    suspend fun deleteByVideoId(videoId: Int): Int

    @Query("DELETE FROM VideoProgress")
    fun deleteAll() // Define deleteAll() method to delete all data

//    @Insert
//    fun insert(videoProgress: VideoProgress)
//
//    @Query("SELECT * FROM video_progress WHERE video_id = :videoId ORDER BY timestamp DESC LIMIT 1")
//    fun getLatestProgress(videoId: Int): VideoProgress
//
//    @Query("UPDATE video_progress SET progress = :progress, timestamp = :timestamp WHERE id = :id")
//    fun update(id: Int, progress: Int, timestamp: Date)
}