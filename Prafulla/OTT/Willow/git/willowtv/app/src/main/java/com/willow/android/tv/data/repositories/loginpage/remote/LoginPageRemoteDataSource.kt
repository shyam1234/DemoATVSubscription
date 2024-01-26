package com.willow.android.tv.data.repositories.loginpage.remote

import android.app.Application
import com.willow.android.WillowApplication
import com.willow.android.tv.data.repositories.BaseRemoteDataSource
import com.willow.android.tv.data.repositories.loginpage.ILoginPageConfig
import com.willow.android.tv.data.repositories.loginpage.datamodel.APICheckAccountDataModel
import com.willow.android.tv.data.repositories.loginpage.datamodel.APIForgotPassDataModel
import com.willow.android.tv.data.repositories.loginpage.datamodel.APILoginDataModel
import com.willow.android.tv.ui.login.model.LoginUserModel
import com.willow.android.tv.utils.Resource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


class LoginPageRemoteDataSource(application: Application) : BaseRemoteDataSource(), ILoginPageConfig {

    init {
        (application as WillowApplication).applicationComponent.inject(this)
    }

    @Inject
    lateinit var loginPageAPIs: LoginPageAPIs


    override suspend fun getLoginPage(loginUser: LoginUserModel): Resource<APILoginDataModel> {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("action", loginUser.action)
            .addFormDataPart("email", loginUser.email.toString())
            .addFormDataPart("password", loginUser.password.toString())
            .addFormDataPart("authToken", loginUser.authToken)
            .build()
        val data = loginPageAPIs.getLoginPagePayload(requestBody)
        return safeApiCall { data }
    }

    override suspend fun getSignupUser(signUpUser: LoginUserModel): Resource<APILoginDataModel> {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("action", signUpUser.action)
            .addFormDataPart("email", signUpUser.email.toString())
            .addFormDataPart("password", signUpUser.password.toString())
            .addFormDataPart("authToken", signUpUser.authToken)
            .build()
        return safeApiCall { loginPageAPIs.getSignUpPagePayload(requestBody) }
    }

    override suspend fun getCheckAccount(user: LoginUserModel): Resource<APICheckAccountDataModel> {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("action", user.action)
            .addFormDataPart("email", user.email.toString())
            .addFormDataPart("authToken", user.authToken)
            .build()

        return safeApiCall { loginPageAPIs.getCheckAccountPayload(requestBody) }
    }

    override suspend fun getForgotPassword(user: LoginUserModel): Resource<APIForgotPassDataModel> {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("action", user.action)
            .addFormDataPart("email", user.email.toString())
            .addFormDataPart("authToken", user.authToken)
            .build()
        return safeApiCall {  loginPageAPIs.getFgPasswordPayload(requestBody) }
    }

}