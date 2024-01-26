package com.willow.android.mobile.views.pages.loginPage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.StringRequest
import com.willow.android.mobile.models.auth.LoginResponseModel
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.WiVolleySingleton

class LoginPageFragmentViewModel: ViewModel() {
    var googleLoginResponseData: LiveData<LoginResponseModel> = MutableLiveData<LoginResponseModel>()
    var appleLoginResponseData: LiveData<LoginResponseModel> = MutableLiveData<LoginResponseModel>()

    fun makeGoogleLoginRequest(context: Context, email: String) {
        val _googleLoginResponseData = MutableLiveData<LoginResponseModel>().apply {
            val url = WiAPIService.socialAuthUrl
            val params = WiAPIService.getGoogleLoginParams(email)

            val stringRequest = object: StringRequest(
                Method.POST, url,
                { response ->
                    Log.e("Google Login Response: ", response)
                    val loginResponseModel = LoginResponseModel()
                    loginResponseModel.setData(response)
                    value = loginResponseModel
                },
                {
                    Log.e("DataFetchError:", url)
                }){
                override fun getParams(): Map<String, String> {
                    return params
                }
            }
            WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        }
        googleLoginResponseData = _googleLoginResponseData
    }

    fun makeAppleLoginRequest(context: Context, status: String, email: String, state: String, code: String, id_token: String) {
        val _appleLoginResponseData = MutableLiveData<LoginResponseModel>().apply {
            val url = WiAPIService.appleAuthUrl
            val params = WiAPIService.getAppleLoginParams(status, email, state, code, id_token)

            val stringRequest = object: StringRequest(
                Method.POST, url,
                { response ->
                    val loginResponseModel = LoginResponseModel()
                    loginResponseModel.setData(response)
                    value = loginResponseModel
                },
                {
                    Log.e("DataFetchError:", url)
                }){
                override fun getParams(): Map<String, String> {
                    return params
                }
            }
            WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        }

        appleLoginResponseData = _appleLoginResponseData
    }
}