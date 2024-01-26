package com.willow.android.mobile.models.pages

import android.util.Log
import com.willow.android.mobile.models.video.VideoModel
import org.json.JSONObject


class ScorecardPageModel {
    var result: ScorecardResultModel = ScorecardResultModel()

    fun setData(seriesId: String, data: String) {
        try {
            val dataJson = JSONObject(data)
            val resultJson = dataJson.getJSONObject("result")
            result.setData(seriesId, resultJson)
        } catch (e: Exception) {
            Log.e("FieldError:", "ScorecardPageModel field: " + "result")
        }
    }
}

class ScorecardResultModel {
    var HLSSuffix: String = ""
    var IsLive: Boolean = false
    var MatchId: String = ""
    var seriesId: String = ""
    var MatchName: String = ""
    var matchTitle: String = ""
    var MatchResult: String = ""
    var ThumbnailFileExt: String = ""
    var VideoBaseUrl: String = ""
    var VideoFileExt: String = ""
    var VideoThumbnailBaseUrl: String = ""
    var slugUrl: String = ""
    var status: String = ""
    var Innings: MutableList<ScorecardInningModel> = mutableListOf()

    fun setData(seriesId: String, data: JSONObject) {
        this.seriesId = seriesId

        try {
            HLSSuffix = data.getString("HLSSuffix")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardResultModel field: " + "HLSSuffix")
        }

        try {
            IsLive = data.getBoolean("IsLive")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardResultModel field: " + "IsLive")
        }

        try {
            MatchId = data.getString("MatchId")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardResultModel field: " + "MatchId")
        }

        try {
            MatchName = data.getString("MatchName")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardResultModel field: " + "MatchName")
        }

        try {
            matchTitle = data.getString("match_s_name")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardResultModel field: " + "match_s_name")
        }

        try {
            MatchResult = data.getString("MatchResult")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardResultModel field: " + "MatchResult")
        }

        try {
            ThumbnailFileExt = data.getString("ThumbnailFileExt")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardResultModel field: " + "ThumbnailFileExt")
        }

        try {
            VideoBaseUrl = data.getString("VideoBaseUrl")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardResultModel field: " + "VideoBaseUrl")
        }

        try {
            VideoFileExt = data.getString("VideoFileExt")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardResultModel field: " + "VideoFileExt")
        }

        try {
            VideoThumbnailBaseUrl = data.getString("VideoThumbnailBaseUrl")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardResultModel field: " + "VideoThumbnailBaseUrl")
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
            status = data.getString("status")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardResultModel field: " + "status")
        }

        try {
            val inningsArray = data.getJSONArray("Innings")
            for (i in 0 until inningsArray.length()) {
                val inningJson = inningsArray[i] as? JSONObject
                if (inningJson != null) {
                    val inningModel = ScorecardInningModel()
                    inningModel.setData(inningJson)
                    Innings.add(inningModel)
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardResultModel field: " + "Innings")
        }

        completeData()
    }


    fun completeData() {
        for (inning in Innings) {
            var videoExtensionvarue = VideoFileExt
            if (HLSSuffix.isNotEmpty()) {
                videoExtensionvarue = VideoFileExt + "/" + HLSSuffix
            }

            inning.completeData(matchTitle, MatchId, seriesId, slugUrl, VideoThumbnailBaseUrl, ThumbnailFileExt, VideoBaseUrl, videoExtensionvarue)
        }
    }
}

class ScorecardInningModel {
    var BatsmenInningSummaries: MutableList<BatsmenInningSummary> = mutableListOf()
    var BattingTeamShortName: String = ""
    var BowlerInningSummaries: MutableList<BowlerInningSummary> = mutableListOf()
    var Extras: Extras = Extras()
    var FOWData: MutableList<String> = mutableListOf()
    var NotPlayedPlayers: MutableList<String> = mutableListOf()
    var TotalRuns: String = ""
    var Wickets: String = ""
    var innName: String = ""
    var totalRunsWickets: String  = ""
    var didNotBatPlayers: String = ""
    var fowPlayers: String = ""
    var overs: String = ""
    var runRate: String = ""

    fun setData(data: JSONObject) {
        try {
            val summeriesArray = data.getJSONArray("BatsmenInningSummaries")
            for (i in 0 until summeriesArray.length()) {
                val inningJson = summeriesArray[i] as? JSONObject
                if (inningJson != null) {
                    val summaryModel = BatsmenInningSummary()
                    summaryModel.setData(inningJson)
                    BatsmenInningSummaries.add(summaryModel)
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardInningModel field: " + "BatsmenInningSummaries")
        }

        try {
            BattingTeamShortName = data.getString("BattingTeamShortName")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardInningModel field: " + "BattingTeamShortName")
        }

        try {
            val summeriesArray = data.getJSONArray("BowlerInningSummaries")
            for (i in 0 until summeriesArray.length()) {
                val inningJson = summeriesArray[i] as? JSONObject
                if (inningJson != null) {
                    val summaryModel = BowlerInningSummary()
                    summaryModel.setData(inningJson)
                    BowlerInningSummaries.add(summaryModel)
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardInningModel field: " + "BowlerInningSummaries")
        }

        try {
            val extrasJson = data.getJSONObject("Extras")
            Extras.setData(extrasJson)
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardInningModel field: " + "Extras")
        }

        try {
            val fowArray = data.getJSONArray("FOWData")
            for (i in 0 until fowArray.length()) {
                val fowDataString = fowArray[i] as? String
                if (fowDataString != null) {
                    FOWData.add(fowDataString)
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardInningModel field: " + "FOWData")
        }

        try {
            val npArray = data.getJSONArray("NotPlayedPlayers")
            for (i in 0 until npArray.length()) {
                val npDataString = npArray[i] as? String
                if (npDataString != null) {
                    NotPlayedPlayers.add(npDataString)
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardInningModel field: " + "NotPlayedPlayers")
        }

        try {
            TotalRuns = data.getString("TotalRuns")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardInningModel field: " + "TotalRuns")
        }

        try {
            Wickets = data.getString("Wickets")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardInningModel field: " + "Wickets")
        }

        try {
            innName = data.getString("innName")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardInningModel field: " + "innName")
        }

        try {
            totalRunsWickets = data.getString("totalRunsWickets")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardInningModel field: " + "totalRunsWickets")
        }

        try {
            didNotBatPlayers = data.getString("didNotBatPlayers")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardInningModel field: " + "didNotBatPlayers")
        }

        try {
            fowPlayers = data.getString("fowPlayers")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardInningModel field: " + "fowPlayers")
        }

        try {
            val oversValue = data.getString("Overs")
            overs = oversValue + " Overs"
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardInningModel field: " + "Overs")
        }

        try {
            val runRateValue = data.getString("RunRate")
            runRate = "   (" + runRateValue + " runs per over" + ")"
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "ScorecardInningModel field: " + "RunRate")
        }
    }

    fun completeData(matchTitle: String, matchId: String, seriesId: String, slugUrl: String, imageBaseUrl: String, imageExtension: String, videoBaseUrl: String, videoExtension: String) {
        if (!TotalRuns.isEmpty()) {
            totalRunsWickets = "TOTAL   " + TotalRuns

            if (!Wickets.isEmpty()) {
                totalRunsWickets = totalRunsWickets + "/" + Wickets
            }
        }

        for (i in 0..(NotPlayedPlayers.count() - 1)) {
            var player = NotPlayedPlayers[i]
            if (i == 0) {
                didNotBatPlayers = player
            } else {
                didNotBatPlayers = didNotBatPlayers + ", " + player
            }
        }

        for (i in 0..(FOWData.count() - 1)) {
            var fow = FOWData[i]
            if (i == 0) {
                fowPlayers = fow
            } else {
                fowPlayers = fowPlayers + ", " + fow
            }
        }

        for (i in 0..(BatsmenInningSummaries.count() - 1)) {
            BatsmenInningSummaries[i].completeData(matchTitle, matchId, seriesId, slugUrl, imageBaseUrl, imageExtension, videoBaseUrl, videoExtension)
        }

        for (i in 0..(BowlerInningSummaries.count() - 1)) {
            BowlerInningSummaries[i].completeData(matchTitle, matchId, seriesId, slugUrl, imageBaseUrl, imageExtension, videoBaseUrl, videoExtension)
        }
    }
}



class BatsmenInningSummary {
    var AllUrls: String = ""
    var Balls: String = ""
    var BatsmanId: String = ""
    var FOWUrls: String = ""
    var FallOfWicket: String = ""
    var FourUrls: String = ""
    var Fours: String = ""
    var Name: String = ""
    var Runs: String = ""
    var SixUrls: String = ""
    var Sixes: String = ""
    var StrikeRate: String = ""
    var Wicket: String = ""
    var WicketUrls: String = ""
    var videos: MutableList<VideoModel> = mutableListOf()

    fun setData(data: JSONObject) {
        try {
            AllUrls = data.getString("AllUrls")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BatsmenInningSummary field: " + "AllUrls")
        }

        try {
            Balls = data.getString("Balls")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BatsmenInningSummary field: " + "Balls")
        }

        try {
            BatsmanId = data.getString("BatsmanId")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BatsmenInningSummary field: " + "BatsmanId")
        }

        try {
            FOWUrls = data.getString("FOWUrls")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BatsmenInningSummary field: " + "FOWUrls")
        }

        try {
            FallOfWicket = data.getString("FallOfWicket")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BatsmenInningSummary field: " + "FallOfWicket")
        }

        try {
            FourUrls = data.getString("FourUrls")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BatsmenInningSummary field: " + "FourUrls")
        }

        try {
            Fours = data.getString("Fours")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BatsmenInningSummary field: " + "Fours")
        }

        try {
            Name = data.getString("Name")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BatsmenInningSummary field: " + "Name")
        }

        try {
            Runs = data.getString("Runs")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BatsmenInningSummary field: " + "Runs")
        }

        try {
            SixUrls = data.getString("SixUrls")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BatsmenInningSummary field: " + "SixUrls")
        }

        try {
            Sixes = data.getString("Sixes")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BatsmenInningSummary field: " + "Sixes")
        }

        try {
            StrikeRate = data.getString("StrikeRate")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BatsmenInningSummary field: " + "StrikeRate")
        }

        try {
            Wicket = data.getString("Wicket")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BatsmenInningSummary field: " + "Wicket")
        }

        try {
            WicketUrls = data.getString("WicketUrls")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BatsmenInningSummary field: " + "WicketUrls")
        }
    }

        fun completeData(matchTitle: String, matchId: String, seriesId: String, slugUrl: String, imageBaseUrl: String, imageExtension: String, videoBaseUrl: String, videoExtension: String) {
        var allUrlsArray = AllUrls.split("||||").toTypedArray()

        for (i in 0..(allUrlsArray.count() - 1)) {
            var contentId = 0
            var urls = allUrlsArray[i]
            var urlsArray = urls.split("||").toTypedArray()

            var videoModel = VideoModel()

            if (urlsArray.size > 2) {
                videoModel.
                setClipData(matchTitle, matchId, seriesId, slugUrl, urlsArray[1], urlsArray[2], imageBaseUrl, imageExtension, videoBaseUrl, videoExtension)
                if (urlsArray[0] != null) {
                    var contentIdvarue = urlsArray[0].toInt()
                    if (contentIdvarue != null) {
                        contentId = contentIdvarue
                    }
                }
            }

            if (contentId > 0) {
                videos.add(videoModel)
            }
        }
    }
}


class BowlerInningSummary {
    var AllUrls: String = ""
    var BowlerId: String = ""
    var Economy: String = ""
    var Maidens: String = ""
    var Name: String = ""
    var NoBallUrls: String = ""
    var NoBalls: String = ""
    var Overs: String = ""
    var Runs: String = ""
    var WicketUrls: String = ""
    var Wickets: String = ""
    var WideUrls: String = ""
    var Wides: String = ""
    var videos: MutableList<VideoModel> = mutableListOf()

    fun setData(data: JSONObject) {
        try {
            AllUrls = data.getString("AllUrls")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BowlerInningSummary field: " + "AllUrls")
        }

        try {
            BowlerId = data.getString("BowlerId")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BowlerInningSummary field: " + "BowlerId")
        }

        try {
            Economy = data.getString("Economy")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BowlerInningSummary field: " + "Economy")
        }

        try {
            Maidens = data.getString("Maidens")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BowlerInningSummary field: " + "Maidens")
        }

        try {
            NoBallUrls = data.getString("NoBallUrls")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BowlerInningSummary field: " + "NoBallUrls")
        }

        try {
            NoBalls = data.getString("NoBalls")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BowlerInningSummary field: " + "NoBalls")
        }

        try {
            Overs = data.getString("Overs")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BowlerInningSummary field: " + "Overs")
        }

        try {
            Name = data.getString("Name")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BatsmenInningSummary field: " + "Name")
        }

        try {
            Runs = data.getString("Runs")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BowlerInningSummary field: " + "Runs")
        }

        try {
            WicketUrls = data.getString("WicketUrls")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BowlerInningSummary field: " + "WicketUrls")
        }

        try {
            Wickets = data.getString("Wickets")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BowlerInningSummary field: " + "Wickets")
        }

        try {
            WideUrls = data.getString("WideUrls")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BowlerInningSummary field: " + "WideUrls")
        }

        try {
            Wides = data.getString("Wides")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BowlerInningSummary field: " + "Wides")
        }

        try {
            WicketUrls = data.getString("WicketUrls")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "BowlerInningSummary field: " + "WicketUrls")
        }
    }

    fun completeData(matchTitle: String, matchId: String, seriesId: String, slugUrl: String, imageBaseUrl: String, imageExtension: String, videoBaseUrl: String, videoExtension: String) {
        var allUrlsArray = AllUrls.split("||||").toTypedArray()

        for (i in 0..(allUrlsArray.count() - 1)) {
            var contentId = 0
            var urls = allUrlsArray[i]
            var urlsArray = urls.split("||").toTypedArray()

            var videoModel = VideoModel()

            if (urlsArray.size > 2) {
                videoModel.setClipData(matchTitle, matchId, seriesId, slugUrl, urlsArray[1], urlsArray[2], imageBaseUrl, imageExtension, videoBaseUrl, videoExtension)
                if (urlsArray[0] != null) {
                    var contentIdvarue = urlsArray[0].toInt()
                    if (contentIdvarue != null) {
                        contentId = contentIdvarue
                    }
                }
            }

            if (contentId > 0) {
                videos.add(videoModel)
            }
        }
    }
}


class Extras {
    var b: String = ""
    var lb: String = ""
    var nb: String = ""
    var total: String = ""
    var wd: String = ""

    var completeString:String = ""

    fun setData(data: JSONObject) {
        try {
            b = data.getString("b")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "Extras field: " + "b")
        }

        try {
            lb = data.getString("lb")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "Extras field: " + "lb")
        }

        try {
            nb = data.getString("nb")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "Extras field: " + "nb")
        }

        try {
            total = data.getString("total")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "Extras field: " + "total")
        }

        try {
            wd = data.getString("wd")
        } catch (e: java.lang.Exception) {
            Log.e("FieldError:", "Extras field: " + "wd")
        }

        completeString = "Extras        " + total + " (nb " + nb + ", wd " + wd + ", b " + b + ", lb " + lb + ")"
    }
}