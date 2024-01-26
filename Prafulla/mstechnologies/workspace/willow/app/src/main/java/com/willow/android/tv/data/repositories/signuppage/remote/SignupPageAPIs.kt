package com.willow.android.tv.data.repositories.signuppage.remote

import com.willow.android.BuildConfig
import com.willow.android.tv.data.repositories.signuppage.datamodel.APISIgnupDataModel
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface SignupPageAPIs {

    @POST(BuildConfig.MOBI_AUTH_URL)
    suspend fun getSignUpPagePayload(@Body signUpUser: RequestBody) : Response<APISIgnupDataModel>
}