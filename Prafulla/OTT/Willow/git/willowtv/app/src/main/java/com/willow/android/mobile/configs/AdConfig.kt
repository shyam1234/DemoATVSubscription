package com.willow.android.mobile.configs

import android.util.Log
import org.json.JSONException
import org.json.JSONObject

object AdConfig {
    const val defaultShowAdForLive = true
    const val defaultShowAdForVod = true
    var mainConfig: JSONObject = JSONObject()
    var userConfig: JSONObject = JSONObject()

    fun setCloudData(data: JSONObject) {
        val deviceType = Keys.devType.trim().lowercase()

        try {
            val deviceConfiguration: JSONObject = data.getJSONObject(deviceType)
            try {
                mainConfig = deviceConfiguration.getJSONObject("main_config")
            } catch (e: JSONException) {
                Log.e("AdConfig: ", "main_config Missing")
            }
            try {
                userConfig = deviceConfiguration.getJSONObject("user_config")
            } catch (e: JSONException) {
                Log.e("AdConfig: ", "user_config Missing")
            }
        } catch (e: JSONException) {
            Log.e("AdConfig: ", "Device Configuration Missing")
        }
    }
}