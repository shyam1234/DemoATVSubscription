package com.willow.android.mobile.views.pages.homePage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.models.pages.CommentaryPageModel
import com.willow.android.mobile.models.pages.HomePageModel
import com.willow.android.mobile.models.video.VideoDetailModel
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.WiVolleySingleton
import org.json.JSONObject

class HomePageViewModel() : ViewModel() {
    var homeData: LiveData<HomePageModel> = MutableLiveData<HomePageModel>()
    var commentaryData: LiveData<CommentaryPageModel> = MutableLiveData<CommentaryPageModel>()

    /**
     * For Deeplink
     */
    var videoDetailPageData: LiveData<VideoDetailModel> = MutableLiveData<VideoDetailModel>()

    fun getHomeData(context: Context) {
         val _homeData = MutableLiveData<HomePageModel>().apply {
             val url: String
             if (UserModel.cc.equals("ca", true)) {
                 url = WiAPIService.homeUrlCA
             } else {
                 url = WiAPIService.homeUrlUSA
             }

             val stringRequest = StringRequest(
                 Request.Method.GET, url,
                 { response ->
                     val homePageModel = HomePageModel()
                     homePageModel.setData(response)
                     value = homePageModel
                 },
                 {
                     Log.e("DataFetchError:", url)
                 })
             WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        }

        homeData = _homeData
    }



    fun getVideoDetailPage(context: Context, slug: String, duration: String, matchId: String, contentType: String) {
        val _videoDetailPageData = MutableLiveData<VideoDetailModel>().apply {
            val url = WiAPIService.videoDetailPageUrl
            val params = WiAPIService.getVideoDetailParams(slug, duration, matchId, contentType)

            val stringRequest = object: StringRequest(
                Method.POST, url,
                { response ->
                    val responseJson = JSONObject(response)
                    val videoDetailModel = VideoDetailModel()
                    videoDetailModel.setData(responseJson)
                    value = videoDetailModel
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

        videoDetailPageData = _videoDetailPageData
    }

    fun getCommentaryData(context: Context, matchId: String, seriesId: String) {
        val _commentaryData = MutableLiveData<CommentaryPageModel>().apply {
            val url = WiAPIService.commentaryUrl + seriesId + "/" + matchId + ".json"

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val responseJson = JSONObject(response)
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


}