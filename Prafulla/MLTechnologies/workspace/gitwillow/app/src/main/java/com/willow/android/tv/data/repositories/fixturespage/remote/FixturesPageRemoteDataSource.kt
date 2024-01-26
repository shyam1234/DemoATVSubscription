package com.willow.android.tv.data.repositories.fixturespage.remote

import android.app.Application
import com.willow.android.WillowApplication
import com.willow.android.tv.common.Types
import com.willow.android.tv.data.repositories.BaseRemoteDataSource
import com.willow.android.tv.data.repositories.fixturespage.IFixturesPageConfig
import com.willow.android.tv.data.repositories.fixturespage.datamodel.APIFixturesDataModel
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.config.GlobalTVConfig
import javax.inject.Inject

class FixturesPageRemoteDataSource(application: Application) : BaseRemoteDataSource(), IFixturesPageConfig {

    init {
        (application  as WillowApplication).applicationComponent.inject(this)
    }

    @Inject
    lateinit var fixturesPageAPIs: FixturesPageAPIs

    override suspend fun getFixturesPageConfig(): Resource<APIFixturesDataModel> {

        return safeApiCall {
            GlobalTVConfig.getScreenPageURL(Types.ScreenType.FIXTURES)
                ?.let { fixturesPageAPIs.getFixturesPagePayload(it) }
        }

    }
}