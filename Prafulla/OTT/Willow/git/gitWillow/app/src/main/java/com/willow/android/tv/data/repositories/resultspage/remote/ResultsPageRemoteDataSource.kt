package com.willow.android.tv.data.repositories.resultspage.remote

import android.app.Application
import com.willow.android.WillowApplication
import com.willow.android.tv.common.Types
import com.willow.android.tv.data.repositories.BaseRemoteDataSource
import com.willow.android.tv.data.repositories.resultspage.IResultsPageConfig
import com.willow.android.tv.data.repositories.resultspage.datamodel.APIResultsDataModel
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.config.GlobalTVConfig
import javax.inject.Inject

class ResultsPageRemoteDataSource(application: Application) : BaseRemoteDataSource(), IResultsPageConfig {

    init {
        (application  as WillowApplication).applicationComponent.inject(this)
    }

    @Inject
    lateinit var resultsPageAPIs: ResultsPageAPIs

    override suspend fun getResultsPageConfig(): Resource<APIResultsDataModel> {

        return safeApiCall { GlobalTVConfig.getScreenPageURL(Types.ScreenType.RESULTS)
            ?.let { resultsPageAPIs.getResultsPagePayload(it) } }

    }
}