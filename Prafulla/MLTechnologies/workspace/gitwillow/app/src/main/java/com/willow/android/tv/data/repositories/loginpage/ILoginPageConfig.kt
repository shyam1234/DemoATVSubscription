package com.willow.android.tv.data.repositories.loginpage

import com.willow.android.tv.data.repositories.loginpage.datamodel.APICheckAccountDataModel
import com.willow.android.tv.data.repositories.loginpage.datamodel.APIForgotPassDataModel
import com.willow.android.tv.data.repositories.loginpage.datamodel.APILoginDataModel
import com.willow.android.tv.ui.login.model.LoginUserModel
import com.willow.android.tv.utils.Resource

interface ILoginPageConfig {
    suspend fun getLoginPage(loginUser: LoginUserModel): Resource<APILoginDataModel>

    suspend fun getSignupUser(signUpUser: LoginUserModel): Resource<APILoginDataModel>

    suspend fun getCheckAccount(signUpUser: LoginUserModel): Resource<APICheckAccountDataModel>
    suspend fun getForgotPassword(signUpUser: LoginUserModel): Resource<APIForgotPassDataModel>

}