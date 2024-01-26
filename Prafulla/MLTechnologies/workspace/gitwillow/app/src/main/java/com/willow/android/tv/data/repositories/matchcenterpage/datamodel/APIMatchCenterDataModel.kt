package com.willow.android.tv.data.repositories.matchcenterpage.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class APIMatchCenterDataModel(
    val result: Result
)
data class Result(
    val detail: Detail,
    val status: String
)

data class Detail(
    val base_url_type: String,
    val content_rows: List<ContentRow>?,
    val content_type: String,
    val cx_eid: Int,
    val display_highlight: Any,
    val display_live: Any,
    val display_live_link: Boolean,
    val display_replay: Any,
    val display_scorecard: Any,
    val event_group_id: Int,
    val event_id: Int,
    val free_for_anonymous_user: Boolean,
    val free_live_event: Boolean,
    val gmt_end_date: String,
    val gmt_start_date: String,
    val highlight_tagged_iptv: Any,
    val img_path: String,
    val match_category: String,
    val match_free: Any,
    val match_priority: Int,
    val match_slug: String,
    val mobile_home_priority: Int,
    val need_login: Int,
    val need_subscription: Int,
    val replay_tagged_iptv: Any,
    val score: Map<String, List<String>>?,
    val score_url: String,
    val series_name: String,
    val short_team_one_name: String,
    val short_team_two_name: String,
    val sub_title: String,
    val target_action: String,
    val target_type: String,
    val target_url: String,
    @SerializedName("team_id_mapping")
    @Expose
    val team_id_mapping: Map<String, Int>?,
    val team_one_name: String,
    val team_two_name: String,
    val thumbnails: Thumbnails,
    val title: String,
    val type: String,
    val venue: String
){
    fun getDateFormatted():String{
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        val date = inputFormat.parse(gmt_start_date)
        return date?.let { outputFormat.format(it) }.toString()
    }

    fun getScoreOne(): String{

        val scoreList = ArrayList<String>()
        score?.forEach {
            scoreList.add(it.value.toString().replace("[\\[\\]]".toRegex(),""))
        }
        return if (scoreList.size>0) scoreList[0] else ""
    }

    fun getScoreTwo(): String{

        val scoreList = ArrayList<String>()
        score?.forEach {
            scoreList.add(it.value.toString().replace("[\\[\\]]".toRegex(),""))
        }
        return if (scoreList.size>1) scoreList[1] else ""
    }
}


data class ContentRow(
    val match_content: MatchContent
)

data class MatchContent(
    val content: List<Content>,
    val content_type: String
)
data class Content(
    val base_url_type: String,
    val cc: List<String>,
    val content_id: Int,
    val content_type: String,
    val created_date: String,
    val display_live_link: Boolean,
    val display_score_card: Boolean,
    val duration: String,
    val duration_seconds: Int,
    val event_id: Int,
    val img_path: String,
    val is_match_live: Boolean,
    val meta_data: String,
    val modified_date: String,
    val need_login: Int,
    val need_subscription: Int,
    val play_trailer: Boolean,
    val series_name: String,
    val sub_title: String,
    val target_action: String,
    val target_type: String,
    val target_url: String,
    val thumbnail: String,
    val thumbnails: ThumbnailsContent,
    val title: String,
    val trailer: String,
    val video_slug: String
)
data class Score(
    val `1973981191`: List<String>,
    val `582599877`: List<String>
)

data class TeamIdMapping(
    val PRS: Int,
    val SYS: Int
)

data class Thumbnails(
    val hrb: String,
    val lgb: String,
    val mdb: String
)


data class ThumbnailsContent(
    val hrb: String,
    val lgb: String,
    val mdb: String
)