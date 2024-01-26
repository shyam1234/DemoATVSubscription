package com.willow.android.mobile.models.pages

import android.util.Log
import com.willow.android.mobile.models.video.VideoModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

class HomePageModel {
    var result: HomePageResultModel = HomePageResultModel()
    fun setData(data: String) {
        try {
            val dataJson = JSONObject(data)
            val resultJson = dataJson.getJSONObject("result")
            result.setData(resultJson)
        } catch (e: Exception) {
            Log.e("FieldError:", "HomePageModel field: " + "result")
        }
    }
}

class HomePageResultModel {
    var HLSSuffix: String = ""
    var clipUrlDict: String = ""
    var VideoFileExt: String = ""
    var videoBaseUrl: String = ""
    var slugBaseUrl: String = ""
    var slugDict: JSONObject = JSONObject()
    var matchDetailsDictModel: JSONObject = JSONObject()
    var status: String = ""
    var imageBaseUrl: String = ""
    var video_data: MutableList<HomePageSectionModel> = mutableListOf()
    var clipExt = ""

    fun setData(data: JSONObject) {
        try {
            HLSSuffix = data.getString("HLSSuffix")
        } catch (e: Exception) {
            Log.e("FieldError:", "HomePageResultModel field: " + "HLSSuffix")
        }

        try {
            val clipUrlDictJson = data.getJSONObject("VideoBaseUrl")
            clipUrlDict = clipUrlDictJson.toString()
        } catch (e: Exception) {
            Log.e("FieldError:", "HomePageResultModel field: " + "VideoBaseUrl")
        }

        try {
            VideoFileExt = data.getString("VideoFileExt")
        } catch (e: Exception) {
            Log.e("FieldError:", "HomePageResultModel field: " + "VideoFileExt")
        }

        clipExt = VideoFileExt + "/" + HLSSuffix

        try {
            videoBaseUrl = data.getString("playback_base_url")
        } catch (e: Exception) {
            Log.e("FieldError:", "HomePageResultModel field: " + "playback_base_url")
        }

        try {
            slugBaseUrl = data.getString("VideoSlugBaseUrl")
        } catch (e: Exception) {
            Log.e("FieldError:", "HomePageResultModel field: " + "VideoSlugBaseUrl")
        }

        try {
            slugDict = data.getJSONObject("VideoMatchSlugUrls")
        } catch (e: Exception) {
            Log.e("FieldError:", "HomePageResultModel field: " + "VideoMatchSlugUrls")
        }

        try {
            matchDetailsDictModel = data.getJSONObject("MatchDetails")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideosPageResultModel field: " + "MatchDetails")
        }

        try {
            status = data.getString("status")
        } catch (e: Exception) {
            Log.e("FieldError:", "HomePageResultModel field: " + "status")
        }

        try {
            imageBaseUrl = data.getString("thumb_base_url")
        } catch (e: Exception) {
            Log.e("FieldError:", "HomePageResultModel field: " + "thumb_base_url")
        }

        try {
            val videoDataValue = data.getJSONArray("video_data")
            for (i in 0 until videoDataValue.length()) {
                val videoData = videoDataValue[i] as? JSONObject
                if (videoData != null) {
                    val homePageSectionModel = HomePageSectionModel()
                    homePageSectionModel.setData(imageBaseUrl, videoBaseUrl, slugBaseUrl, slugDict, clipUrlDict, clipExt, videoData, matchDetailsDictModel = matchDetailsDictModel)
                    video_data.add(homePageSectionModel)
                }
            }

        } catch (e: Exception) {
            Log.e("FieldError:", "HomePageResultModel field: " + "thumb_base_url")
        }
    }
}


class HomePageSectionModel {
    var title: String = ""
    var view_type: String = ""
    var content: MutableList<Any> = mutableListOf()

    fun setData(imageBaseUrl: String, videoBaseUrl: String, slugBaseUrl: String, slugDict: JSONObject, clipUrlDict: String, clipExt: String, data: JSONObject, matchDetailsDictModel: JSONObject? = null) {
        try {
            title = data.getString("title")
        } catch (e: Exception) {
            Log.e("FieldError:", "HomePageSectionModel field: " + "title")
        }

        try {
            view_type = data.getString("view_type")
        } catch (e: Exception) {
            Log.e("FieldError:", "HomePageSectionModel field: " + "view_type")
        }

        var contentValue: JSONArray? = null
        try {
            contentValue = data.getJSONArray("content")
        } catch (e: Exception) {
            Log.e("FieldError:", "HomePageSectionModel field: " + "content")
        }

        if (contentValue != null)
            when (view_type) {
                "live_row" -> {
                    for (i in 0 until contentValue.length()) {
                        val liveData = contentValue[i] as JSONObject
                        val liveModel = LiveModel()
                        liveModel.setData(slugBaseUrl, imageBaseUrl, liveData)
                        content.add(liveModel)
                    }
                }

                "highlights_row" -> {
                    for (i in 0 until contentValue.length()) {
                        val videoData = contentValue[i] as JSONObject
                        val videoModel = VideoModel()
                        videoModel.setBaseData(imageBaseUrl, videoBaseUrl, clipUrlDict, clipExt, "")
                        videoModel.setData(videoData)
                        videoModel.setVideoSlugUrl(slugBaseUrl, slugDict)
                        content.add(videoModel)
                    }
                }

                "results_row" -> {
                    for (i in 0 until contentValue.length()) {
                        val resultJson = contentValue[i] as JSONObject
                        val resultModel = ResultModel()
                        resultModel.setData(imageBaseUrl, videoBaseUrl, slugBaseUrl, slugDict, resultJson)
                        content.add(resultModel)
                    }
                }

                "fixtures_row" -> {
                    for (i in 0 until contentValue.length()) {
                        val fixtureJson = contentValue[i] as JSONObject
                        val fixtureModel = FixtureModel()
                        fixtureModel.setData(imageBaseUrl, "", fixtureJson)
                        content.add(fixtureModel)
                    }
                }
        }
    }
}

class LiveModel {
    var seriesId = ""
    var contentId = ""
    var matchId = ""
    var contentType = ""
    var title = ""
    var imageUrl = ""
    var matchName = ""
    var score = ""
    var needLogin = true
    var needSubscription = true
    var scorecardEnabled = false
    var commentaryEnabled = false

    var slugWithoutDomain = ""
    var slugUrl = ""

    var cc = ""
    var sources: MultipleLiveSourcesModel = MultipleLiveSourcesModel()

    fun setData(slugBaseUrl: String, imageBaseUrl: String, data: JSONObject) {
        try {
            slugWithoutDomain = data.getString("vslug")
            slugUrl = slugBaseUrl + slugWithoutDomain
        } catch (e: Exception) {
            Log.e("FieldError:", "LiveModel field: " + "vslug")
        }

        try {
            matchId = data.getString("match_id")
        } catch (e: Exception) {
            Log.e("FieldError:", "LiveModel field: " + "match_id")
        }

        try {
            seriesId = data.getString("series_id")
        } catch (e: Exception) {
            Log.e("FieldError:", "LiveModel field: " + "series_id")
        }

        try {
            contentId = data.getString("content_id")
        } catch (e: Exception) {
            Log.e("FieldError:", "LiveModel field: " + "content_id")
        }

        try {
            contentType = data.getString("content_type")
        } catch (e: Exception) {
            Log.e("FieldError:", "LiveModel field: " + "content_type")
        }

        try {
            title = data.getString("match_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "LiveModel field: " + "match_name")
        }

        try {
            val imageValue = data.getString("image")
            imageUrl = imageBaseUrl + imageValue
        } catch (e: Exception) {
            Log.e("FieldError:", "LiveModel field: " + "image")
        }

        try {
            matchName = data.getString("match_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "LiveModel field: " + "match_name")
        }

        try {
            score = data.getString("current_innings_score")
        } catch (e: Exception) {
            Log.e("FieldError:", "LiveModel field: " + "current_innings_score")
        }

        try {
            needLogin = data.getBoolean("need_login")
        } catch (e: Exception) {
            Log.e("FieldError:", "LiveModel field: " + "need_login")
        }

        try {
            needSubscription = data.getBoolean("need_subscription")
        } catch (e: Exception) {
            Log.e("FieldError:", "LiveModel field: " + "need_subscription")
        }

        try {
            scorecardEnabled = data.getBoolean("bscard")
        } catch (e: Exception) {
            Log.e("FieldError:", "LiveModel field: " + "bscard")
        }

        try {
            commentaryEnabled = data.getBoolean("bcomm")
        } catch (e: Exception) {
            Log.e("FieldError:", "LiveModel field: " + "bcomm")
        }

        try {
            val streamValue = data.getJSONObject("stream")
            sources.setIds(matchId, seriesId)
            sources.setData(streamValue)
        } catch (e: Exception) { }

        try {
            cc = data.getString("CC")
        } catch (e: Exception) {
            Log.e("FieldError:", "LiveModel field: " + "CC")
        }
    }


    fun setResultData(resultData: ResultModel) {
        seriesId = resultData.series_id
        contentId = ""
        matchId = resultData.match_id
        contentType = ""
        title = resultData.series_name
//        imageUrl = resultData.imageBaseUrl
        matchName = resultData.match_name
        score = ""
//        needLogin = resultData.needLogin
//        needSubscription = resultData.needSubscription
        scorecardEnabled = resultData.scorecardEnabled
        commentaryEnabled = resultData.commentaryEnabled

        cc = resultData.CC
//        sources = resultData.sources
    }
}

class MultipleLiveSourcesModel : Serializable {
    var match_id: String = ""
    var series_id: String = ""
    val streaming_url: String = ""
    val video_sources: MutableList<LiveSourceModel> = mutableListOf()

    fun setIds(matchId: String, seriesId: String) {
        match_id = matchId
        series_id = seriesId
    }

    fun setData(data: JSONObject) {
        val videoSources = data.getJSONArray("video_sources")
        for (i in 0 until videoSources.length()) {
            val videoSource = videoSources[i] as? JSONObject
            if (videoSource != null) {
                val liveSourcesModel = LiveSourceModel()
                liveSourcesModel.setIds(match_id, series_id)
                liveSourcesModel.setData(videoSource)
                video_sources.add(liveSourcesModel)
            }
        }
    }
}

class LiveSourceModel {
    var priority = 0
    var cdn = ""
    var title = ""
    var match_id: String = ""
    var series_id: String = ""

    fun setIds(matchId: String, seriesId: String) {
        match_id = matchId
        series_id = seriesId
    }

    fun setData(data: JSONObject) {
        try {
            priority = data.getInt("priority")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoSourceModel field: " + "priority")
        }

        try {
            title = data.getString("title")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoSourceModel field: " + "title")
        }

        try {
            cdn = data.getString("cdn")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoSourceModel field: " + "cdn")
        }
    }
}