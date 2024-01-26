package com.willow.android.mobile.models.video

import android.util.Log
import com.willow.android.mobile.models.pages.LiveModel
import com.willow.android.mobile.utils.Utils
import org.json.JSONObject
import java.io.Serializable


class VideoModel : Serializable {
    var imageBaseUrl = ""
    var videoBaseUrl = ""
    var clipBaseUrl = ""
    var clipUrlDict = ""
    var clipExt = ""
    var streamUrl = "" // If its a clip, its a stream url otherwise it will be hls stream url.

    var slugUrl = ""
    var slugWithoutDomain = ""

    var matchTitle = ""
    var matchId = ""
    var seriesId = ""
    var contentId = ""
    var clipId = ""
    var contentType = ""
    var title = ""
    var altTitle = ""
    var imageUrl = ""
    var videoParams = ""
    var duration = ""
    var durationSecondsString = ""
    var isClip = false
    var isLive = false
    /** Default Live Priority of any Live Video is 1.
     * This value should be changed if the video is live and User has Selected a Live Priority from the popup */
    var livePriority = "1"
    var needLogin = true
    var needSubscription = true
    var cc = ""
    var isDataSet = false

    fun setMatchTitleValue(matchTitleValue: String) {
        matchTitle = matchTitleValue
    }

    fun setBaseData(imageBaseUrl: String, videoBaseUrl: String, clipUrlDict: String, clipExt: String, matchTitle: String) {
        this.imageBaseUrl = imageBaseUrl
        this.videoBaseUrl = videoBaseUrl
        this.clipUrlDict = clipUrlDict
        this.clipExt = clipExt
        this.matchTitle = matchTitle
    }

    fun setIdsData(matchId: String, seriesId: String, cc: String) {
        this.matchId = matchId
        this.seriesId = seriesId
        this.cc = cc
    }

    fun setData(data: JSONObject) {
        try {
            matchId = data.getString("match_id")
        } catch (e: Exception) {}

        if (matchId.isNotEmpty()) {
            try {
                val clipUrlDictJson = JSONObject(clipUrlDict)
                clipBaseUrl = clipUrlDictJson.getString(matchId)
            } catch (e: Exception) {}
        }

        try {
            seriesId = data.getString("series_id")
        } catch (e: Exception) {}

        try {
            contentId = data.getString("content_id")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoModel field: " + "content_id")
        }

        try {
            clipId = data.getString("video_id")
        } catch (e: Exception) {}

        try {
            contentType = data.getString("content_type")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoModel field: " + "content_type")
        }

        try {
            title = data.getString("title")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoModel field: " + "title")
        }

        try {
            altTitle = data.getString("altTitle")
        } catch (e: Exception) {}
        
        if (contentType.lowercase().equals("replay", true)) {
            title = altTitle
        }

        try {
            val imageValue = data.getString("image")
            imageUrl = imageBaseUrl + imageValue
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoModel field: " + "image")
        }

        try {
            val durationInt = data.getInt("duration")
            duration = Utils.getFormattedDuration(durationInt)
            durationSecondsString = duration.toString()
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoModel field: " + "duration")
        }

        try {
            needLogin = data.getBoolean("need_login")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoModel field: " + "need_login")
        }

        try {
            needSubscription = data.getBoolean("need_subscription")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoModel field: " + "need_subscription")
        }

        try {
            cc = data.getString("CC")
        } catch (e: Exception) {}
        
        if (contentType.lowercase().equals("clip", true)) {
            isClip = true
            streamUrl = clipBaseUrl + clipId + clipExt
        } else {
            isClip = false
        }

        if (contentType.lowercase().equals("highlight", true)) {
            videoParams = "mid=" + matchId + "&type=" + contentType + "&id=" + contentId + "&need_login=" + needLogin.toString() + "&need_subscription=" + needSubscription.toString()
        } else {
            videoParams = "mid=" + matchId + "&type=" + contentType + "&title=" + contentId + "&need_login=" + needLogin.toString() + "&need_subscription=" + needSubscription.toString()
        }

        try {
            slugWithoutDomain = data.getString("vslug")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoModel field: " + "vslug")
        }

        isDataSet = true
    }

    fun setMatchCenterVideoData(mcVideoModel: MatchCenterVideoModel, livePriority: Int) {
        this.title = mcVideoModel.title
        this.matchId = mcVideoModel.matchId
        this.seriesId = mcVideoModel.seriesId
        this.contentId = mcVideoModel.contentId
        this.contentType = mcVideoModel.contentType
        this.imageUrl = mcVideoModel.image
        this.needLogin = mcVideoModel.needLogin
        this.needSubscription = mcVideoModel.needSubscription
        this.isLive = mcVideoModel.isLive
        this.slugWithoutDomain = mcVideoModel.slugWithoutDomain

        try {
            val clipUrlDictJson = JSONObject(clipUrlDict)
            clipBaseUrl = clipUrlDictJson.getString(matchId)
        } catch (e: Exception) {
            Log.e("FieldError:", "VideoModel field: " + "clipUrlDict")
        }

        if (contentType.lowercase().equals("clip", true)) {
            isClip = true
            streamUrl = clipBaseUrl + clipId + clipExt
        } else {
            isClip = false
        }

        if (contentType.lowercase().equals("highlight", true)) {
            videoParams = "mid=" + matchId + "&type=" + contentType + "&id=" + contentId + "&need_login=" + needLogin.toString() + "&need_subscription=" + needSubscription.toString()
        } else if (contentType.lowercase().equals("live", true)) {
            this.livePriority = livePriority.toString()
            videoParams = "mid=" + matchId + "&type=" + contentType + "&pr=" + this.livePriority + "&need_login=" + needLogin.toString()+ "&need_subscription=" + needSubscription.toString()
        } else {
            videoParams = "mid=" + matchId + "&type=" + contentType + "&title=" + contentId + "&need_login=" + needLogin.toString()+ "&need_subscription=" + needSubscription.toString()
        }
    }


    fun setClipData(matchTitle: String, matchIdValue: String, seriesIdValue: String, slugUrlValue: String, titleValue: String, clipIdValue: String, imageBaseUrl: String, imageExtension: String, videoBaseUrl: String, videoExtension: String) {
        this.matchTitle = matchTitle

        slugWithoutDomain = slugUrlValue
        slugUrl = slugUrlValue
        title = titleValue
        imageUrl = imageBaseUrl + clipIdValue + imageExtension
        streamUrl = videoBaseUrl + clipIdValue + videoExtension
        matchId = matchIdValue
        seriesId = seriesIdValue
        contentId = clipIdValue
        clipId = clipIdValue
        isClip = true
        isLive = false
        needSubscription = false
        needLogin = false
        contentType = "clip"
    }

    fun setLiveData(liveData: LiveModel) {
        slugUrl = liveData.slugUrl
        slugWithoutDomain = liveData.slugWithoutDomain
        title = liveData.title
        matchId = liveData.matchId
        seriesId = liveData.seriesId
        contentId = liveData.contentId
        contentType = liveData.contentType
        if (contentType.isEmpty()) {
            contentType = "live"
        }
        imageUrl = liveData.imageUrl
        isLive = true
        isClip = false
        needLogin = liveData.needLogin
        needSubscription = liveData.needSubscription
        cc = liveData.cc
    }

    fun setDeeplinkData(data: JSONObject) {
        try {
            cc = data.getString("CC")
        } catch (e: Exception) {
            Log.e("FieldError:", "DeeplinkData field: " + "CC")
        }

        try {
            contentId = data.getString("content_id")
        } catch (e: Exception) {
            Log.e("FieldError:", "DeeplinkData field: " + "content_id")
        }

        try {
            matchId = data.getString("match_id")
        } catch (e: Exception) {
            Log.e("FieldError:", "DeeplinkData field: " + "match_id")
        }

        try {
            seriesId = data.getString("series_id")
        } catch (e: Exception) {}

        try {
            contentType = data.getString("content_type")
        } catch (e: Exception) {
            Log.e("FieldError:", "DeeplinkData field: " + "content_type")
        }

        try {
            val durationInt = data.getInt("duration")
            duration = Utils.getFormattedDuration(durationInt)
            durationSecondsString = duration.toString()
        } catch (e: Exception) {
            Log.e("FieldError:", "DeeplinkData field: " + "duration")
        }

        try {
            val imageValue = data.getString("image")
            imageUrl = imageBaseUrl + imageValue
        } catch (e: Exception) {
            Log.e("FieldError:", "DeeplinkData field: " + "image")
        }

        try {
            needLogin = data.getBoolean("need_login")
        } catch (e: Exception) {
            Log.e("FieldError:", "DeeplinkData field: " + "need_login")
        }

        try {
            needSubscription = data.getBoolean("need_subscription")
        } catch (e: Exception) {
            Log.e("FieldError:", "DeeplinkData field: " + "need_subscription")
        }

        try {
            streamUrl = data.getString("stream_url")
        } catch (e: Exception) {
            Log.e("FieldError:", "DeeplinkData field: " + "stream_url")
        }

        try {
            title = data.getString("title")
        } catch (e: Exception) {
            Log.e("FieldError:", "DeeplinkData field: " + "title")
        }

        try {
            slugWithoutDomain = data.getString("vslug")
        } catch (e: Exception) {
            Log.e("FieldError:", "DeeplinkData field: " + "vslug")
        }

        if (contentType.lowercase().equals("highlight", true)) {
            videoParams = "mid=" + matchId + "&type=" + contentType + "&id=" + contentId + "&need_login=" + needLogin.toString()+ "&need_subscription=" + needSubscription.toString()
        }
    }

    /** It should be set after setting all the data of VideoModel */
    fun setVideoSlugUrl(slugBaseUrl: String, slugDict: JSONObject) {
        if (slugWithoutDomain.isNotEmpty()) {
            slugUrl = slugBaseUrl + slugWithoutDomain
            return
        }

        try {
            val videoMatchSlugData = slugDict.getJSONObject(matchId)
            if (contentType.lowercase().equals("highlight", true)) {
                try {
                    val matchSlugValue = videoMatchSlugData.getString("highlightsMatchSlug")
                    slugUrl = slugBaseUrl + matchSlugValue
                } catch (e: Exception) {}
            } else {
                try {
                    val matchSlugValue = videoMatchSlugData.getString("replayMatchSlug")
                    slugUrl = slugBaseUrl + matchSlugValue
                } catch (e: Exception) {}
            }

            if (slugUrl.isEmpty()) {
                try {
                    val matchSlugValue = videoMatchSlugData.getString("matchSlug")
                    slugUrl = slugBaseUrl + matchSlugValue
                } catch (e: Exception){}
            }

        } catch (e: Exception) {
            Log.e("FieldError:", "VideoModel field: " + "slugDict")
        }
    }
}

class MatchCenterVideoModel {
    var imageBaseUrl = ""
    var playbackBaseUrl = ""

    var title = ""
    var seriesName = ""
    var matchResult = ""
    var isLive = true
    var contentType = ""
    var image = ""
    var matchId = ""
    var seriesId = ""
    var contentId = ""
    var teamOneName = ""
    var teamOneShortName = ""
    var teamTwoName = ""
    var teamTwoShortName = ""
    var teamOneLogo = ""
    var teamTwoLogo = ""
    var teamOneScore = ""
    var teamTwoScore = ""
    var teamOneWon = false
    var teamTwoWon = false
    var duration = ""
    var needLogin = true
    var needSubscription = true
    var matchTitle = ""

    var isSelectedVideoDataValid = false

    var slugWithoutDomain = ""

    fun setData(imageBaseUrl: String, winningTeamShortName: String, data: JSONObject) {
        try {
            title = data.getString("title")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "title")
        }

        try {
            seriesName = data.getString("series_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "series_name")
        }

        try {
            matchResult = data.getString("match_result")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "match_result")
        }

        try {
            isLive = data.getBoolean("is_live")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "is_live")
        }

        try {
            contentType = data.getString("content_type")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "content_type")
        }

        try {
            val imageValue = data.getString("image")
            image = imageBaseUrl + imageValue
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "image")
        }

        try {
            matchId = data.getString("match_id")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "match_id")
        }

        try {
            seriesId = data.getString("series_id")
        } catch (e: Exception) {}

        try {
            contentId = data.getString("content_id")
            isSelectedVideoDataValid = true
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "content_id")
        }

        try {
            teamOneName = data.getString("team_one_fname")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "team_one_fname")
        }

        try {
            teamTwoName = data.getString("team_two_fname")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "team_two_fname")
        }

        try {
            teamOneShortName = data.getString("team_one_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "team_one_name")
        }

        try {
            teamTwoShortName = data.getString("team_two_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "team_two_name")
        }

        try {
            val logoValue = data.getString("team_one_logo")
            teamOneLogo = imageBaseUrl + logoValue
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "team_one_logo")
        }

        try {
            val logoValue = data.getString("team_two_logo")
            teamTwoLogo = imageBaseUrl + logoValue
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "team_two_logo")
        }

        try {
            val shortScore = data.getJSONObject("short_score")

            try {
                val t11InningsScore = shortScore.getString("T11InningsScore")
                if (t11InningsScore.isNotEmpty()) {
                    teamOneScore = t11InningsScore
                }
            } catch (e: Exception) {}

            try {
                val t11InningsOvers = shortScore.getString("T11InningsOvers")
                if (t11InningsOvers.isNotEmpty()) {
                    teamOneScore = teamOneScore + "(" + t11InningsOvers + ")"
                }
            } catch (e: Exception) {}

            try {
                val t12InningsScore = shortScore.getString("T12InningsScore")
                if (t12InningsScore.isNotEmpty()) {
                    teamOneScore = teamOneScore +  "  |  " + t12InningsScore
                }
            } catch (e: Exception) {}

            try {
                val t12InningsOvers = shortScore.getString("T12InningsOvers")
                if (t12InningsOvers.isNotEmpty()) {
                    teamOneScore = teamOneScore + "(" + t12InningsOvers + ")"
                }
            } catch (e: Exception) {}

            try {
                val t21InningsScore = shortScore.getString("T21InningsScore")
                if (t21InningsScore.isNotEmpty()) {
                    teamTwoScore = t21InningsScore
                }
            } catch (e: Exception) {}

            try {
                val t21InningsOvers = shortScore.getString("T21InningsOvers")
                if (t21InningsOvers.isNotEmpty()) {
                    teamTwoScore = teamTwoScore + "(" + t21InningsOvers + ")"
                }
            } catch (e: Exception) {}

            try {
                val t22InningsScore = shortScore.getString("T22InningsScore")
                if (t22InningsScore.isNotEmpty()) {
                    teamTwoScore = teamTwoScore +  "  |  " + t22InningsScore
                }
            } catch (e: Exception) {}

            try {
                val t22InningsOvers = shortScore.getString("T22InningsOvers")
                if (t22InningsOvers.isNotEmpty()) {
                    teamTwoScore = teamTwoScore + "(" + t22InningsOvers + ")"
                }
            } catch (e: Exception) {}
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "short_score")
        }

        try {
            val durationValue = data.getString("duration")
            val durationInt = durationValue.toInt()
            duration = Utils.getFormattedDuration(durationInt)
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "duration")
        }

        try {
            needLogin = data.getBoolean("need_login")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "need_login")
        }

        try {
            needSubscription = data.getBoolean("need_subscription")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "need_subscription")
        }

        if (teamOneShortName.lowercase().equals(winningTeamShortName, true)) {
            teamOneWon = true
        } else {
            teamTwoWon = true
        }

        try {
            matchTitle = data.getString("match_s_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "match_s_name")
        }

        try {
            slugWithoutDomain = data.getString("vslug")
        } catch (e: Exception) {
            Log.e("FieldError:", "MatchCenterVideoModel field: " + "vslug")
        }
    }
}

class SuggestedVideosModel: Serializable {
    var list: List<VideoModel> = mutableListOf()
    var groupedVideos: MutableList<MutableList<VideoModel>> = mutableListOf()

    fun setData(videos: List<VideoModel>) {
        list = videos
        createGroupedVideosData()
    }

    fun createGroupedVideosData() {
        val threeVideosGroup: MutableList<VideoModel> = mutableListOf()

        for (i in 0 until list.size) {
            threeVideosGroup.add(list[i])

            if ((threeVideosGroup.size == 3) || (i == (list.size - 1))) {
                val groupVideosValue = threeVideosGroup.toMutableList()
                groupedVideos.add(groupVideosValue)
                threeVideosGroup.clear()
            }
        }
    }
}