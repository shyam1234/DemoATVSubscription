package com.willow.android.tv.data.repositories.scoreCard


import com.willow.android.tv.data.repositories.scoreCard.datamodel.ApiMatchInfoDataModel
import com.willow.android.tv.data.repositories.scoreCard.datamodel.ApiScorecardDataModel
import com.willow.android.tv.utils.Resource

interface IScorecardPageConfig {
    suspend fun getScorecardPageDetails(url: String): Resource<ApiScorecardDataModel>
    suspend fun getMatchInfoPageDetails(url: String) : Resource<ApiMatchInfoDataModel>
}