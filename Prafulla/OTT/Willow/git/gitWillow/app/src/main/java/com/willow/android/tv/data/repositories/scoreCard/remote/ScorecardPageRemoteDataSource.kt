package com.willow.android.tv.data.repositories.fixturespage.remote

import android.app.Application
import com.willow.android.WillowApplication
import com.willow.android.tv.data.repositories.BaseRemoteDataSource
import com.willow.android.tv.data.repositories.scoreCard.IScorecardPageConfig
import com.willow.android.tv.data.repositories.scoreCard.datamodel.ApiMatchInfoDataModel
import com.willow.android.tv.data.repositories.scoreCard.datamodel.ApiScorecardDataModel
import com.willow.android.tv.utils.Resource
import javax.inject.Inject

class ScorecardPageRemoteDataSource(application: Application) : BaseRemoteDataSource(), IScorecardPageConfig {

    init {
        (application  as WillowApplication).applicationComponent.inject(this)
    }

    @Inject
    lateinit var scorecardPageAPIs: ScorecardPageAPIs

    override suspend fun getScorecardPageDetails(url: String): Resource<ApiScorecardDataModel> {
        return safeApiCall { scorecardPageAPIs.getScorecardPageDetails(url) }    }

    override suspend fun getMatchInfoPageDetails(url: String): Resource<ApiMatchInfoDataModel> {
        return safeApiCall { scorecardPageAPIs.getMatchInfoPageDetails(url) }
    }
}