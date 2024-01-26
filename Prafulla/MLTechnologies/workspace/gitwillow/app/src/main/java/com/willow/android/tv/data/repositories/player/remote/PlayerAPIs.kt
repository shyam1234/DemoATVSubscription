package com.willow.android.tv.data.repositories.player.remote

import com.willow.android.tv.data.repositories.player.datamodel.APIPollerDataModel
import com.willow.android.tv.data.repositories.player.datamodel.APIStreamingURLDataModel
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url


interface PlayerAPIs {

    @POST
    suspend fun getPlayerPayload(@Url url: String, @Body params: RequestBody) : Response<APIStreamingURLDataModel>?

    @GET
    suspend fun getPollerPayload(@Url url: String) : Response<APIPollerDataModel>


}