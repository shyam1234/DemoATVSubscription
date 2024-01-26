package com.willow.android.mobile.models.video

import android.util.Log
import com.willow.android.mobile.utils.Utils
import org.json.JSONObject


class VideosSectionModel {
    var imageBaseUrl = ""
    var videoBaseUrl = ""
    var slugBaseUrl = ""
    var slugDict: JSONObject = JSONObject()
    var matchDetailsDictModel: JSONObject = JSONObject()
    var matchTitle: String = ""
    var shouldCheckMatchTitleInDict = false
    var clipUrlDict = ""
    var clipExt = ""

    var mobile_view: String = ""
    var tablet_view: String = ""
    var title: String = ""
    var videos: MutableList<VideoModel> = mutableListOf()
    var isVerticalScroll: Boolean = false
    var groupedVideos: MutableList<MutableList<VideoModel>> = mutableListOf()

    fun setBaseData(imageBaseUrl: String, videoBaseUrl: String, slugBaseUrl: String, slugDict: JSONObject, clipUrlDict: String, clipExt: String, sectionViewType: String? = null, matchDetailsDictModel: JSONObject? = null, matchTitle: String? = null) {
        this.imageBaseUrl = imageBaseUrl
        this.videoBaseUrl = videoBaseUrl
        this.slugBaseUrl = slugBaseUrl
        this.slugDict = slugDict
        this.clipUrlDict = clipUrlDict
        this.clipExt = clipExt
        if (matchDetailsDictModel != null) {
            this.matchDetailsDictModel = matchDetailsDictModel
            shouldCheckMatchTitleInDict = true
        }
        if (matchTitle != null) {
            this.matchTitle = matchTitle
        }

        if (sectionViewType != null) {
            mobile_view = sectionViewType
        }
    }

    fun setJsonData(data: JSONObject) {
        try {
            mobile_view = data.getString("mobile_view")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideosSectionModel field: " + "mobile_view")
        }

        try {
            tablet_view = data.getString("tablet_view")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideosSectionModel field: " + "tablet_view")
        }

        try {
            title = data.getString("title")
        } catch (e: Exception) {
            Log.e("FieldError:", "VideosSectionModel field: " + "title")
        }

        if (mobile_view.equals("vertical_grid", true)) {
            isVerticalScroll = true
        }

        try {
            val videosJson = data.getJSONArray("videos")
            for (i in 0 until videosJson.length()) {
                val videoJson = videosJson[i] as? JSONObject
                if (videoJson != null) {
                    val videoModel = VideoModel()
                    videoModel.setBaseData(imageBaseUrl, videoBaseUrl, clipUrlDict, clipExt, matchTitle)
                    videoModel.setData(videoJson)
                    videoModel.setVideoSlugUrl(slugBaseUrl, slugDict)
                    
                    updateVideoSpecificMatchTitleIfAvailable(videoModel.matchId)
                    videoModel.setMatchTitleValue(matchTitle)

                    if (Utils.isAllowedInCountry(videoModel.cc)) {
                        videos.add(videoModel)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FieldError:", "VideosSectionModel field: " + "title")
        }
        createGroupedVideosData()
    }

    fun setVideoModels(videoModels: MutableList<VideoModel>) {
        for (i in 0 until videoModels.size) {
            val videoModel = videoModels[i]
            if (Utils.isAllowedInCountry(videoModel.cc)) {
                videos.add(videoModel)
            }
        }
        createGroupedVideosData()
    }

    private fun createGroupedVideosData() {
        val threeVideosGroup: MutableList<VideoModel> = mutableListOf()

        for (i in 0 until videos.size) {
            threeVideosGroup.add(videos[i])

            if ((threeVideosGroup.size == 3) || (i == (videos.size - 1))) {
                val groupVideosValue = threeVideosGroup.toMutableList()
                groupedVideos.add(groupVideosValue)
                threeVideosGroup.clear()
            }
        }
    }

    private fun updateVideoSpecificMatchTitleIfAvailable(matchId: String) {
        if (shouldCheckMatchTitleInDict) {
            try {
                val matchDetailsDict = matchDetailsDictModel.getJSONObject(matchId)
                val matchTitleValue = matchDetailsDict.getString("match_s_name")
                if (matchTitleValue.isNotEmpty()) {
                    matchTitle = matchTitleValue
                } else {
                    matchTitle = ""
                }
            } catch (e: Exception) {
                matchTitle = ""
                Log.e("FieldError:", "VideosSectionModel field: " + "title")
            }
        }
    }
}