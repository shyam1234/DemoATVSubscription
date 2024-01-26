package tv.willow.Models

import android.util.Log
import com.willow.android.mobile.models.video.VideosSectionModel
import org.json.JSONObject


class VideosPageModel {
    var result: VideosPageResultModel = VideosPageResultModel()
    fun setData(data: String) {
        try {
            val dataJson = JSONObject(data)
            val resultJson = dataJson.getJSONObject("result")
            result.setData(resultJson)
        } catch (e: Exception) {
            Log.e("FieldError:", "VideosPageModel field: " + "result")
        }
    }
}

class VideosPageResultModel {
    var HLSSuffix: String = ""
    var clipUrlDict: String = ""
    var VideoFileExt: String = ""
    var videoBaseUrl: String = ""
    var slugBaseUrl: String = ""
    var slugDict: JSONObject = JSONObject()
    var matchDetailsDictModel: JSONObject = JSONObject()
    var status: String = ""
    var imageBaseUrl: String = ""
    var video_data: MutableList<VideosSectionModel> = mutableListOf()
    var clipExt = ""

    fun setData(data: JSONObject) {
        try {
            HLSSuffix = data.getString("HLSSuffix")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideosPageResultModel field: " + "HLSSuffix")
        }

        try {
            VideoFileExt = data.getString("VideoFileExt")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideosPageResultModel field: " + "VideoFileExt")
        }

        clipExt = VideoFileExt + "/" + HLSSuffix

        try {
            videoBaseUrl = data.getString("playback_base_url")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideosPageResultModel field: " + "playback_base_url")
        }

        try {
            val clipUrlDictJson = data.getJSONObject("VideoBaseUrl")
            clipUrlDict = clipUrlDictJson.toString()
        } catch (e: Exception) {
            Log.e("FieldError:", "VideosPageResultModel field: " + "VideoBaseUrl")
        }

        try {
            slugBaseUrl = data.getString("VideoSlugBaseUrl")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideosPageResultModel field: " + "VideoSlugBaseUrl")
        }

        try {
            slugDict = data.getJSONObject("VideoMatchSlugUrls")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideosPageResultModel field: " + "VideoMatchSlugUrls")
        }

        try {
            matchDetailsDictModel = data.getJSONObject("MatchDetails")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideosPageResultModel field: " + "MatchDetails")
        }

        try {
            status = data.getString("status")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideosPageResultModel field: " + "status")
        }

        try {
            imageBaseUrl = data.getString("thumb_base_url")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideosPageResultModel field: " + "thumb_base_url")
        }


        try {
            val videoSections = data.getJSONArray("video_data")
            for (i in 0 until videoSections.length()) {
                val videoJson = videoSections[i] as? JSONObject
                if (videoJson != null) {
                    val videoSectionModel = VideosSectionModel()
                    videoSectionModel.setBaseData(imageBaseUrl, videoBaseUrl, slugBaseUrl, slugDict, clipUrlDict, clipExt, matchDetailsDictModel = matchDetailsDictModel)
                    videoSectionModel.setJsonData(videoJson)
                    video_data.add(videoSectionModel)
                }
            }
        } catch (e: Exception) {
            Log.e("FieldError:", "VideosPageResultModel field: " + "video_data")
        }
    }
}