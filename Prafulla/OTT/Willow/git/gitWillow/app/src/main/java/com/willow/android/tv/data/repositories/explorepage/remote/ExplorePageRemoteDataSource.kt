package com.willow.android.tv.data.repositories.explorepage.remote

import android.app.Application
import com.willow.android.WillowApplication
import com.willow.android.tv.common.Types
import com.willow.android.tv.data.repositories.BaseRemoteDataSource
import com.willow.android.tv.data.repositories.commondatamodel.CommonCardRow
import com.willow.android.tv.data.repositories.explorepage.IExplorePageConfig
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.config.GlobalTVConfig
import javax.inject.Inject

class ExplorePageRemoteDataSource(application: Application) : BaseRemoteDataSource(), IExplorePageConfig {

    init {
        (application  as WillowApplication).applicationComponent.inject(this)
    }

    @Inject
    lateinit var explorePageAPIs: ExplorePageAPIs

    override suspend fun getExplorePageConfig(): Resource<CommonCardRow> {
        return safeApiCall {
            GlobalTVConfig.getScreenPageURL(Types.ScreenType.EXPLORE)
                ?.let { explorePageAPIs.getExplorePagePayload(it) }
        }
    }
}