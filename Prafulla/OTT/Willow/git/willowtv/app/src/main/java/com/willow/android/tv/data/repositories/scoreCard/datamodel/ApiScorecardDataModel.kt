package com.willow.android.tv.data.repositories.scoreCard.datamodel

import com.willow.android.mobile.models.video.VideoModel

data class ApiScorecardDataModel(
    val result: Result
)

data class Result(
    val BBDisabled: Boolean,
    val HLSSuffix: String,
    val Innings: List<Inning>,
    val IsLive: Boolean,
    val ManOfTheMatch: String,
    val MatchId: String,
    val MatchName: String,
    val MatchResult: String,
    val ScorecardButtonTxt: String,
    val SeriesName: String,
    val ShortMatchName: String,
    val ThumbnailBaseUrl: String,
    val ThumbnailFileExt: String,
    val UseAkamaiCDN: Boolean,
    val VideoBaseUrl: String,
    val VideoFileExt: String,
    val VideoSlugBaseUrl: String,
    val VideoThumbnailBaseUrl: String,
    val YTVideoBaseUrl: String,
    val match_s_name: String,
    val rdtimes: String,
    val scorecardMatchSlug: String,
    val status: String,
    val t1Captain: Any,
    val t1Logo: String,
    val t1Name: String,
    val t1ShortName: String,
    val t1WicketKeeper: Any,
    val t2Captain: Any,
    val t2Logo: String,
    val t2Name: String,
    val t2ShortName: String,
    val t2WicketKeeper: Any,
    val tn1Logo: String,
    val tn2Logo: String
)

data class Inning(
    val BatsmenInningSummaries: List<BatsmenInningSummary>,
    val BattingTeam: String,
    val BattingTeamShortName: String,
    val BowlerInningSummaries: List<BowlerInningSummary>,
    val Extras: Extras,
    val FOWData: List<Any>,
    val InningSummary: String,
    val NotPlayedPlayers: List<String>,
    val Overs: String,
    val RunRate: String,
    val TotalBatsmen: Int,
    val TotalBowlers: Int,
    val TotalRuns: String,
    val VideosDisabled: Boolean,
    val Wickets: String,
    val innName: String,
    val lastBatsman: Any,
    val lastBowler: Any,
    val tnLogo: String
)

data class Extras(
    val b: String,
    val lb: String,
    val nb: String,
    val total: String,
    val wd: String
)

data class BatsmenInningSummary(
    var AllUrls: String = "",
    var Balls: String = "",
    var BatsmanId: String = "",
    var FOWUrls: String = "",
    var FallOfWicket: String = "",
    var FourUrls: String = "",
    var Fours: String = "",
    var Name: String = "",
    var Runs: String = "",
    var SixUrls: String = "",
    var Sixes: String = "",
    var StrikeRate: String = "",
    var Wicket: String = "",
    var WicketUrls: String = "",
    var videos: MutableList<VideoModel> = mutableListOf()
)

data class BowlerInningSummary (
    var AllUrls: String = "",
    var BowlerId: String = "",
    var Economy: String = "",
    var Maidens: String = "",
    var Name: String = "",
    var NoBallUrls: String = "",
    var NoBalls: String = "",
    var Overs: String = "",
    var Runs: String = "",
    var WicketUrls: String = "",
    var Wickets: String = "",
    var WideUrls: String = "",
    var Wides: String = "",
    var videos: MutableList<VideoModel> = mutableListOf()
)

