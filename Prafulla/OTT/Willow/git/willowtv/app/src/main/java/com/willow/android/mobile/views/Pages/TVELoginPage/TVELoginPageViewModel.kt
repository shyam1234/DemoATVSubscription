package com.willow.android.mobile.views.pages.tVELoginPage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.willow.android.mobile.models.TVEConfigModel
import com.willow.android.mobile.models.auth.TVELoginResponseModel
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.WiVolleySingleton
import org.json.JSONObject

class TVELoginPageViewModel : ViewModel() {
    var tveConfigData: LiveData<TVEConfigModel> = MutableLiveData<TVEConfigModel>()
    var tveUserData: LiveData<TVELoginResponseModel> = MutableLiveData<TVELoginResponseModel>()

    fun getTVEConfigData(context: Context) {
        val _tveConfigData = MutableLiveData<TVEConfigModel>().apply {
            val url = WiAPIService.tveConfigUrl

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val responseJson = JSONObject(response)
                    val tveConfigModel = TVEConfigModel()
                    tveConfigModel.setData(responseJson)
                    value = tveConfigModel
                },
                {
                    Log.e("DataFetchError:", url)
                })
            WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        }
        tveConfigData = _tveConfigData
    }


    fun verifyTVEUserData(context: Context, token: String) {
        val _tveUserData = MutableLiveData<TVELoginResponseModel>().apply {
            val url = WiAPIService.tveAuthUrl
            val params = WiAPIService.getTVELoginParams(token)

            val stringRequest = object: StringRequest(
                Method.POST, url,
                { response ->
                    val responseJson = JSONObject(response)
                    val tveLoginResponseModel = TVELoginResponseModel()
                    tveLoginResponseModel.setData(responseJson)
                    value = tveLoginResponseModel
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
        tveUserData = _tveUserData
    }
}