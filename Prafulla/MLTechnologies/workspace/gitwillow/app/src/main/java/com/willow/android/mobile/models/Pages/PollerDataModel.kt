package com.willow.android.mobile.models.pages

import android.util.Log
import org.json.JSONObject
import java.io.Serializable


class PollerDataModel : Serializable {
    var status: String = ""
    var guid: String = ""

    fun setData(data: JSONObject) {
        try {
            status = data.getString("status")
        } catch (e: Exception) {
            Log.e("FieldError:", "PollerDataModel field: " + "result")
        }

        try {
            guid = data.getString("guid")
        } catch (e: Exception) {
            Log.e("FieldError:", "PollerDataModel field: " + "guid")
        }
    }
}