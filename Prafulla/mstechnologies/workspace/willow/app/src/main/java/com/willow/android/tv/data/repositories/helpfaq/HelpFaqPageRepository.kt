package com.willow.android.tv.data.repositories.helpfaq

import android.app.Application
import com.willow.android.tv.data.repositories.helpfaq.remote.HelpFaqPageRemoteDataSource

class HelpFaqPageRepository {

    fun helpFaqPageData(application: Application): IHelpFaqPageConfig {
        return HelpFaqPageRemoteDataSource(application)
    }
}