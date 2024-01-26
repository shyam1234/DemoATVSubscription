package com.willow.android.mobile.views.popup.loginPopup

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.StringRequest
import com.willow.android.mobile.models.auth.CheckExistingEmailResponseModel
import com.willow.android.mobile.models.auth.LoginResponseModel
import com.willow.android.mobile.models.auth.SignupResponseModel
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.WiVolleySingleton


class LoginPopupViewModel : ViewModel() {
    var checkExistingEmailResponseData: LiveData<CheckExistingEmailResponseModel> = MutableLiveData<CheckExistingEmailResponseModel>()
    var loginResponseData: LiveData<LoginResponseModel> = MutableLiveData<LoginResponseModel>()
    var signupResponseData: LiveData<SignupResponseModel> = MutableLiveData<SignupResponseModel>()

    fun checkExistingEmailRequest(context: Context, email: String) {
        val _checkExistingEmailResponseData = MutableLiveData<CheckExistingEmailResponseModel>().apply {
            val url = WiAPIService.authUrl
            val params = WiAPIService.getExistingEmailParams(email)

            val stringRequest = object: StringRequest(
                Method.POST, url,
                { response ->
                    val checkExistingEmailResponseModel = CheckExistingEmailResponseModel()
                    checkExistingEmailResponseModel.setData(response)
                    value = checkExistingEmailResponseModel
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
        checkExistingEmailResponseData = _checkExistingEmailResponseData
    }

    fun makeLoginRequest(context: Context, email: String, password: String) {
        val _loginResponseData = MutableLiveData<LoginResponseModel>().apply {
            val url = WiAPIService.authUrl
            val params = WiAPIService.getLoginParams(email, password)

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
        loginResponseData = _loginResponseData
    }

    fun makeSignupRequest(context: Context, email: String, password: String, name: String) {
        val _signupResponseData = MutableLiveData<SignupResponseModel>().apply {
            val url = WiAPIService.authUrl
            val params = WiAPIService.getSignupParams(email, password, name)

            val stringRequest = object: StringRequest(
                Method.POST, url,
                { response ->
                    val signupResponseModel = SignupResponseModel()
                    signupResponseModel.setData(response)
                    value = signupResponseModel
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
        signupResponseData = _signupResponseData
    }

}