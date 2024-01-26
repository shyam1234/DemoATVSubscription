package com.willow.android.mobile.views.pages.fixturesPage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.models.pages.FixturesPageModel
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.WiVolleySingleton
import org.json.JSONObject


class FixturesPageViewModel : ViewModel() {
    var fixturesData: LiveData<FixturesPageModel> = MutableLiveData<FixturesPageModel>()

    fun makeFixturesDataRequest(context: Context) {
        val _fixturesData = MutableLiveData<FixturesPageModel>().apply {
            val url: String
            if (UserModel.cc.equals("ca", true)) {
                url = WiAPIService.fixturesUrlCA
            } else {
                url = WiAPIService.fixturesUrlUSA
            }

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val jsonResponse = JSONObject(response)
                    val fixturesPageModel = FixturesPageModel()
                    fixturesPageModel.setData(jsonResponse)
                    value = fixturesPageModel
                },
                {
                    Log.e("DataFetchError:", url)
                })
            WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        }

        fixturesData = _fixturesData
    }

}