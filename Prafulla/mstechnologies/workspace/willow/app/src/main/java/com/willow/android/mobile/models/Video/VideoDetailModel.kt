package com.willow.android.mobile.models.video

import android.util.Log
import org.json.JSONObject


class VideoDetailModel {
    var selectedVideo: VideoModel = VideoModel()
    var relatedVideos: MutableList<VideoModel> = mutableListOf()

    fun setData(data: JSONObject) {
        try {
            val result = data.getJSONObject("result")

            try {
                val selectedVideoModel = result.getJSONObject("selected_video")
                selectedVideo.setDeeplinkData(selectedVideoModel)
            } catch (e: Exception) { }


            try {
                val playbackBaseUrl = result.getString("playback_base_url")
                val relatedVideosValue = result.getJSONArray("related_videos")
                for (i in 0 until relatedVideosValue.length()) {
                    val relatedVideoJson = relatedVideosValue[i] as? JSONObject
                    if (relatedVideoJson != null) {
                        val videosModel = VideoModel()
                        videosModel.videoBaseUrl = playbackBaseUrl
                        videosModel.setDeeplinkData(relatedVideoJson)
                        relatedVideos.add(videosModel)
                    }
                }
            } catch (e: Exception) { }

        } catch (e: Exception) {
            Log.e("FieldError:", "HomePageResultModel field: " + "thumb_base_url")
        }
    }
}