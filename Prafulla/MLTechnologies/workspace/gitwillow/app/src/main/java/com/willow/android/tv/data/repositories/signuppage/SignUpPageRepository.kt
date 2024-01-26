package com.willow.android.tv.data.repositories.signuppage

import android.app.Application
import com.willow.android.tv.data.repositories.signuppage.remote.SignUpPageRemoteDataSource

class SignUpPageRepository{


    fun getSignUpPageData(application: Application): ISignUpPageRepository {

        return  SignUpPageRemoteDataSource(application)

    }

}