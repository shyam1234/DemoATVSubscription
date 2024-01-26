package com.willow.android.tv.ui.playback.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.willow.android.WillowApplication
import com.willow.android.tv.data.room.db.VideoProgressDao


class PlaybackViewModelFactory(
    val application: WillowApplication,
    private val videoProgressDao: VideoProgressDao,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlaybackViewmodel(application, videoProgressDao) as T
    }
}

