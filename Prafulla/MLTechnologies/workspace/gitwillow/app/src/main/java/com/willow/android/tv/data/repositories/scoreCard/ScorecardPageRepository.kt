package com.willow.android.tv.data.repositories.fixturespage

import android.app.Application
import com.willow.android.tv.data.repositories.fixturespage.remote.ScorecardPageRemoteDataSource
import com.willow.android.tv.data.repositories.scoreCard.IScorecardPageConfig

class ScorecardPageRepository {
    fun getScorecardPageData(
        application: Application,
        isRemote: Boolean = true
    ): IScorecardPageConfig {
        return if (!isRemote) {
            ScorecardPageRemoteDataSource(application)
        } else {
            ScorecardPageRemoteDataSource(application)
        }
    }

}