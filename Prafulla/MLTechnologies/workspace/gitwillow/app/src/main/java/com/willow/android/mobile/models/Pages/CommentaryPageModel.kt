package com.willow.android.mobile.models.pages

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import com.willow.android.R
import com.willow.android.mobile.models.video.VideoModel
import org.json.JSONObject

class CommentaryPageModel {
    var HLSSuffix: String = ""
    var Innings: MutableList<CommentaryInningModel> = mutableListOf()
    var IsLive: Boolean = false
    var MatchId: String = ""
    var MatchName: String = ""
    var matchTitle: String = ""
    var MatchResult: String = ""
    var SeriesId: String = ""
    var ThumbnailFileExt: String = ""
    var VideoBaseUrl: String = ""
    var VideoFileExt: String = ""
    var slugUrl: String = ""
    var VideoThumbnailBaseUrl: String = ""
    var t1score: String = ""
    var t2score: String = ""

    fun setData(data: JSONObject) {
        try {
            HLSSuffix = data.getString("HLSSuffix")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryPageModel field: " + "HLSSuffix")
        }

        try {
            val InningsJson = data.getJSONArray("Innings")
            for (i in 0 until InningsJson.length()) {
                val inningJson = InningsJson[i] as? JSONObject
                if (inningJson != null) {
                    val inningModel = CommentaryInningModel()
                    inningModel.setData(inningJson)
                    Innings.add(inningModel)
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryPageModel field: " + "Innings")
        }

        try {
            IsLive = data.getBoolean("IsLive")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryPageModel field: " + "IsLive")
        }

        try {
            MatchId = data.getString("MatchId")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryPageModel field: " + "MatchId")
        }

        try {
            MatchName = data.getString("MatchName")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryPageModel field: " + "MatchName")
        }

        try {
            matchTitle = data.getString("match_s_name")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryPageModel field: " + "match_s_name")
        }

        try {
            MatchResult = data.getString("MatchResult")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryPageModel field: " + "MatchResult")
        }

        try {
            ThumbnailFileExt = data.getString("ThumbnailFileExt")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryPageModel field: " + "ThumbnailFileExt")
        }


        try {
            VideoBaseUrl = data.getString("VideoBaseUrl")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryPageModel field: " + "VideoBaseUrl")
        }

        try {
            VideoFileExt = data.getString("VideoFileExt")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryPageModel field: " + "VideoFileExt")
        }

        try {
            val VideoSlugBaseUrl = data.getString("VideoSlugBaseUrl")
            try {
                val commentaryMatchSlug = data.getString("commentaryMatchSlug")
                slugUrl = VideoSlugBaseUrl + commentaryMatchSlug
            } catch (e: java.lang.Exception) {
                Log.e("FieldError:", "CommentaryPageModel field: " + "commentaryMatchSlug")
            }
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryPageModel field: " + "VideoSlugBaseUrl")
        }

        try {
            VideoThumbnailBaseUrl = data.getString("VideoThumbnailBaseUrl")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryPageModel field: " + "VideoThumbnailBaseUrl")
        }

        try {
            t1score = data.getString("t1score")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryPageModel field: " + "t1score")
        }

        try {
            t2score = data.getString("t2score")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryPageModel field: " + "t2score")
        }

        completeData()
    }

    fun completeData() {
        if (HLSSuffix.isNotEmpty()) { VideoFileExt = VideoFileExt + "/" + HLSSuffix }

        for (inning in Innings) {
            for (inningData in inning.data) {
                for (ballByBall in inningData.ball_by_ball) {
                    ballByBall.completeData(matchTitle, MatchId, SeriesId, slugUrl, VideoThumbnailBaseUrl, ThumbnailFileExt, VideoBaseUrl, VideoFileExt)
                }
            }

            inning.data = inning.data.reversed().toMutableList()
            inning.setLatestVideosOfInning()
        }
    }

    /**
     * To Get the Video For Deeplink Url
     */
    fun findVideoModelFromClipId(clipId: String): VideoModel {
        for (inning in Innings) {
            var foundClipVideoModel = inning.findVideoModelFromClipId(clipId)
            if (foundClipVideoModel.isClip) {
                return foundClipVideoModel
            }
        }

        return VideoModel()
    }

    fun findRelatedVideoModelsOfClip(): MutableList<VideoModel> {
        for (inning in Innings) {
            var foundClipVideoModel = inning.findLatestClipsOfInning()
            return foundClipVideoModel
        }

        return mutableListOf()
    }

    /** Add Latest Score Balls for Live Match */
    fun updateLastOverData(latestBallsData: CommentaryPageModel) {
        try {
            for (existingInning in Innings) {
                if (latestBallsData.Innings[0].innId.equals(existingInning.innId, true)) {
                    existingInning.updateLastOverData(latestBallsData.Innings[0].data)
                }
            }
        } catch (e: Exception) {
            Log.e("DecodeError:", "Last Over data format is not correct")
        }
    }
}

class BallByBall {
    var display: String = ""
    var has_video: Boolean = false
    var title: String = ""
    var video_id: String = ""
    var background_color: Int = Color.WHITE
    var text_color: Int = Color.BLACK

    var videoModel: VideoModel = VideoModel()

    fun setData(data: JSONObject) {
        try {
            display = data.getString("display")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BallByBall field: " + "display")
        }

        try {
            has_video = data.getBoolean("has_video")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BallByBall field: " + "has_video")
        }

        try {
            title = data.getString("title")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BallByBall field: " + "title")
        }

        try {
            video_id = data.getString("video_id")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BallByBall field: " + "video_id")
        }
    }

    fun completeData(matchTitle: String, matchId: String, seriesId: String, slugUrl: String, imageBaseUrl: String, imageExtension: String, videoBaseUrl: String, videoExtension: String) {
        var background_color = R.color.scorecard_cell
        videoModel = VideoModel()
        videoModel.setClipData(matchTitle, matchId, seriesId, slugUrl, title, video_id, imageBaseUrl, imageExtension, videoBaseUrl, videoExtension)
        decideBallColor()
    }

    // Should be called in the last as it's dependent on the other data.
    @SuppressLint("ResourceAsColor")
    private fun decideBallColor() {
        text_color = Color.BLACK
        background_color = Color.WHITE

        if (has_video) {
            text_color = Color.WHITE
            background_color = Color.rgb(99, 181, 220)
        }

        if (display.equals("4")) {
            text_color = Color.WHITE
            background_color = Color.rgb(97, 133, 225)
        }

        if (display.equals("6")) {
            text_color = Color.WHITE
            background_color = Color.rgb(181, 137, 245)
        }

        if (display.equals("W" ,true)) {
            text_color = Color.WHITE
            background_color = Color.rgb(227,78,104)
        }
    }
}


class CommentaryInningModel {
    var data: MutableList<CommOverModel> = mutableListOf()
    var innId: String = ""
    var innName: String = ""
    var tnShort: String = ""

    var latestVideos: MutableList<VideoModel> = mutableListOf()

    fun setData(jsonData: JSONObject) {
        try {
            val dataArray = jsonData.getJSONArray("data")
            for (i in 0 until dataArray.length()) {
                val dataUnit = dataArray[i] as? JSONObject
                if (dataUnit != null) {
                    val commentaryDataModel = CommOverModel()
                    commentaryDataModel.setData(dataUnit)
                    data.add(commentaryDataModel)
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryInningModel field: " + "HLSSuffix")
        }

        try {
            innId = jsonData.getString("innId")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryInningModel field: " + "innId")
        }

        try {
            innName = jsonData.getString("innName")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryInningModel field: " + "innName")
        }

        try {
            tnShort = jsonData.getString("tnShort")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryInningModel field: " + "tnShort")
        }
    }

    fun findVideoModelFromClipId(clipId: String): VideoModel {
        for (over in data) {
            for (ball in over.ball_by_ball) {
                if (ball.video_id == clipId) {
                    return ball.videoModel
                }
            }
        }

        return VideoModel()
    }

    fun findLatestClipsOfInning(): MutableList<VideoModel> {
        var videoModels: MutableList<VideoModel> = mutableListOf()

        for (over in data) {
            for (ball in over.ball_by_ball) {
                videoModels.add(ball.videoModel)

                if (videoModels.size > 11) {
                    return videoModels
                }
            }
        }

        return videoModels
    }

    fun updateLastOverData(oversData: List<CommOverModel>) {
        for (i in (oversData.size - 1) downTo 0)  {
            addLiveOvers(oversData[i])
        }
    }

    fun addLiveOvers(latestOver: CommOverModel) {
        var lastAvailableOver = data[0]
        var overNumberInt = latestOver.over_number.toInt()

        if (overNumberInt == lastAvailableOver.over_number.toInt()) {
            data.removeAt(0)
            data.add(0, latestOver)
        } else if (overNumberInt > lastAvailableOver.over_number.toInt()) {
            data.add(0, latestOver)
        }
    }

    fun setLatestVideosOfInning(){
        var latestVideosLocalValue: MutableList<VideoModel> = mutableListOf()

        for (i in 0 until data.size) {
            val overData = data[i]
            for (j in 0 until overData.ball_by_ball.size) {
                    val ballData = overData.ball_by_ball[j]
                    if (ballData.has_video) {
                        latestVideosLocalValue.add(ballData.videoModel)

                        if (latestVideosLocalValue.size > 11) {
                            break
                        }
                    }
                }
            }
        latestVideos = latestVideosLocalValue
    }
}


class CommOverModel {
    var ball_by_ball: MutableList<BallByBall> = mutableListOf()
    var batsman: MutableList<String> = mutableListOf()
    var bowler: String = ""
    var over_number: String = ""
    var runs_in_over: Int = 0
    var score: String = ""

    fun setData(data: JSONObject) {
        try {
            val ballByBallJson = data.getJSONArray("ball_by_ball")
            for (i in 0 until ballByBallJson.length()) {
                val ballByBallData = ballByBallJson[i] as? JSONObject
                if (ballByBallData != null) {
                    val ballByBallModel = BallByBall()
                    ballByBallModel.setData(ballByBallData)
                    ball_by_ball.add(ballByBallModel)
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryData field: " + "ball_by_ball")
        }

        try {
            val batsmanJson = data.getJSONArray("batsman")
            for (i in 0 until batsmanJson.length()) {
                val batsmanData = batsmanJson[i] as? String
                if (batsmanData != null) {
                    batsman.add(batsmanData)
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryData field: " + "HLSSuffix")
        }

        try {
            bowler = data.getString("bowler")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryData field: " + "bowler")
        }

        try {
            over_number = data.getString("over_number")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryData field: " + "over_number")
        }

        try {
            runs_in_over = data.getInt("runs_in_over")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryData field: " + "runs_in_over")
        }

        try {
            score = data.getString("score")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "CommentaryData field: " + "score")
        }
    }
}