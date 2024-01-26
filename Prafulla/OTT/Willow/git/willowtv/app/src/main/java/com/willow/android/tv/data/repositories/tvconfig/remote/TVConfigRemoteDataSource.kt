package com.willow.android.tv.data.repositories.tvconfig.remote

import android.app.Application
import com.willow.android.WillowApplication
import com.willow.android.tv.data.repositories.BaseRemoteDataSource
import com.willow.android.tv.data.repositories.tvconfig.ITVConfigRepository
import com.willow.android.tv.data.repositories.tvconfig.datamodel.APIDFPConfigDataModel
import com.willow.android.tv.data.repositories.tvconfig.datamodel.APITVConfigDataModel
import com.willow.android.tv.utils.Resource
import javax.inject.Inject

class TVConfigRemoteDataSource(application: Application) : BaseRemoteDataSource(), ITVConfigRepository {

    init {
        (application as WillowApplication).applicationComponent.inject(this)
    }

    @Inject
    lateinit var tvConfigAPI: TVConfigAPI

    override suspend fun getTVConfig(): Resource<APITVConfigDataModel> {
        return safeApiCall { tvConfigAPI.getTVConfig() }

    }

    override suspend fun getDfpConfig(url :String): Resource<APIDFPConfigDataModel> {
        return safeApiCall { tvConfigAPI.getDfpConfigData(url) }

    }

    override suspend fun getCountryConfig(countryCheckEndPoint: String): Resource<String> {
        return safeApiCall { tvConfigAPI.getCountryCode(countryCheckEndPoint) }

    }
    
}