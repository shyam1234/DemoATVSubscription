package com.willow.android.mobile.views.pages.scorecardPage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.willow.android.mobile.models.pages.ScorecardPageModel
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.WiVolleySingleton

class ScorecardPageViewModel : ViewModel() {
    var scorecardData: LiveData<ScorecardPageModel> = MutableLiveData<ScorecardPageModel>()

    fun makeScorecardPageDataRequest(context: Context, seriesId: String, matchId: String) {
        val _scorecardData = MutableLiveData<ScorecardPageModel>().apply {
            val url = WiAPIService.scorecardUrl + seriesId + "/" + matchId + ".json"

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val responseStringJson: String = response.substring(response.indexOf("(") + 1, response.lastIndexOf(")"))
                    val scorecardPageModel = ScorecardPageModel()
                    scorecardPageModel.setData(seriesId, responseStringJson)
                    value = scorecardPageModel
                },
                {
                    Log.e("DataFetchError:", url)
                })
            WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        }

        scorecardData = _scorecardData
    }
}