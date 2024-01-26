package com.willow.android.mobile.models.video

import android.util.Log
import org.json.JSONObject
import java.io.Serializable

class VideoStreamsModel : Serializable {
    var Videos: MutableList<VideoStream> = mutableListOf()

    fun setData(data: String) {
        try {
            val dataJson = JSONObject(data)
            val videosArray = dataJson.getJSONArray("Videos")
            for (i in 0 until videosArray.length()) {
                val videoJson = videosArray[i] as? JSONObject
                if (videoJson != null) {
                    val videoStreamModel = VideoStream()
                    videoStreamModel.setData(videoJson)
                    Videos.add(videoStreamModel)
                }
            }
        } catch (e: Exception) {
            Log.e("FieldError:", "HomePageModel field: " + "result")
        }
    }
}

class VideoStream : Serializable {
    var EventName: String = ""
    var Image: String = ""
    var SeriesName: String = ""
    var Title: String = ""
    var Url: String = ""
    var baseURL: String = ""
    var priority: Int = 1

    fun setData(data: JSONObject) {
        try {
            EventName = data.getString("EventName")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoStream field: " + "EventName")
        }

        try {
            Image = data.getString("Image")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoStream field: " + "Image")
        }

        try {
            SeriesName = data.getString("SeriesName")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoStream field: " + "HLSSuffix")
        }

        try {
            Title = data.getString("Title")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoStream field: " + "Title")
        }

        try {
            Url = data.getString("Url")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoStream field: " + "Url")
        }

        try {
            baseURL = data.getString("baseURL")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoStream field: " + "baseURL")
        }

        try {
            priority = data.getInt("priority")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoStream field: " + "priority")
        }
    }
}