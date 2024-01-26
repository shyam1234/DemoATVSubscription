package com.willow.android.mobile.views.pages.forgotPasswordPage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.StringRequest
import com.willow.android.mobile.models.auth.ForgotPasswordModel
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.WiVolleySingleton

class ForgotPasswordPageViewModel : ViewModel() {
    var forgotPasswordResponseData: LiveData<ForgotPasswordModel> = MutableLiveData<ForgotPasswordModel>()

    fun makeForgotPasswordRequest(context: Context, email: String) {
        val _forgotPasswordResponseData = MutableLiveData<ForgotPasswordModel>().apply {
            val url = WiAPIService.authUrl
            val params = WiAPIService.getForgotPasswordParams(email)

            val stringRequest = object: StringRequest(
                Method.POST, url,
                { response ->
                    val forgotPasswordModel = ForgotPasswordModel()
                    forgotPasswordModel.setData(response)
                    value = forgotPasswordModel
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
        forgotPasswordResponseData = _forgotPasswordResponseData
    }
}