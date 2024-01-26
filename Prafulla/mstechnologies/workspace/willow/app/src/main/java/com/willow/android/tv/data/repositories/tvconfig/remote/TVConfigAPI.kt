package com.willow.android.tv.data.repositories.tvconfig.remote

import com.willow.android.tv.data.repositories.tvconfig.datamodel.APIDFPConfigDataModel
import com.willow.android.tv.data.repositories.tvconfig.datamodel.APITVConfigDataModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface TVConfigAPI {

    @GET("apps/conf/AndroidTVConfig.json")
    suspend fun getTVConfig() : Response<APITVConfigDataModel>

    @GET
    suspend fun getDfpConfigData(@Url url: String):Response<APIDFPConfigDataModel>

    @GET
    suspend fun getCountryCode(@Url url: String):Response<String>

}