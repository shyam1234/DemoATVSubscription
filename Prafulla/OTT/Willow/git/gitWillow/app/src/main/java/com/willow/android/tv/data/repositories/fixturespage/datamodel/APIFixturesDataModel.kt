package com.willow.android.tv.data.repositories.fixturespage.datamodel

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.willow.android.tv.utils.CommonFunctions
import com.willow.android.tv.utils.config.GlobalTVConfig
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

data class APIFixturesDataModel(
    val result: Result
)

data class Result(
    val fixtures_by_date: List<FixturesByDate>,
    val fixtures_by_series: List<FixturesBySeries>,
    val status: String
)

data class FixturesByDate(
    val match_info_base_url_type: String,
    val watch_live_base_url_type: String,
    val content_type: String,
    val content_details:   List<ContentDetail>?=null,
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
    val gmt_end_date: String,
    val gmt_start_date: String,
    val gmt_start_date_ts: Long,
    val gmt_end_date_ts: Long,
    val highlight_tagged_iptv: Boolean,
    val img_path: String,
    val match_category: String,
    val match_free: Boolean,
    val match_priority: Int,
    val match_slug: String,
    val mobile_home_priority: Int,
    val need_login: Int,
    val need_subscription: Int,
    val pst_start_date: String,
    val replay_tagged_iptv: Boolean,
    @SerializedName("score")
    @Expose
    val score: Map<String, List<String>>?,
    val score_url: String,
    val series_name: String,
    val short_team_one_name: String,
    val short_team_two_name: String,
    val sub_title: String,
    val target_action: String,
    val target_type: String,
    val watch_live_target_url: String,
    val match_info_target_url: String,
    @SerializedName("team_id_mapping")
    @Expose
    val team_id_mapping: Map<String, Int>?,
    val team_one_name: String,
    val team_two_name: String,
    val thumbnails: Thumbnails,
    val title: String,
    val tv_everywhere: Boolean,
    val type: String,
    val venue: String,
    val yr_month: String,
    val yr_month_formatted: String,
    val date_formatted: String?
){

    fun getLocalTimeFromGmtTs():String{
//        return pst_start_date
        return CommonFunctions.convertToLocaleDateTime(gmt_start_date_ts)
    }

    fun getMonthFromGmtTs():String{
        return CommonFunctions.convertToMonthFromGmtTs(gmt_start_date_ts)
    }

    fun getLocalTimeWithDayFromGmtTs():String{
        return CommonFunctions.convertToLocaleDateTimeWithDay(gmt_start_date_ts)
    }

    fun getLocalTimeWithZoneGmtTs():String{
        return CommonFunctions.convertToLocaleTime(gmt_start_date_ts)
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

    fun getFormattedDate(): String{
        return "date_formatted"
    }

    fun getDateInFormat(): Date {
        return try {
            val date =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.ENGLISH).parse(gmt_start_date)
            date
        } catch (e: Exception) {
            Date()
        }
    }

    fun getThumbnail(): String {
        return GlobalTVConfig.getImageBaseUrl()+img_path+thumbnails.mdb
    }

    fun getExpandedThumbnail(): String {
        //remove this once data payload available for new attribute 'thumbnailExpandableURL'
        return "https://cdn.wallpapersafari.com/24/50/26RaKV.jpg"
    }


    fun isSubscriptionRequired(): Boolean{
        return need_subscription == 1
    }
    fun getTargetUrlMatchInfo(): String{
        return GlobalTVConfig.getStreamingTargetURL(match_info_base_url_type.toString(),match_info_target_url.toString())
    }

    fun getTargetUrlWatchLive(): String{
        return GlobalTVConfig.getStreamingTargetURL(watch_live_base_url_type.toString(),watch_live_target_url.toString())
    }
}


data class Thumbnails(
    val mdb: String
)

data class FixturesBySeries(
    val base_url_type: String,
    val content_type: String,
    val display_live_link: Boolean,
    val display_score_card: Boolean,
    val enddate: String,
    val event_group_id: Int,
    val gmt_series_end_date: String,
    val gmt_series_start_date: String,
    val img_path: String,
    val is_match_live: Boolean,
    val series_country_code: List<String>,
    val series_cx_eid: Int,
    val series_domain_list: SeriesDomainList,
    val series_highlights_tagged: Int,
    val series_is_tv_only: Int,
    val series_name: String,
    val series_replays_tagged: Int,
    val series_slug: String,
    val seriesactive: Int,
    val seriesdesciption: String,
    val seriesshouldrenderinarchivetab: Int,
    val seriesshouldrenderinupcomingtab: Int,
    val startdate: String,
    val target_action: String,
    val target_type: String,
    val target_url: String,
    val thumbnails: ThumbnailsSeries,
    val tv_everywhere: Boolean
){
    fun getThumbnail(): String {
        return GlobalTVConfig.getImageBaseUrl()+img_path+thumbnails.ptrMdb
    }
    fun getThumbnailLarge(): String {
        return GlobalTVConfig.getImageBaseUrl()+img_path+thumbnails.ptrLgb
    }

    fun geTargetURL() = GlobalTVConfig.getStreamingTargetURL(base_url_type,target_url)
}

class SeriesDomainList

data class ThumbnailsSeries(
    val hrb: String,
    val lgb: String,
    val mdb: String,
    @SerializedName("ptr-lgb")
    val ptrLgb: String,
    @SerializedName("ptr-mdb")
    val ptrMdb: String,
    @SerializedName("ptr-smb")
    val ptrSmb: String
)
@Parcelize
data class ContentDetail(
    val content_id: Int?,
    val name: String?,
    val priority: Int?,
    val streaming_url: String?
) : Parcelable {}