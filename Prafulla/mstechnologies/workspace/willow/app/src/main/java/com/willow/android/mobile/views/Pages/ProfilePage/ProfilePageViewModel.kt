package com.willow.android.mobile.views.pages.profilePage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.StringRequest
import com.willow.android.mobile.models.auth.DeleteAccountResponseModel
import com.willow.android.mobile.models.auth.ForgotPasswordModel
import com.willow.android.mobile.models.auth.VerifyEmailResponseModel
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.WiVolleySingleton

class ProfilePageViewModel : ViewModel() {
    var forgotPasswordResponseData: LiveData<ForgotPasswordModel> = MutableLiveData<ForgotPasswordModel>()
    var verifyEmailResponseData: LiveData<VerifyEmailResponseModel> = MutableLiveData<VerifyEmailResponseModel>()
    var deleteAccountResponseData: LiveData<DeleteAccountResponseModel> = MutableLiveData<DeleteAccountResponseModel>()

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

    fun makeVerifyEmailRequest(context: Context, email: String) {
        val _verifyEmailResponseData = MutableLiveData<VerifyEmailResponseModel>().apply {
            val url = WiAPIService.verifyEmailUrl
            val params = WiAPIService.getVerifyEmailParams(email)

            val stringRequest = object: StringRequest(
                Method.POST, url,
                { response ->
                    Log.e("VerifyResp", response)
                    val verifyEmailResponseModel = VerifyEmailResponseModel()
                    verifyEmailResponseModel.setData(response)
                    value = verifyEmailResponseModel
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
        verifyEmailResponseData = _verifyEmailResponseData
    }

    fun makeDeleteAccountRequest(context: Context, email: String, userId: String) {
        val _deleteAccountResponseData = MutableLiveData<DeleteAccountResponseModel>().apply {
            val url = WiAPIService.deleteAccountUrl
            val params = WiAPIService.getDeleteAccountParams(email, userId)

            val stringRequest = object: StringRequest(
                Method.POST, url,
                { response ->
                    Log.e("DeleteResp", response)
                    val deleteAccountResponseModel = DeleteAccountResponseModel()
                    deleteAccountResponseModel.setData(response)
                    value = deleteAccountResponseModel
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
        deleteAccountResponseData = _deleteAccountResponseData
    }
}