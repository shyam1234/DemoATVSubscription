package com.willow.android.mobile.views.pages.videoDetailPage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.StringRequest
import com.willow.android.mobile.models.video.VideoModel
import com.willow.android.mobile.models.video.VideoStreamsModel
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.WiVolleySingleton


class VideoDetailPageViewModel : ViewModel() {
    var streamData: LiveData<VideoStreamsModel> = MutableLiveData<VideoStreamsModel>()
    var tveStreamData: LiveData<VideoStreamsModel> = MutableLiveData<VideoStreamsModel>()

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
}