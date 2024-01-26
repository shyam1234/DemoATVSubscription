package com.willow.android.tv.data.repositories.matchcenterpage.remote

import com.willow.android.tv.data.repositories.matchcenterpage.datamodel.APIMatchCenterDataModel
import com.willow.android.tv.data.repositories.matchcenterpage.datamodel.APINewMatchCenterDataModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url


interface MatchCenterPageAPIs {

    @GET
    suspend fun getMatchCenterPagePayload(@Url url: String) : Response<APIMatchCenterDataModel>
    @GET
    suspend fun getMatchCenterPagePayloadNew(@Url url: String) : Response<APINewMatchCenterDataModel>
}