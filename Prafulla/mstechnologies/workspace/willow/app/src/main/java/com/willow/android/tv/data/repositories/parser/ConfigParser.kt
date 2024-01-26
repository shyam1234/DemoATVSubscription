package com.willow.android.tv.data.repositories.parser

import com.google.gson.Gson
import com.willow.android.tv.data.repositories.commondatamodel.CommonCardRow
import com.willow.android.tv.data.repositories.tvconfig.datamodel.APIDFPConfigDataModel
import com.willow.android.tv.data.repositories.tvconfig.datamodel.APITVConfigDataModel

object ConfigParser {

    fun loadTVConfigFromJson(jsonString: String): APITVConfigDataModel? {
        return Gson().fromJson(jsonString, APITVConfigDataModel::class.java)
    }

    fun loadExplorePageConfigFromJson(jsonString: String): CommonCardRow? {
        return Gson().fromJson(jsonString, CommonCardRow::class.java)
    }

    fun loadDFPConfigFromJson(jsonString: String): APIDFPConfigDataModel? {
        return Gson().fromJson(jsonString, APIDFPConfigDataModel::class.java)
    }

    fun loadCurrentCountryConfigFromJson(jsonString: String): String? {
        return jsonString
    }
}