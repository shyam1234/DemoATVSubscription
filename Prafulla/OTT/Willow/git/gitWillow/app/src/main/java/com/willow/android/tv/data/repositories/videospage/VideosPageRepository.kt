package com.willow.android.tv.data.repositories.videospage

import android.app.Application
import com.willow.android.tv.data.repositories.videospage.remote.VideosPageRemoteDataSource

class VideosPageRepository{ fun getVideosPageData(application: Application, isRemote: Boolean = true): IVideosPageConfig {
        return if (!isRemote) {
            VideosPageRemoteDataSource(application)
        } else {
            VideosPageRemoteDataSource(application)
        }
    }

}