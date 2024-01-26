package com.willow.android.tv.data.repositories.matchcenterpage.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.willow.android.tv.data.repositories.commondatamodel.ContentDetail
import com.willow.android.tv.utils.CommonFunctions
import com.willow.android.tv.utils.config.GlobalTVConfig
import java.text.SimpleDateFormat
import java.util.*

data class APINewMatchCenterDataModel(
    var result: ResultNew
)

data class ResultNew(
    var detail: DetailNew,
    var status: String
) {

}
data class DetailNew(
    var just_show_start_date:Boolean,
    var title: String? = null,
    var base_url_type: String,
    var content_type: String,
    var display_live_link: Boolean,
    var display_score_card: Boolean,
    var enddate: String,
    var event_group_id: Int,
    var event_group_slug: String,
    var gmt_series_end_date: String?,
    var gmt_series_end_date_ts: Long?,
    var gmt_series_start_date: String?,
    var gmt_series_start_date_ts: Long?,
    var gmt_end_date: String?,
    var gmt_end_date_ts: Long?,
    var gmt_start_date: String?,
    var gmt_start_date_ts: Long?,
    var img_path: String,
    var is_match_live: Boolean,
    var series_country_code: List<String>,
    var series_cx_eid: Int,
    var series_domain_list: SeriesDomainList,
    var series_highlights_tagged: Int,
    var series_is_tv_only: Int,
    var series_name: String,
    var series_replays_tagged: Int,
    var series_slug: String,
    var seriesactive: Int,
    var seriesdesciption: String,
    var seriesshouldrenderinarchivetab: Int,
    var seriesshouldrenderinupcomingtab: Int,
    var startdate: String,
    var tabs: List<Tab>,
    var target_action: String,
    var target_type: String,
    var target_url: String,
    var thumbnails: ThumbnailsNew,
    var tv_everywhere: Boolean,
    val need_login: Int,
    val need_subscription: Int,
) {


    /**
     *  04 May -  13 May 2023
     */
    fun getDateFormatted():String{
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat1 = SimpleDateFormat("dd MMM", Locale.getDefault())
        val outputFormat2 = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        val date1: Date
        val date2: Date
        if( gmt_series_start_date!=null && gmt_series_end_date!=null){
            date1 = inputFormat.parse(gmt_series_start_date)
            date2 = inputFormat.parse(gmt_series_end_date)
        }else{
            date1 = inputFormat.parse(gmt_start_date)
            date2 = inputFormat.parse(gmt_end_date)
        }

        val returnDate = date1?.let { outputFormat1.format(it) }.toString() + " - " +date2?.let { outputFormat2.format(it) }.toString()

        return returnDate
    }

    /**
     * May 04 - May 13, 2023
     */
    fun formatMMMDDStartAndEndTime(justShowStartDate: Boolean):String{
        return if( gmt_series_start_date_ts != null && gmt_series_end_date_ts != null){
            CommonFunctions.formatMMMDDStartAndEndTime(gmt_series_start_date_ts!!, gmt_series_end_date_ts!!,justShowStartDate)
        }else  if( gmt_start_date_ts != null && gmt_end_date_ts != null){
            CommonFunctions.formatMMMDDStartAndEndTime(gmt_start_date_ts!!, gmt_end_date_ts!!,justShowStartDate)
        }else {
            ""
        }
    }

}
class SeriesDomainList

data class Tab(
    var base_url_type: String?,
    var items: List<Item>?,
    var rows: List<Row>?,
    var target_url: String?,
    var title: String,
    var tab_type: String?
) {

    //If the target url is not coming from api we just return null instead of baseurl appended targte url
    fun getUrl():String? {
        if(target_url == null){
            return null
        }else{
            return  GlobalTVConfig.getStreamingTargetURL(base_url_type, target_url)
        }

    }

}

data class ItemNew(
    var base_url_type: String,
    var cc: List<String>,
    var content_type: String,
    var content_details:    List<ContentDetail>?=null,
    var cx_eid: Int,
    val content_id: Int,
    var display_highlight: Boolean,
    var display_live: Boolean,
    var display_live_link: Boolean,
    var display_replay: Boolean,
    var display_scorecard: Boolean,
    var event_group_id: Int,
    var event_id: Int,
    var free_for_anonymous_user: Boolean,
    var free_live_event: Boolean,
    var gmt_end_date: String,
    var gmt_end_date_ts: Int,
    var gmt_start_date: String,
    var gmt_start_date_ts: Int,
    var ground_details: String,
    var highlight_tagged_iptv: Boolean,
    var img_path: String,
    var match_category: String,
    var match_free: Boolean,
    var match_priority: Int,
    var match_slug: String,
    var mobile_home_priority: Int,
    var need_login: Int,
    var need_subscription: Int,
    var pst_en_date: String,
    var pst_st_date: String,
    var pst_start_date: String,
    var replay_tagged_iptv: Boolean,
    @SerializedName("score")
    @Expose
    val score: Map<String, List<String>>?,
    var score_url: String,
    var short_team_one_name: String,
    var short_team_two_name: String,
    var sub_title: String,
    var tags: List<String>,
    var target_action: String,
    var target_type: String,
    var target_url: String,
    @SerializedName("team1_player_ids")
    @Expose
    val team1_player_ids: Map<Int, String>?,
    @SerializedName("team2_player_ids")
    @Expose
    val team2_player_ids: Map<Int, String>?,
    @SerializedName("team_id_mapping")
    @Expose
    val team_id_mapping: Map<String, Int>?,
    var team_one_name: String,
    var team_two_name: String,
    var thumbnail: Any,
    var thumbnails: ThumbnailsNew,
    var title: String,
    var tv_everywhere: Boolean,
    var type: String,
    var venue: String,
    var yr_month: String,
    var yr_month_formatted: String,
    var duration: String,
    var duration_seconds: Int
) {

}

data class ContentNew(
    val base_url_type: String,
    val cc: List<String>,
    var content_details:    List<ContentDetail>?=null,
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
data class Row(
    var card_type: String?,
    var items: List<ItemNew>?,
    var content: List<ContentNew>?,
    var items_category: String?,
    var content_type: String?,
    var sub_title: String,
    var title: String
) {

}
data class Item(
    var base_url_type: String,
    var cc: List<String>,
    var content_type: String,

    var cx_eid: Int,
    var display_highlight: Boolean,
    var display_live: Boolean,
    var display_live_link: Boolean,
    var display_replay: Boolean,
    var display_scorecard: Boolean,
    var event_group_id: Int,
    var event_id: Int,
    var free_for_anonymous_user: Boolean,
    var free_live_event: Boolean,
    var gmt_end_date: String,
    var gmt_end_date_ts: Int,
    var gmt_start_date: String,
    var gmt_start_date_ts: Int,
    var ground_details: String,
    var highlight_tagged_iptv: Boolean,
    var img_path: String,
    var match_category: String,
    var match_free: Boolean,
    var match_priority: Int,
    var match_slug: String,
    var mobile_home_priority: Int,
    var need_login: Int,
    var need_subscription: Int,
    var pst_en_date: String,
    var pst_st_date: String,
    var pst_start_date: String,
    var replay_tagged_iptv: Boolean,
    @SerializedName("score")
    @Expose
    val score: Map<String, List<String>>?,
    var score_url: String,
    var short_team_one_name: String,
    var short_team_two_name: String,
    var sub_title: String,
    var tags: List<String>,
    var target_action: String,
    var target_type: String,
    var target_url: String,
    @SerializedName("team1_player_ids")
    @Expose
    val team1_player_ids: Map<Int, String>?,
    @SerializedName("team2_player_ids")
    @Expose
    val team2_player_ids: Map<Int, String>?,
    @SerializedName("team_id_mapping")
    @Expose
    val team_id_mapping: Map<String, Int>?,
    var team_one_name: String,
    var team_two_name: String,
    var thumbnail: String,
    var thumbnails: ThumbnailsNew,
    var title: String,
    var tv_everywhere: Boolean,
    var type: String,
    var venue: String,
    var yr_month: String,
    var yr_month_formatted: String
) {

    fun getLocalTimeFromGmtTs():String{
//        return pst_start_date
        return CommonFunctions.convertToLocaleDateTime(gmt_start_date_ts.toLong())
    }
    fun getMonthFromGmtTs():String{
        return CommonFunctions.convertToMonthFromGmtTs(gmt_start_date_ts.toLong())
    }

    fun getLocalTimeWithDayFromGmtTs():String{
        return CommonFunctions.convertToLocaleDateTimeWithDay(gmt_start_date_ts.toLong())
    }

    fun getLocalTimeWithZoneGmtTs():String{
        return CommonFunctions.convertToLocaleTime(gmt_start_date_ts.toLong())
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
    fun getThumbnailOfImage(): String {
        return GlobalTVConfig.getImageBaseUrl()+img_path+thumbnails.lgb
    }
    fun getTargetUrlMatchInfo(): String{
        return GlobalTVConfig.getStreamingTargetURL(base_url_type.toString(),target_url.toString())
    }

}


data class ThumbnailsNew(
    var hrb: String,
    var lgb: String,
    var mdb: String,
    @SerializedName("ptr-lgb")
    @Expose
    val ptr_lgb: String?,
    @SerializedName("ptr-mdb")
    @Expose
    var ptr_mdb: String?,
    @SerializedName("ptr-smb")
    @Expose
    var ptr_smb: String?
)