package com.willow.android.tv.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


/**
 * Created by eldhosepaul on 14/02/23.
 */

@Entity
data class VideoProgress(
    var videoId: Int,
    var progress: Double,
    var timestamp: Date,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)