package com.willow.android.tv.data.repositories.explorepage

import android.app.Application
import com.willow.android.tv.data.repositories.explorepage.local.ExplorePageLocalDataSource
import com.willow.android.tv.data.repositories.explorepage.remote.ExplorePageRemoteDataSource

class ExplorePageRepository{ fun getExplorePageData(application: Application, isRemote: Boolean = true): IExplorePageConfig {
    return if (!isRemote) {
        ExplorePageLocalDataSource(application)
    } else {
        ExplorePageRemoteDataSource(application)
    }
}

}