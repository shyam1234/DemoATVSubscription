package com.willow.android.tv.data.repositories.helpfaq.remote

import com.willow.android.tv.data.repositories.helpfaq.datamodel.ApiHelpDataModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface HelpFaqPageAPIs {

    @GET
    suspend fun getHelpPagePayload(@Url url: String) : Response<ApiHelpDataModel>
}