package com.willow.android.mobile.models

import android.util.Log
import org.json.JSONObject


class TVEConfigModel {
    val tve_providers: MutableList<TVEProviderConfigModel> = mutableListOf()

    fun setData(data: JSONObject) {
        try {
            val tveProviderArray = data.getJSONArray("tve_providers")
            for (i in 0 until tveProviderArray.length()) {
                val tveProviderJson = tveProviderArray[i] as? JSONObject
                if (tveProviderJson != null) {
                    val tveProviderConfigModel = TVEProviderConfigModel()
                    tveProviderConfigModel.setData(tveProviderJson)
                    tve_providers.add(tveProviderConfigModel)
                }
            }
        } catch (e: Exception) {
            Log.e("FieldError:", "TVEProviderConfigModel field: " + "tve_providers")
        }
    }
}

class TVEProviderConfigModel {
    var id: String = ""
    var image: String = ""
    var isAvailable: Boolean = false
    var message: String = ""

    fun setData(data: JSONObject) {
        try {
            id = data.getString("id")
        } catch (e: Exception) {
            Log.e("FieldError:", "TVEProviderConfigModel field: " + "id")
        }

        try {
            image = data.getString("image")
        } catch (e: Exception) {
            Log.e("FieldError:", "TVEProviderConfigModel field: " + "image")
        }

        try {
            isAvailable = data.getBoolean("isAvailable")
        } catch (e: Exception) {
            Log.e("FieldError:", "TVEProviderConfigModel field: " + "isAvailable")
        }

        try {
            message = data.getString("message")
        } catch (e: Exception) {
            Log.e("FieldError:", "TVEProviderConfigModel field: " + "message")
        }
    }
}