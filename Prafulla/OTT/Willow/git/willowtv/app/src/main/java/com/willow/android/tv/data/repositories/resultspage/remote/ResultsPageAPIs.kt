package com.willow.android.tv.data.repositories.resultspage.remote

import com.willow.android.tv.data.repositories.resultspage.datamodel.APIResultsDataModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url


interface ResultsPageAPIs {

    @GET
    suspend fun getResultsPagePayload(@Url url: String) : Response<APIResultsDataModel>
}