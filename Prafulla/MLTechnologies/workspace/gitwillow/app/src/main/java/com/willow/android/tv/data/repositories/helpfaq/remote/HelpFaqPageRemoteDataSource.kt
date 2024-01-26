package com.willow.android.tv.data.repositories.helpfaq.remote

import android.app.Application
import com.willow.android.WillowApplication
import com.willow.android.tv.data.repositories.BaseRemoteDataSource
import com.willow.android.tv.data.repositories.helpfaq.IHelpFaqPageConfig
import com.willow.android.tv.data.repositories.helpfaq.datamodel.ApiHelpDataModel
import com.willow.android.tv.utils.Resource
import javax.inject.Inject

class HelpFaqPageRemoteDataSource(application: Application) : BaseRemoteDataSource(), IHelpFaqPageConfig {

    init {
        (application as WillowApplication).applicationComponent.inject(this)
    }

    @Inject
    lateinit var helpFaqPageAPIs: HelpFaqPageAPIs

    override suspend fun getHelpPageData(url: String): Resource<ApiHelpDataModel> {
        return safeApiCall { helpFaqPageAPIs.getHelpPagePayload(url) }
    }
}