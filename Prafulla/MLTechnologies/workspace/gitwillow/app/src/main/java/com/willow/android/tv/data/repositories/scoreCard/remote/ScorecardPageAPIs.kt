package com.willow.android.tv.data.repositories.fixturespage.remote

import com.willow.android.tv.data.repositories.scoreCard.datamodel.ApiMatchInfoDataModel
import com.willow.android.tv.data.repositories.scoreCard.datamodel.ApiScorecardDataModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url


interface ScorecardPageAPIs {

    @GET
    suspend fun getScorecardPageDetails(@Url url: String) : Response<ApiScorecardDataModel>

    @GET
    suspend fun getMatchInfoPageDetails(@Url url: String) : Response<ApiMatchInfoDataModel>
}