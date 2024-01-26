package com.willow.android.tv.data.repositories.fixturespage.remote

import com.willow.android.tv.data.repositories.fixturespage.datamodel.APIFixturesDataModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url


interface FixturesPageAPIs {

    @GET
    suspend fun getFixturesPagePayload(@Url url: String) : Response<APIFixturesDataModel>
}