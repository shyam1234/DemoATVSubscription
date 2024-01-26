package com.willow.android.mobile.views.pages.settingsPage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.WiVolleySingleton
import tv.willow.Models.SettingsPageModel

class SettingsPageViewModel : ViewModel() {
    var settingsData: LiveData<SettingsPageModel> = MutableLiveData<SettingsPageModel>()

    fun getSettingsPageData(context: Context) {
        val _settingsData = MutableLiveData<SettingsPageModel>().apply {
            val url = WiAPIService.settingsUrl

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val settingsPageModel = SettingsPageModel()
                    settingsPageModel.setData(response)
                    value = settingsPageModel
                },
                {
                    Log.e("DataFetchError:", url)
                })
            WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        }

        settingsData = _settingsData
    }
}