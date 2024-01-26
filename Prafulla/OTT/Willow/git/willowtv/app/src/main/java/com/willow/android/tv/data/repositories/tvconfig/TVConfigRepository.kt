package com.willow.android.tv.data.repositories.tvconfig

import android.app.Application
import com.willow.android.tv.data.repositories.tvconfig.local.TVConfigLocalDataSource
import com.willow.android.tv.data.repositories.tvconfig.remote.TVConfigRemoteDataSource

class TVConfigRepository{

    /**
     * Return a getTVConfig from specific repository implementation.
     */
    fun getTVConfigData(application: Application,isRemote: Boolean = true): ITVConfigRepository {
        return if (!isRemote) {
            TVConfigLocalDataSource(application)
        } else {
            TVConfigRemoteDataSource(application)
        }
    }

    fun getDFPConfigData(application: Application,isRemote: Boolean = true): ITVConfigRepository {
        return if (!isRemote) {
            TVConfigLocalDataSource(application)
        } else {
            TVConfigRemoteDataSource(application)
        }
    }

    fun getCountryCode(application: Application,isRemote: Boolean = true): ITVConfigRepository{
        return if (!isRemote) {
            TVConfigLocalDataSource(application)
        } else {
            TVConfigRemoteDataSource(application)
        }
    }
}