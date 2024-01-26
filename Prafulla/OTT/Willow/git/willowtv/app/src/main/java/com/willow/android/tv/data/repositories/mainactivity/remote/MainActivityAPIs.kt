package com.willow.android.tv.data.repositories.mainactivity.remote

import com.willow.android.BuildConfig
import com.willow.android.tv.data.repositories.mainactivity.datamodel.APICheckSubDataModel
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface MainActivityAPIs {

    @POST(BuildConfig.MOBI_AUTH_URL)
    suspend fun getCheckSubPayload(@Body loginUser: RequestBody) : Response<APICheckSubDataModel>


}