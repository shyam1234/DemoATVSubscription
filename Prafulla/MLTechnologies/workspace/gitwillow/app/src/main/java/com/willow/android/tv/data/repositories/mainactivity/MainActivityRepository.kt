package com.willow.android.tv.data.repositories.mainactivity

import android.app.Application
import com.willow.android.tv.data.repositories.mainactivity.remote.MainActivityRemoteDataSource

class MainActivityRepository{

    /**
     * Return a GetLoginData from specific repository implementation.
     */
    fun getCheckSubData(application: Application): IMainActivityConfig {

        return  MainActivityRemoteDataSource(application)

    }


}