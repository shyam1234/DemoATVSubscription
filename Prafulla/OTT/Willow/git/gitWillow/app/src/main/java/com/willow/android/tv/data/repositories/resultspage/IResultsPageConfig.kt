package com.willow.android.tv.data.repositories.resultspage


import com.willow.android.tv.data.repositories.resultspage.datamodel.APIResultsDataModel
import com.willow.android.tv.utils.Resource

interface IResultsPageConfig {
    suspend fun getResultsPageConfig() : Resource<APIResultsDataModel>
}