package com.willow.android.tv.data.repositories.matchcenterpage.remote

import android.app.Application
import com.willow.android.WillowApplication
import com.willow.android.tv.data.repositories.BaseRemoteDataSource
import com.willow.android.tv.data.repositories.matchcenterpage.IMatchCenterPageConfig
import com.willow.android.tv.data.repositories.matchcenterpage.datamodel.APIMatchCenterDataModel
import com.willow.android.tv.data.repositories.matchcenterpage.datamodel.APINewMatchCenterDataModel
import com.willow.android.tv.utils.Resource
import javax.inject.Inject

class MatchCenterPageRemoteDataSource(application: Application) : BaseRemoteDataSource(),
    IMatchCenterPageConfig {

    init {
        (application  as WillowApplication).applicationComponent.inject(this)
    }

    @Inject
    lateinit var matchCenterPageAPIs: MatchCenterPageAPIs

    override suspend fun getMatchCenterPageConfig(url:String): Resource<APIMatchCenterDataModel> {
        val data = matchCenterPageAPIs.getMatchCenterPagePayload(url)

        return safeApiCall { data }

    }
    override suspend fun getMatchCenterPageConfigNew(url:String): Resource<APINewMatchCenterDataModel> {

        return safeApiCall { matchCenterPageAPIs.getMatchCenterPagePayloadNew(url) }

    }
}