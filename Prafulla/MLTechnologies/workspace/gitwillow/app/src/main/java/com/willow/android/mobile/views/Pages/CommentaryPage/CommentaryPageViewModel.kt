package com.willow.android.mobile.views.pages.commentaryPage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.willow.android.mobile.models.pages.CommentaryPageModel
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.WiVolleySingleton
import org.json.JSONObject


class CommentaryPageViewModel : ViewModel() {
    var commentaryData: LiveData<CommentaryPageModel> = MutableLiveData<CommentaryPageModel>()
    var scoreRefreshData: LiveData<CommentaryPageModel> = MutableLiveData<CommentaryPageModel>()

    fun getCommentaryData(context: Context, matchId: String, seriesId: String) {
        val _commentaryData = MutableLiveData<CommentaryPageModel>().apply {
            val url = WiAPIService.commentaryUrl + seriesId + "/" + matchId + ".json"

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val responseStringJson: String = response.substring(response.indexOf("(") + 1, response.lastIndexOf(")"))
                    val responseJson = JSONObject(responseStringJson)
                    val commentaryPageModel = CommentaryPageModel()
                    commentaryPageModel.setData(responseJson)
                    value = commentaryPageModel
                },
                {
                    Log.e("DataFetchError:", url)
                })
            WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        }

        commentaryData = _commentaryData
    }


    fun getScoreRefreshData(context: Context, matchId: String, seriesId: String) {
        val _lastBallsData = MutableLiveData<CommentaryPageModel>().apply {
            val url = WiAPIService.lastBallScoreUrl + seriesId + "/" + matchId + ".json"

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val responseStringJson: String = response.substring(response.indexOf("(") + 1, response.lastIndexOf(")"))
                    val responseJson = JSONObject(responseStringJson)
                    val commentaryPageModel = CommentaryPageModel()
                    commentaryPageModel.setData(responseJson)
                    value = commentaryPageModel
                },
                {
                    Log.e("DataFetchError:", url)
                })
            WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        }

        scoreRefreshData = _lastBallsData
    }

}