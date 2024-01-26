package com.willow.android.mobile.views.pages.videosPage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.WiVolleySingleton
import tv.willow.Models.VideosPageModel

class VideosPageViewModel : ViewModel() {
    var videosPageData: LiveData<VideosPageModel> = MutableLiveData<VideosPageModel>()

    fun getVideosPageData(context: Context) {
        val _videosPageData = MutableLiveData<VideosPageModel>().apply {
            val url: String
            if (UserModel.cc.equals("ca", true)) {
                url = WiAPIService.videosUrlCA
            } else {
                url = WiAPIService.videosUrlUSA
            }

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val videosPageModel = VideosPageModel()
                    videosPageModel.setData(response)
                    value = videosPageModel
                },
                {
                    Log.e("DataFetchError:", url)
                })
            WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        }

        videosPageData = _videosPageData
    }
}