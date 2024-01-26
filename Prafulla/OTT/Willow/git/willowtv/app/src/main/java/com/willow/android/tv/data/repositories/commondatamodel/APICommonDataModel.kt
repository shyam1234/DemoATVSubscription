package com.willow.android.tv.data.repositories.commondatamodel

import android.os.Build
import android.os.Parcelable
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.google.gson.annotations.SerializedName
import com.willow.android.WillowApplication
import com.willow.android.tv.common.Types
import com.willow.android.tv.utils.CommonFunctions
import com.willow.android.tv.utils.GlobalConstants
import com.willow.android.tv.utils.PrefRepository
import com.willow.android.tv.utils.config.GlobalTVConfig
import kotlinx.parcelize.Parcelize
import timber.log.Timber

/**
* This is the common DataModel we use inside Explore and Videos Package APIs.
**/

@Parcelize
data class CommonCardRow(
    val result: Result
) : Parcelable {}

@Parcelize
data class ContentDetail(
    val content_id: Int,
    val name: String,
    val priority: Int,
    val streaming_url: String
) : Parcelable {}

@Parcelize
data class Card(
    val action: String?="",
    val base_url_type: String?="",
    val button_title: String?="",
    val cc: List<String>?=null,
    val content_details: List<ContentDetail>?=null,
    var content_id: Int?=0,
    val content_type: String?="",
    val created_date: String?="",
    val cx_eid: Int?=0,
    val description: String?="",
    val display_live_link: Boolean = false,
    val display_score_card: Boolean?=false,
    val duration: String?="",
    val duration_seconds: Int =0,
    val enable_button: Boolean?=false,
    val enable_trailer: Boolean?=false,
    val enabled: Boolean?=false,
    val event_gmt_end_date: String?="",
    val event_gmt_start_date: String?="",
    val event_group_id: Int?=0,
    val tv_everywhere :Boolean? = false,
    val event_id: Int?=0,
    val ground_details: String?="",
    val img_path: String?="",
    val is_match_live: String?="",
    val league_name: String?="",
    val match_type: String?="",
    val meta_data: String?="",
    val modified_date: String?="",
    val need_login: Int?=0,
    val need_subscription: Int?=0,
    val play_trailer: Boolean?=false,
    var priority: Int?=0,
    val promo_id: Int?=0,
    val score_url: String?="",
    val series_end_date: String?="",
    val series_id: Int?=0,
    val series_name: String?="",
    val series_start_date: String?="",
    val short_name: String?="",
    val short_team_one_name: String?="",
    val short_team_two_name: String?="",
    val sub_title: String?="",
    val tags: List<String>?=null,
    val target_action: String?="",
    val target_type: String?="",
    val target_url: String?="",
    val team_id: Int?=0,
    val team_name: String?="",
    val team_one_name: String?="",
    val team_two_name: String?="",
    private val thumbnail: String?="",
    val thumbnails: Thumbnails?=null,
    val time: String?="",
    val title: String?="",
    val trailer: String?="",
    val video_slug: String?="",
    //this will be available for Live and upcoming NOT for VOD, highlights
    val gmt_start_date_ts:Long?=null,
    val gmt_end_date_ts:Long?= null,
    @SerializedName("score")
    val score: Map<String, List<String>>?= null,
    @SerializedName("team_id_mapping")
    val team_id_mapping: Map<String, Int>?= null,
    //additional
    var isExpandable : Boolean = false,
    var progress : Double?=0.0,
    var index: Int = 0,
    var isPoster :Boolean = false
) : Parcelable {


    //high to low order ( hrb> lgb > mdb ) resolution
    fun getThumbnailHRB(): String {
        return GlobalTVConfig.getImageBaseUrl() +img_path +thumbnails?.lgb
    }

    fun getThumbnailMDB(): String {
        return GlobalTVConfig.getImageBaseUrl() +img_path +thumbnails?.mdb
    }

    //For carousel, thumbnail will have complete url path
    fun getCarouselBG(): String? {
        return thumbnail
    }
    //For poster, form the url and it should be high resolution
    fun getPosterHRB(): String {
        return GlobalTVConfig.getImageBaseUrl() +img_path +thumbnails?.hrb
    }
    //high to low order ( ptrlgb> ptrmdb > ptrsmb ) resolution
    fun getThumbnailPortrait(): String {
        return GlobalTVConfig.getImageBaseUrl() +img_path +thumbnails?.ptrsmb
    }

    /**
     * use to show Live tag
     */
    fun isShowLiveTag():Boolean{
        return  display_live_link
    }

    /**
     * use to show Prime tag
     */
    fun isShowPrimeTag(): Boolean{
        Timber.d("isShowPrimeTag $title >>>  need_subscription: $need_subscription " + ">> digitalCust: ${isDigitalCustomerSubscribed()} " + ">> TVCust: ${isTVCustomer()} " + ">> isLive: ${isShowLiveTag()} " + ">> duration_seconds: $duration_seconds ")
        Timber.d("isShowPrimeTag ----------------------------------------- ")
        //business logic:Show prime tag iff user nighter digital nor tv and content duration is less than 5 min.
        return (need_subscription == 1 && !isDigitalCustomerSubscribed() && !isTVCustomer() && (isShowLiveTag() || duration_seconds >= 300))
    }

    /**
     * use to check subscription required or not
     */
    fun isSubscriptionRequired(): Boolean{
        //business case: user can watch content which is less than 300 sec (5 min) without subscription
        return isShowPrimeTag()
    }


    /**
     * use to check login required or not
     */
    fun isLoginRequired(): Boolean{
        Timber.d("isShowPrimeTag $title >>> isLoginReq: ${need_login == 1 || isSubscriptionRequired()} ")

        return  need_login == 1 || isSubscriptionRequired()
    }

    fun isDigitalCustomerFree(): Boolean {
        return  PrefRepository(WillowApplication.instance).getCustomerType().contains(GlobalTVConfig.DIGITAL_CUSTOMER_FREE, true)
    }
    fun isDigitalCustomer(): Boolean{
        return  PrefRepository(WillowApplication.instance).getLoggedIn()?:false
    }
    fun isDigitalCustomerSubscribed(): Boolean {
        return  PrefRepository(WillowApplication.instance).getCustomerType().contains(GlobalTVConfig.DIGITAL_CUSTOMER_SUBSCRIBED, true)
    }

    fun isTVCustomer(): Boolean {
        return  PrefRepository(WillowApplication.instance).getCustomerType().contains(GlobalTVConfig.TV_CUSTOMER, true)
    }

    /**
     * 1. check valid country
     * 2. check (in case of DigitalCustomerSubscribed, tv_everywhere item should item should not show )
     * 3. for Guest/DigitalCustomerFree/TVUser, tv_everywhere item should come.
     */
    fun isItemWhitelist():Boolean{
        return (GlobalConstants.bypassCountryCheck ||
                (((cc == null) || (cc.contains(GlobalTVConfig.country.uppercase()))) &&
                        (!isDigitalCustomerSubscribed() || (tv_everywhere != true))))
    }

    /**
     * remove non live content from the Live row
     * Leela told to hold on this. it can be handle from server
     */
    fun isValidLiveContent():Boolean{
        if(content_type?.contains( Types.Content.LIVE.name, true) == true){
            return display_live_link
        }
        return true
    }

    fun getTargetUrl(): String{

       return GlobalTVConfig.getStreamingTargetURL(base_url_type.toString(),target_url.toString())
    }

    fun getSeriesStartLocalTimeInMonthDate():String?{
        return gmt_start_date_ts?.let { CommonFunctions.convertToLocaleMonthDate(it) }
    }
    fun getSeriesEndLocalTimeInMonthDateYear(): String? {
        return gmt_end_date_ts?.let { CommonFunctions.convertToLocaleMonthDateYear(it) }
    }

    fun getSeriesStartAndEndTime(): String? {
        return gmt_start_date_ts?.let { start ->
            gmt_end_date_ts?.let { end ->
                CommonFunctions.formatMMMDDStartAndEndTime(start, end)
            }
        }
    }


    fun getTeamsWithTheirScores(): Spanned {
        val str = StringBuilder()
        var count = 0
        team_id_mapping?.let { teamIdMapping ->
            for ((teamName, teamId) in teamIdMapping) {
                val teamScore = score?.get(teamId.toString())
                if (!teamScore.isNullOrEmpty()) {
                    str.append(if (count == 0) "<b>$teamName ${teamScore.joinToString(separator = " | ", prefix = "", postfix = "")}</b>" else "<br>vs<br><b>$teamName ${teamScore.joinToString(separator = " | ", prefix = "", postfix = "")}</b>" )
                } else {
                    str.append(if (count == 0) "<b>${teamName}</b>" else "<br>vs<br><b>$teamName</b>" )
                }
                count++
            }
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            HtmlCompat.fromHtml(str.toString(), HtmlCompat.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(str.toString())
        }
    }

}

@Parcelize
data class Result(
    val rows: ArrayList<CardRow>,
    val status: String
) : Parcelable {}

@Parcelize
data class CardRow(
    val card_type: String,
    var items: ArrayList<Card>?,
    val items_category: String,
    val sub_title: String,
    val title: String
) : Parcelable {
    fun useShadow(): Boolean {
        return true
    }

    fun getCardType(): Types.Card? {
        return try {
            Types.Card.valueOf(card_type.uppercase())
        } catch (ex: Exception) {
            Timber.d("getCardType ${ex.message}")
            null
        }
    }

    fun getItemCategory(): Types.CardRowCategory? {
        return try {
            items_category.let { Types.CardRowCategory.valueOf(it.uppercase()) }
        } catch (ex: Exception) {
            null
        }
    }

    fun getListOfCards(): ArrayList<Card>?{
        return items
    }
}

@Parcelize
data class Thumbnails(
    val hrb: String? = null,
    val lgb: String? = null,
    val mdb: String? = null,
    @SerializedName("ptr-lgb")
    val ptrlgb : String?=null,
    @SerializedName("ptr-mdb")
    val ptrmdb : String?=null,
    @SerializedName("ptr-smb")
    val ptrsmb : String?= null

) : Parcelable {}