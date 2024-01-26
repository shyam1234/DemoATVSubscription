package com.willow.android.mobile.models.pages

import android.util.Log
import org.json.JSONObject


class IAPReceiptVerificationModel {
    var accessValid: Boolean = false
    var message: String = ""
    var status: String = ""
    var user_id: String = "0"

    fun setData(data: JSONObject) {
        try {
            accessValid = data.getBoolean("accessValid")
        } catch (e: Exception) {
            Log.e("FieldError:", "IAPReceiptVerificationModel field: " + "accessValid")
        }

        try {
            message = data.getString("message")
        } catch (e: Exception) {
            Log.e("FieldError:", "IAPReceiptVerificationModel field: " + "message")
        }

        try {
            status = data.getString("status")
        } catch (e: Exception) {
            Log.e("FieldError:", "IAPReceiptVerificationModel field: " + "status")
        }

        try {
            user_id = data.getString("user_id")
        } catch (e: Exception) {
            Log.e("FieldError:", "IAPReceiptVerificationModel field: " + "user_id")
        }
    }

}