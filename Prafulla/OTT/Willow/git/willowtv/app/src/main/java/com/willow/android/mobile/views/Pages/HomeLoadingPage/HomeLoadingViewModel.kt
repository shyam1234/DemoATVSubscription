package com.willow.android.mobile.views.pages.homeLoadingPage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.willow.android.mobile.configs.AdConfig
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.configs.WiConfig
import com.willow.android.mobile.models.auth.CheckSubscriptionUserModel
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.WiVolleySingleton
import org.json.JSONObject

class HomeLoadingViewModel : ViewModel() {
    var gotConfigData: LiveData<Boolean> = MutableLiveData()
    var countryCode: LiveData<String> = MutableLiveData<String>()
    var checkSubscriptionUserData: LiveData<CheckSubscriptionUserModel> = MutableLiveData<CheckSubscriptionUserModel>()

    fun getConfigData(context: Context) {
        val _configData = MutableLiveData<Boolean>().apply {
            val url = WiAPIService.configUrl

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val configJson = JSONObject(response)
                    WiConfig.setCloudData(configJson)
                    MessageConfig.setCloudData(configJson)
                    value = true
                },
                {
                    Log.e("DataFetchError:", url)
                })
            WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        }

        gotConfigData = _configData
    }

    fun getDfpConfigData(context: Context) {
        val url = WiAPIService.dfpConfigUrl

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val responseJson = JSONObject(response)
                    AdConfig.setCloudData(responseJson)
                } catch (e: Exception) {
                    Log.e("DataDecodeError:", url)
                }
            },
            {
                Log.e("DataFetchError:", url)
            })
        WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
    }

    fun getCountryCode(context: Context) {
        val _countryCode = MutableLiveData<String>().apply {
            val url = WiAPIService.countryCodeUrl

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    value = response
                },
                {
                    Log.e("DataFetchError:", url)
                    value = "na"
                })
            WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        }
        countryCode = _countryCode
    }

    fun getCheckSubscriptionUserData(context: Context) {
        val _checkSubscriptionUserData = MutableLiveData<CheckSubscriptionUserModel>().apply {
            val url = WiAPIService.authUrl
            val params = WiAPIService.getCheckSubscriptionParams()

            val stringRequest = object: StringRequest(
                Method.POST, url,
                { response ->
                    val responseJson = JSONObject(response)
                    val checkSubscriptionUserModel = CheckSubscriptionUserModel()
                    checkSubscriptionUserModel.setData(responseJson)
                    value = checkSubscriptionUserModel
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

        checkSubscriptionUserData = _checkSubscriptionUserData
    }
}