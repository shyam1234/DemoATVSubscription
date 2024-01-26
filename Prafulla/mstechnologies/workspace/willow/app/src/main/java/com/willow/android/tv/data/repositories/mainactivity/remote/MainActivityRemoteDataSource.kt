package com.willow.android.tv.data.repositories.mainactivity.remote

import android.app.Application
import com.willow.android.WillowApplication
import com.willow.android.tv.data.repositories.BaseRemoteDataSource
import com.willow.android.tv.data.repositories.mainactivity.IMainActivityConfig
import com.willow.android.tv.data.repositories.mainactivity.datamodel.APICheckSubDataModel
import com.willow.android.tv.ui.main.model.UserModel
import com.willow.android.tv.utils.Resource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


class MainActivityRemoteDataSource(application: Application) : BaseRemoteDataSource(), IMainActivityConfig {

    init {
        (application as WillowApplication).applicationComponent.inject(this)
    }

    @Inject
    lateinit var loginPageAPIs: MainActivityAPIs


    override suspend fun getCheckSubscription(user: UserModel): Resource<APICheckSubDataModel> {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("action", user.action)
            .addFormDataPart("uid", user.uid)
            .addFormDataPart("authToken", user.authToken)
            .build()
        return safeApiCall { loginPageAPIs.getCheckSubPayload(requestBody) }
    }


}