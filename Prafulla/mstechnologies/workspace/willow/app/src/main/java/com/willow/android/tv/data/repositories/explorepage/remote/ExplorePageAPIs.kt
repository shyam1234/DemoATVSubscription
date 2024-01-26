package com.willow.android.tv.data.repositories.explorepage.remote

import com.willow.android.tv.data.repositories.commondatamodel.CommonCardRow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url


interface ExplorePageAPIs {

    @GET
    suspend fun getExplorePagePayload(@Url url: String) : Response<CommonCardRow>
}