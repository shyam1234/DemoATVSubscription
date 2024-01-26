package com.willow.android.tv.data.repositories.scoreCard.datamodel

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonObject

data class ApiMatchInfoDataModel(
    val result: ResultData
)

data class ResultData(
    val detail: Detail,
    val status: String
)

data class Detail(
    val base_url_type: String,
    val content_type: String,
    val cx_eid: Int,
    val display_highlight: Boolean,
    val display_live: Boolean,
    val display_live_link: Boolean,
    val display_replay: Boolean,
    val display_scorecard: Boolean,
    val event_group_id: Int,
    val event_id: Int,
    val free_for_anonymous_user: Boolean,
    val free_live_event: Boolean,
    val game_type: String,
    val gmt_end_date: String,
    val gmt_end_date_ts: Double,
    val gmt_start_date: String,
    val gmt_start_date_ts: Double,
    val highlight_tagged_iptv: Boolean,
    val img_path: String,
    val match_category: String,
    val match_free: Boolean,
    val match_priority: Int,
    val match_result: String,
    val match_slug: String,
    val match_status: String,
    val match_winner: Int,
    val mobile_home_priority: Int,
    val need_login: Int,
    val need_subscription: Int,
    val replay_tagged_iptv: Boolean,
    val score: Any,
    val score_url: String,
    val series_name: String,
    val short_team_one_name: String,
    val short_team_two_name: String,
    val sub_title: String,
    val tabs: List<Tab>,
    val target_action: String,
    val target_type: String,
    val target_url: String,
    val team_id_mapping: TeamIdMapping,
    val team_id_name_mapping: TeamIdNameMapping,
    val team_one_captain: Int,
    val team_one_id: Int,
    val team_one_name: String,
    val team_one_wicket_keeper: Int,
    val team_two_captain: Int,
    val team_two_id: Int,
    val team_two_name: String,
    val team_two_wicket_keeper: Int,
    val thumbnails: ThumbnailsX,
    val title: String,
    val toss_elected_to: String,
    val toss_won_by: Int,
    val tv_everywhere: Boolean,
    val type: String,
    val venue: String
)

data class Tab(
    val base_url_type: String,
    val match_data: MatchData,
    val rows: List<Row>,
    val tab_type: String,
    val target_url: String,
    val title: String
)

data class TeamIdMapping(
    val NZ: Int,
    val SCO: Int
)

data class TeamIdNameMapping(
    val `1651400838`: String,
    val `990864522`: String
)

data class ThumbnailsX(
    val hrb: String,
    val lgb: String,
    val mdb: String
)

data class MatchData(
    val game_type: String,
    val gmt_start_date_ts: Double,
    val match_result: String,
    val match_winner: Int,
    val potm: String,
    val series_name: String,
    val team1_captain: Int,
    val team1_squad: JsonObject,
    val team1_wkt_keeper: Int,
    val team2_captain: Int,
    val team2_squad: JsonObject,
    val team2_wkt_keeper: Int,
    val team_id_map: TeamIdMap,
    val title: String,
    val toss: String,
    val umpires: List<Any>
) {

    private fun getSquadMap(teamSquad: JsonObject): HashMap<String, String>? {
        return Gson().fromJson(teamSquad, HashMap::class.java) as HashMap<String, String>?
    }

    private fun getSquadData(teamMap: Map<String, String>?, capton: Int, wktKeeper: Int): String {
        val list = mutableListOf<String>().apply {
            add(teamMap?.get(capton.toString()).toString() + "(captain)")
            add(teamMap?.get(wktKeeper.toString()).toString() + "(wk)")
            teamMap?.filter {
                !TextUtils.equals(it.key.toString() , capton.toString()) ||
                !TextUtils.equals(it.key.toString() , wktKeeper.toString())
            }?.values?.let {
                addAll(it)
            }
        }
        return list.joinToString(",")
    }

    fun getSquad1Data() = getSquadData(getSquadMap(team1_squad), team1_captain, team1_wkt_keeper)
    fun getSquad2Data() = getSquadData(getSquadMap(team2_squad), team2_captain, team2_wkt_keeper)
}

data class Row(
    val content: List<Content>,
    val content_type: String,
    val title: String
)

data class Team1Squad(
    val `1135676376`: String,
    val `1174441023`: String,
    val `123848241`: String,
    val `1452562089`: String,
    val `1497731420`: String,
    val `1550033790`: String,
    val `1570115256`: String,
    val `1573411244`: String,
    val `1715468413`: String,
    val `407027069`: String,
    val `672168081`: String
)

data class Team2Squad(
    val `11296906`: String,
    val `1230882757`: String,
    val `1252479514`: String,
    val `1317745723`: String,
    val `176172000`: String,
    val `1808908327`: String,
    val `215989321`: String,
    val `21650043`: String,
    val `390752329`: String,
    val `44445326`: String,
    val `668944369`: String
)

data class TeamIdMap(
    val `1651400838`: String,
    val `990864522`: String
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
    val thumbnails: ThumbnailsX,
    val title: String,
    val trailer: String,
    val video_slug: String
)