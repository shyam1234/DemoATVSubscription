package com.willow.android.tv.data.repositories.matchcenterpage

import android.app.Application
import com.willow.android.tv.data.repositories.matchcenterpage.remote.MatchCenterPageRemoteDataSource

class MatchCenterPageRepository{
    fun getMatchCenterPageData(application: Application, isRemote: Boolean = true): IMatchCenterPageConfig {
        return if (!isRemote) {
            MatchCenterPageRemoteDataSource(application)
        } else {
            MatchCenterPageRemoteDataSource(application)
        }
    }

}