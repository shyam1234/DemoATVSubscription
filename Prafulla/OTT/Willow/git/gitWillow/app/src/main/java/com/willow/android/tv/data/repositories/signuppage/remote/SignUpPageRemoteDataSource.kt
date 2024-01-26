package com.willow.android.tv.data.repositories.signuppage.remote

import android.app.Application
import com.willow.android.WillowApplication
import com.willow.android.tv.data.repositories.BaseRemoteDataSource
import com.willow.android.tv.data.repositories.signuppage.ISignUpPageRepository
import com.willow.android.tv.data.repositories.signuppage.datamodel.APISIgnupDataModel
import com.willow.android.tv.ui.login.model.SignUpUserModel
import com.willow.android.tv.utils.Resource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


class SignUpPageRemoteDataSource(application: Application) : BaseRemoteDataSource(), ISignUpPageRepository {

    init {
        (application as WillowApplication).applicationComponent.inject(this)
    }

    @Inject
    lateinit var signupPageAPIs: SignupPageAPIs


    override suspend fun getSignupUser(signUpUser: SignUpUserModel): Resource<APISIgnupDataModel> {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("action", signUpUser.action)
            .addFormDataPart("fname", signUpUser.name)
            .addFormDataPart("email", signUpUser.email)
            .addFormDataPart("password", signUpUser.password)
            .addFormDataPart("authToken", signUpUser.authToken)
            .build()
        return safeApiCall {  signupPageAPIs.getSignUpPagePayload(requestBody) }
    }

}