package com.willow.android.tv.data.repositories.tvconfig

import com.willow.android.tv.data.repositories.tvconfig.datamodel.APIDFPConfigDataModel
import com.willow.android.tv.data.repositories.tvconfig.datamodel.APITVConfigDataModel
import com.willow.android.tv.utils.Resource

interface ITVConfigRepository {
    suspend fun getTVConfig(): Resource<APITVConfigDataModel>
    suspend fun getDfpConfig(url: String): Resource<APIDFPConfigDataModel>
    suspend fun getCountryConfig(countryCheckEndPoint: String): Resource<String>
}