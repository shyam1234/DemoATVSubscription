package com.willow.android.tv.data.repositories.loginpage

import android.app.Application
import com.willow.android.tv.data.repositories.loginpage.remote.LoginPageRemoteDataSource
import com.willow.android.tv.data.repositories.signuppage.ISignUpPageRepository
import com.willow.android.tv.data.repositories.signuppage.remote.SignUpPageRemoteDataSource

class LoginPageRepository{

    /**
     * Return a GetLoginData from specific repository implementation.
     */
    fun getLoginPageData(application: Application): ILoginPageConfig {

        return  LoginPageRemoteDataSource(application)

    }

    fun getSignUpPageData(application: Application): ISignUpPageRepository {

        return  SignUpPageRemoteDataSource(application)

    }


}