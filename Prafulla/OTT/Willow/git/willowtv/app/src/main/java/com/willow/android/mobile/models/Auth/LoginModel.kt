package com.willow.android.mobile.models.auth

import android.util.Log
import org.json.JSONObject


class CheckExistingEmailResponseModel {
    var status: Boolean = false

    fun setData(data: String) {
        try {
            val dataJson = JSONObject(data)
            status = dataJson.getBoolean("status")
        } catch (e: Exception) {
            Log.e("FieldError:", "LoginResponseModel field: " + "result")
        }
    }
}

class TVELoginResponseModel {
    var status: String = ""
    var subscriptionStatus: Int = 0
    var userId: Int = 0
    var email: String = ""
    var Provider: String = ""
    var ads_category: String = "free"

    fun setData(data: JSONObject) {
        try {
            status = data.getString("status")
        } catch (e: Exception) {
            Log.e("FieldError:", "TVELoginResponseModel field: " + "status")
        }

        try {
            subscriptionStatus = data.getInt("subscriptionStatus")
        } catch (e: Exception) {
            Log.e("FieldError:", "TVELoginResponseModel field: " + "subscriptionStatus")
        }

        try {
            userId = data.getInt("userId")
        } catch (e: Exception) {
            Log.e("FieldError:", "TVELoginResponseModel field: " + "userId")
        }

        try {
            email = data.getString("email")
        } catch (e: Exception) {
            Log.e("FieldError:", "TVELoginResponseModel field: " + "email")
        }

        try {
            Provider = data.getString("Provider")
        } catch (e: Exception) {
            Log.e("FieldError:", "TVELoginResponseModel field: " + "Provider")
        }

        try {
            ads_category = data.getString("ads_category")
        } catch (e: Exception) {
            Log.e("FieldError:", "TVELoginResponseModel field: " + "ads_category")
        }
    }
}

class LoginResponseModel {
    var result: LoginResponseResultModel = LoginResponseResultModel()

    fun setData(data: String) {
        try {
            val dataJson = JSONObject(data)
            val resultJson = dataJson.getJSONObject("result")
            result.setData(resultJson)
        } catch (e: Exception) {
            Log.e("FieldError:", "LoginResponseModel field: " + "result")
        }
    }
}

class SignupResponseModel {
    var result: LoginResponseResultModel = LoginResponseResultModel()

    fun setData(data: String) {
        try {
            val dataJson = JSONObject(data)
            val resultJson = dataJson.getJSONObject("result")
            result.setData(resultJson)
        } catch (e: Exception) {
            Log.e("FieldError:", "SignupResponseModel field: " + "result")
        }
    }
}

class LoginResponseResultModel {
    var status: String = ""
    var message: String = ""
    var subscriptionStatus: Int = 0
    var userId: Int = 0
    var tveUserId: String = ""
    var wuid: String = ""
    var email: String = ""
    var ads_category: String = "free"
    var emailVerified: Int = 0
    var nextRenewalDate: String = ""

    fun setData(data: JSONObject) {
        try {
            status = data.getString("status")
        } catch (e: Exception) {
            Log.e("FieldError:", "LoginResponseResultModel field: " + "status")
        }

        try {
            message = data.getString("message")
        } catch (e: Exception) {
            Log.e("FieldError:", "LoginResponseResultModel field: " + "message")
        }

        try {
            subscriptionStatus = data.getInt("subscriptionStatus")
        } catch (e: Exception) {
            Log.e("FieldError:", "LoginResponseResultModel field: " + "subscriptionStatus")
        }

        try {
            userId = data.getInt("userId")
        } catch (e: Exception) {
            Log.e("FieldError:", "LoginResponseResultModel field: " + "userId")
        }

        try {
            tveUserId = data.getString("tveUserId")
        } catch (e: Exception) {
            Log.e("FieldError:", "LoginResponseResultModel field: " + "tveUserId")
        }

        try {
            wuid = data.getString("wuid")
        } catch (e: Exception) {
            Log.e("FieldError:", "LoginResponseResultModel field: " + "wuid")
        }

        try {
            email = data.getString("email")
        } catch (e: Exception) {
            Log.e("FieldError:", "LoginResponseResultModel field: " + "email")
        }

        try {
            ads_category = data.getString("ads_category")
        } catch (e: Exception) {
            Log.e("FieldError:", "LoginResponseResultModel field: " + "ads_category")
        }

        try {
            emailVerified = data.getInt("emailVerified")
        } catch (e: Exception) {
            Log.e("FieldError:", "LoginResponseResultModel field: " + "emailVerified")
        }

        try {
            nextRenewalDate = data.getString("nextRenewalDate")
        } catch (e: Exception) {
            Log.e("FieldError:", "LoginResponseResultModel field: " + "nextRenewalDate")
        }
    }
}


class CheckSubscriptionUserModel {
    var status: String = ""
    var subscriptionStatus: Int = 0
    var ads_category: String = "free"
    var emailVerified: Int = 0
    var nextRenewalDate: String = ""

    fun setData(data: JSONObject) {
        try {
            status = data.getString("status")
        } catch (e: Exception) {
            Log.e("FieldError:", "CheckSubscriptionUserModel field: " + "status")
        }

        try {
            subscriptionStatus = data.getInt("subscriptionStatus")
        } catch (e: Exception) {
            Log.e("FieldError:", "CheckSubscriptionUserModel field: " + "subscriptionStatus")
        }

        try {
            ads_category = data.getString("ads_category")
        } catch (e: Exception) {
            Log.e("FieldError:", "CheckSubscriptionUserModel field: " + "ads_category")
        }

        try {
            emailVerified = data.getInt("emailVerified")
        } catch (e: Exception) {
            Log.e("FieldError:", "LoginResponseResultModel field: " + "emailVerified")
        }

        try {
            nextRenewalDate = data.getString("nextRenewalDate")
        } catch (e: Exception) {
            Log.e("FieldError:", "LoginResponseResultModel field: " + "nextRenewalDate")
        }
    }
}

class ForgotPasswordModel {
    var result: ForgotPasswordResultModel = ForgotPasswordResultModel()

    fun setData(data: String) {
        try {
            val dataJson = JSONObject(data)
            val resultJson = dataJson.getJSONObject("result")
            result.setData(resultJson)
        } catch (e: Exception) {
            Log.e("FieldError:", "ForgotPasswordModel field: " + "result")
        }
    }
}

class ForgotPasswordResultModel {
    var status: String = ""
    var message: String = ""

    fun setData(data: JSONObject) {
        try {
            status = data.getString("status")
        } catch (e: Exception) {
            Log.e("FieldError:", "ForgotPasswordResultModel field: " + "status")
        }

        try {
            message = data.getString("message")
        } catch (e: Exception) {
            Log.e("FieldError:", "ForgotPasswordResultModel field: " + "message")
        }
    }
}

class VerifyEmailResponseModel {
    var result: VerifyEmailResponseResultModel = VerifyEmailResponseResultModel()

    fun setData(data: String) {
        try {
            val dataJson = JSONObject(data)
            val resultJson = dataJson.getJSONObject("result")
            result.setData(resultJson)
        } catch (e: Exception) {
            Log.e("FieldError:", "VerifyEmailResponseModel field: " + "result")
        }
    }
}

class VerifyEmailResponseResultModel {
    var status: String = ""
    var message: String = ""

    fun setData(data: JSONObject) {
        try {
            status = data.getString("status")
        } catch (e: Exception) {
            Log.e("FieldError:", "VerifyEmailResponseResultModel field: " + "status")
        }

        try {
            message = data.getString("message")
        } catch (e: Exception) {
            Log.e("FieldError:", "VerifyEmailResponseResultModel field: " + "message")
        }
    }
}


class DeleteAccountResponseModel {
    var result: DeleteAccountResponseResultModel = DeleteAccountResponseResultModel()

    fun setData(data: String) {
        try {
            val dataJson = JSONObject(data)
            val resultJson = dataJson.getJSONObject("result")
            result.setData(resultJson)
        } catch (e: Exception) {
            Log.e("FieldError:", "DeleteAccountResponseModel field: " + "result")
        }
    }
}


class DeleteAccountResponseResultModel {
    var status: String = ""
    var message: String = ""

    fun setData(data: JSONObject) {
        try {
            status = data.getString("status")
        } catch (e: Exception) {
            Log.e("FieldError:", "DeleteAccountResponseResultModel field: " + "status")
        }

        try {
            message = data.getString("message")
        } catch (e: Exception) {
            Log.e("FieldError:", "DeleteAccountResponseResultModel field: " + "message")
        }
    }
}