package com.willow.android.mobile.views.pages.matchCenterPage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.models.pages.CommentaryPageModel
import com.willow.android.mobile.models.pages.MatchCenterPageModel
import com.willow.android.mobile.models.pages.PollerDataModel
import com.willow.android.mobile.models.video.VideoModel
import com.willow.android.mobile.models.video.VideoStreamsModel
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.WiVolleySingleton
import org.json.JSONObject


class MatchCenterPageViewModel : ViewModel() {
    var matchCenterData: LiveData<MatchCenterPageModel> = MutableLiveData<MatchCenterPageModel>()
    var streamData: LiveData<VideoStreamsModel> = MutableLiveData<VideoStreamsModel>()
    var tveStreamData: LiveData<VideoStreamsModel> = MutableLiveData<VideoStreamsModel>()
    var pollerData: LiveData<PollerDataModel> = MutableLiveData<PollerDataModel>()
    var scoreRefreshData: LiveData<CommentaryPageModel> = MutableLiveData<CommentaryPageModel>()

    fun getMatchCenterData(context: Context, matchId: String, seriesId: String) {
        val _matchCenterData = MutableLiveData<MatchCenterPageModel>().apply {
            val url = WiAPIService.matchCenterUrl + seriesId + "/" + matchId + ".json"

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val matchCenterPageModel = MatchCenterPageModel()
                    matchCenterPageModel.setData(matchId, response)
                    value = matchCenterPageModel
                },
                {
                    Log.e("DataFetchError:", url)
                })
            WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        }

        matchCenterData = _matchCenterData
    }

    fun getStreamData(context: Context, videoModel: VideoModel) {
        val _streamData = MutableLiveData<VideoStreamsModel>().apply {
            val url = videoModel.videoBaseUrl
            val params = WiAPIService.getPlaybackStreamParams(videoModel)

            val stringRequest = object: StringRequest(
                Method.POST, url,
                { response ->
                    val videoStreamsModel = VideoStreamsModel()
                    videoStreamsModel.setData(response)
                    value = videoStreamsModel
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
        streamData = _streamData
    }


    fun getTVEStreamData(context: Context, token: String, videoModel: VideoModel) {
        val _tveStreamData = MutableLiveData<VideoStreamsModel>().apply {
            val url = videoModel.videoBaseUrl
            val params = WiAPIService.getTVEPlaybackStreamParams(token, videoModel)

            val stringRequest = object: StringRequest(
                Method.POST, url,
                { response ->
                    val videoStreamsModel = VideoStreamsModel()
                    videoStreamsModel.setData(response)
                    value = videoStreamsModel
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
        tveStreamData = _tveStreamData
    }

    fun sendPollerRequest(context: Context, videoModel: VideoModel, guid: String) {
        val _pollerData = MutableLiveData<PollerDataModel>().apply {
            var url: String
            if (guid.isEmpty()) {
                url = WiAPIService.pollerUrl + "?UserId=" + UserModel.userId + "&matchId=" + videoModel.matchId
            } else {
                url = WiAPIService.pollerUrl  + "?UserId=" + UserModel.userId + "&matchId=" + videoModel.matchId + "&guid=" + guid
            }

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val responseJSON = JSONObject(response)
                    val pollerDataModel = PollerDataModel()
                    pollerDataModel.setData(responseJSON)
                    value = pollerDataModel
                },
                {
                    Log.e("DataFetchError:", url)
                })
            WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        }
        pollerData = _pollerData
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