package com.willow.android.mobile.models.pages

import android.util.Log
import org.json.JSONObject


class IAPOfferModel {
    val offers: MutableList<String> = mutableListOf()

    fun setData(data: String) {
        try {
            val dataJson = JSONObject(data)
            val resultJson = dataJson.getJSONArray("offers")
            for (i in 0 until resultJson.length()) {
                val offerString = resultJson[i] as? String
                if (offerString != null) {
                    offers.add(offerString)
                }
            }
        } catch (e: Exception) {
            Log.e("FieldError:", "IAPOfferModel field: " + "offers")
        }
    }
}
