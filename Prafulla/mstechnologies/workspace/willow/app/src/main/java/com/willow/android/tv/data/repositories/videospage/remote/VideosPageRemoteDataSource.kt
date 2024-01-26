package com.willow.android.tv.data.repositories.videospage.remote

import android.app.Application
import com.willow.android.WillowApplication
import com.willow.android.tv.common.Types
import com.willow.android.tv.data.repositories.BaseRemoteDataSource
import com.willow.android.tv.data.repositories.commondatamodel.CommonCardRow
import com.willow.android.tv.data.repositories.videospage.IVideosPageConfig
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.config.GlobalTVConfig
import javax.inject.Inject

class VideosPageRemoteDataSource(application: Application) : BaseRemoteDataSource(), IVideosPageConfig {

    init {
        (application  as WillowApplication).applicationComponent.inject(this)
    }

    @Inject
    lateinit var videosPageAPIs: VideosPageAPIs

    override suspend fun getVideosPageConfig(url: String?): Resource<CommonCardRow> {
        return safeApiCall { (url?: GlobalTVConfig.getScreenPageURL(Types.ScreenType.VIDEOS))?.let {
            videosPageAPIs.getVideosPagePayload(it)
        } }

    }
}