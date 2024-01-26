package com.willow.android.tv.data.repositories.tvconfig.local

import android.app.Application
import com.willow.android.R
import com.willow.android.tv.data.repositories.parser.ConfigParser
import com.willow.android.tv.data.repositories.tvconfig.ITVConfigRepository
import com.willow.android.tv.data.repositories.tvconfig.datamodel.APIDFPConfigDataModel
import com.willow.android.tv.data.repositories.tvconfig.datamodel.APITVConfigDataModel
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.Utils

class TVConfigLocalDataSource(private val application: Application) : ITVConfigRepository {

    private val _tvConfig: APITVConfigDataModel? by lazy {
        val jsonString = Utils.readJsonFromFile(application, R.raw.tvconfig_api)
        ConfigParser.loadTVConfigFromJson(jsonString)
    }

    private val _dfpConfig: APIDFPConfigDataModel? by lazy {
        val jsonString = Utils.readJsonFromFile(application, R.raw.v1_ads_user_config)
        ConfigParser.loadDFPConfigFromJson(jsonString)
    }

    private val _countryConfig: String? by lazy {
        val jsonString = Utils.readJsonFromFile(application, R.raw.country)
        ConfigParser.loadCurrentCountryConfigFromJson(jsonString)
    }

    override suspend fun getTVConfig(): Resource<APITVConfigDataModel> {
        return Resource.Success(data =_tvConfig!!)
    }

    override suspend fun getDfpConfig(url:String): Resource<APIDFPConfigDataModel> {
        return Resource.Success(data = _dfpConfig!!)
    }

    override suspend fun getCountryConfig(countryCheckEndPoint: String): Resource<String> {
        return Resource.Success(data =_countryConfig!!)
    }
}