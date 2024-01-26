package com.willow.android.tv.data.repositories.explorepage.local

import android.app.Application
import com.willow.android.R
import com.willow.android.tv.data.repositories.BaseRemoteDataSource
import com.willow.android.tv.data.repositories.commondatamodel.CommonCardRow
import com.willow.android.tv.data.repositories.explorepage.IExplorePageConfig
import com.willow.android.tv.data.repositories.parser.ConfigParser
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.Utils

class ExplorePageLocalDataSource(private val application: Application) : BaseRemoteDataSource(),
    IExplorePageConfig {

    private val _exploreCardRow: CommonCardRow? by lazy {
        val jsonString = Utils.readJsonFromFile(application, R.raw.explore)
        ConfigParser.loadExplorePageConfigFromJson(jsonString)
    }

    override suspend fun getExplorePageConfig(): Resource<CommonCardRow> {
        return Resource.Success(data = _exploreCardRow!!)
    }


}