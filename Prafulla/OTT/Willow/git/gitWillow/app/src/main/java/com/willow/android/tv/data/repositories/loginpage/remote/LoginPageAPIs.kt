package com.willow.android.tv.data.repositories.loginpage.remote

import com.willow.android.BuildConfig
import com.willow.android.tv.data.repositories.loginpage.datamodel.APICheckAccountDataModel
import com.willow.android.tv.data.repositories.loginpage.datamodel.APIForgotPassDataModel
import com.willow.android.tv.data.repositories.loginpage.datamodel.APILoginDataModel
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface LoginPageAPIs {

    @POST(BuildConfig.MOBI_AUTH_URL)
    suspend fun getLoginPagePayload(@Body loginUser: RequestBody) : Response<APILoginDataModel>

    @POST(BuildConfig.MOBI_AUTH_URL)
    suspend fun getSignUpPagePayload(@Body signUpUser: RequestBody) : Response<APILoginDataModel>

    @POST(BuildConfig.MOBI_AUTH_URL)
    suspend fun getCheckAccountPayload(@Body signUpUser: RequestBody) : Response<APICheckAccountDataModel>

    @POST(BuildConfig.MOBI_AUTH_URL)
    suspend fun getFgPasswordPayload(@Body signUpUser: RequestBody) : Response<APIForgotPassDataModel>

}