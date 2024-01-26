package com.willow.android.mobile.views.pages.resultsPage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.models.pages.ResultsPageModel
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.WiVolleySingleton
import org.json.JSONObject


class ResultsPageViewModel : ViewModel() {
    var resultsData: LiveData<ResultsPageModel> = MutableLiveData<ResultsPageModel>()

    fun makeResultsDataRequest(context: Context) {
        val _resultsData = MutableLiveData<ResultsPageModel>().apply {
            val url: String
            if (UserModel.cc.equals("ca", true)) {
                url = WiAPIService.resultsUrlCA
            } else {
                url = WiAPIService.resultsUrlUSA
            }

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val jsonResponse = JSONObject(response)
                    val resultsPageModel = ResultsPageModel()
                    resultsPageModel.setData(jsonResponse)
                    value = resultsPageModel
                },
                {
                    Log.e("DataFetchError:", url)
                })
            WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        }

        resultsData = _resultsData
    }

}