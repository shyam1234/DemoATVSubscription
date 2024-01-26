package com.willow.android.tv.data.repositories.resultspage

import android.app.Application
import com.willow.android.tv.data.repositories.resultspage.remote.ResultsPageRemoteDataSource

class ResultsPageRepository{ fun getResultsPageData(application: Application, isRemote: Boolean = true): IResultsPageConfig {
        return if (!isRemote) {
            ResultsPageRemoteDataSource(application)
        } else {
            ResultsPageRemoteDataSource(application)
        }
    }

}