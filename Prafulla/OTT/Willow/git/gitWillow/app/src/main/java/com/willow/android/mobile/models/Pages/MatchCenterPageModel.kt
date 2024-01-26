package com.willow.android.mobile.models.pages

import android.util.Log
import com.willow.android.mobile.models.video.MatchCenterVideoModel
import com.willow.android.mobile.models.video.VideosSectionModel
import org.json.JSONObject

class MatchCenterPageModel {
    var result: MatchCenterResultModel = MatchCenterResultModel()
    fun setData(matchId: String, data: String) {
        try {
            val dataJson = JSONObject(data)
            val resultJson = dataJson.getJSONObject("result")
            result.setData(matchId, resultJson)
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterPageModel field: " + "result")
        }
    }
}

class MatchCenterResultModel {
    var imageBaseUrl = ""
    var videoBaseUrl = ""
    var slugBaseUrl = ""
    var slugDict: JSONObject = JSONObject()
    var clipUrlDict = ""
    var clipExt = ""

    var matchId = ""
    var matchTitle = ""
    var shortMatchName = ""
    var seriesName = ""
    var winningTeamShortName = ""
    var selectedVideo = MatchCenterVideoModel()
    var videoSections: MutableList<VideosSectionModel> = mutableListOf()

    var scorecardEnabled = false
    var commentaryEnabled = false
    var hasSelectedVideo = false

    fun setData(matchId: String, data: JSONObject) {
        this.matchId = matchId

        try {
            imageBaseUrl = data.getString("thumb_base_url")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "thumb_base_url")
        }

        try {
            videoBaseUrl = data.getString("playback_base_url")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "playback_base_url")
        }

        try {
            slugBaseUrl = data.getString("slug_base_url")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "slug_base_url")
        }

        createSlugUrlDict(matchId, data)

        try {
            val clipUrlDictJson = data.getJSONObject("VideoBaseUrl")
            clipUrlDict = clipUrlDictJson.toString()
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "VideoBaseUrl")
        }

        try {
            clipExt = data.getString("VideoFileExt")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "VideoFileExt")
        }

        try {
            val HLSSuffixValue = data.getString("HLSSuffix")
            clipExt = clipExt + "/" + HLSSuffixValue
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "HLSSuffix")
        }

        try {
            matchTitle = data.getString("match_s_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "match_s_name")
        }

        try {
            shortMatchName = data.getString("shortMatchName")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "shortMatchName")
        }

        try {
            seriesName = data.getString("seriesName")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "seriesName")
        }

        try {
            winningTeamShortName = data.getString("winning_team_short_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "winning_team_short_name")
        }

        try {
            val selectedVideoJson = data.getJSONObject("selected_video")
            selectedVideo.setData(imageBaseUrl, winningTeamShortName, selectedVideoJson)
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "selected_video")
        }

        try {
            val videoSectionsJson = data.getJSONArray("video_data")
            for (i in 0 until videoSectionsJson.length()) {
                val videoSectionJson = videoSectionsJson[i] as? JSONObject
                if (videoSectionJson != null) {
                    val videoSectionModel = VideosSectionModel()
                    videoSectionModel.setBaseData(imageBaseUrl, videoBaseUrl, slugBaseUrl, slugDict, clipUrlDict, clipExt, matchTitle = matchTitle)
                    videoSectionModel.setJsonData(videoSectionJson)
                    videoSections.add(videoSectionModel)
                }
            }
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "bscard")
        }


        try {
            scorecardEnabled = data.getBoolean("bscard")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "bscard")
        }

        try {
            commentaryEnabled = data.getBoolean("bcomm")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "bcomm")
        }
    }

    fun createSlugUrlDict(matchId: String, data: JSONObject) {
        var matchSlug = ""
        var replayMatchSlug = ""
        var highlightsMatchSlug = ""
        var scorecardMatchSlug = ""
        var commentaryMatchSlug = ""

        try {
            matchSlug = data.getString("matchSlug")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "matchSlug")
        }

        try {
            replayMatchSlug = data.getString("replayMatchSlug")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "replayMatchSlug")
        }

        try {
            highlightsMatchSlug = data.getString("highlightsMatchSlug")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "highlightsMatchSlug")
        }

        try {
            scorecardMatchSlug = data.getString("scorecardMatchSlug")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "scorecardMatchSlug")
        }

        try {
            commentaryMatchSlug = data.getString("highlightsMatchSlug")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterResultModel field: " + "commentaryMatchSlug")
        }


        var dict = JSONObject()
        dict.put("matchSlug", matchSlug)
        dict.put("replayMatchSlug", replayMatchSlug)
        dict.put("highlightsMatchSlug", highlightsMatchSlug)
        dict.put("scorecardMatchSlug", scorecardMatchSlug)
        dict.put("commentaryMatchSlug", commentaryMatchSlug)

        slugDict.put(matchId, dict)
    }
}