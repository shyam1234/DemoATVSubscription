package com.willow.android.tv.data.repositories.fixturespage

import android.app.Application
import com.willow.android.tv.data.repositories.fixturespage.remote.FixturesPageRemoteDataSource

class FixturesPageRepository{ fun getFixturesPageData(application: Application, isRemote: Boolean = true): IFixturesPageConfig {
        return if (!isRemote) {
            FixturesPageRemoteDataSource(application)
        } else {
            FixturesPageRemoteDataSource(application)
        }
    }

}