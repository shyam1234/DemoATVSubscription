package com.willow.android.tv.utils

object GlobalConstants {

    //------cheat code-----------
    const val bypassCountryCheck: Boolean = false
    val bypassVPN = false
    //-----------------

    const val ALLOWED_COUNTRIES = "us, ca"
    enum class ActivityType { DETAILS_PAGE }

    object Keys {
        const val SCORECARD_DATA = "SCORECARD_DATA"
        const val TARGET_URL = "SCORECARD_DATA"
        const val KEY_DIALOG_MODEL = "KEY_DIALOG_MODEL"
        const val SUCCESSFULE_TITLE = "SUCCESSFULE_TITLE"
        const val URL = "URL"
    }

    object ApiConstant {
        const val USER_IS_SUBSCRIBED = 1
        const val staticUrl = "staticUrl"
    }

    object MatchCenterTab {
        const val VIDEOS = "videos"
        const val SCORECARD = "scorecard"
        const val MATCH_INFO = "match_info"
        const val UPCOMING = "upcoming"
        const val FIXTURES = "fixtures"
        const val STANDINGS = "standings"


    }

    const val DEFAULT_SHOW_ADS_FOR_LIVE: Boolean = true
    const val DEFAULT_SHOW_ADS_FOR_VOD: Boolean = true
    const val KEY_ERROR_MGS ="KEY_ERROR_MGS"
    const val KEY_PRODUCT_ID ="KEY_PRODUCT_ID"
    const val KEY_IS_SUBSCRIBE ="KEY_IS_SUBSCRIBE"
    const val PaymentSuccessfullModel ="PaymentSuccessfullModel"


    enum class AdapterViewType {
        SUB_BUTTON,SUB_TEXT,SUB_PLAN_CHANGE;
    }
    enum class ScoreCard {
        SCORECARD_HEADER, SCORECARD_ROW, SCORECARD_EXTRA, SCORECARD_TOTAL,
        MATCHINFO_ROW, MATCHINFO_PARA_DATA
    }

    const val DEFAULT_PAGE: String = "Explore"
    const val DELAY_TO_SHOW_INACTIVE_PLAYER_STATUS_ERROR: Long = 5*1000L
    const val DELAY_IN_AUTO_SCROLL_CAROUSEL_WITH_LIVE_TRAILER: Long = 30*1000
    const val DELAY_IN_AUTO_SCROLL_CAROUSEL_WITH_POSTER = 6*1000L
    const val DELAY_IN_VIDEO_TRAILER_PLAYBACK: Long = 4*1000L
    const val DELAY_IN_RENDERING_POSTER: Long = 800
    const val DELAY_IN_HIDE_PLAYER_CONTROLLER = 5000
    const val DELAY_IN_CARD_EXPAND_ANIM: Long = 1500
    const val NUM_TABS = 2
    const val MATCH_CENTER_NUM_TABS = 3
    //Minimum progress percent for the video to be saved to Continue Watching list
    const val MIN_CONTENT_PROGRESS = 5.0
    //Maximum progress percent for the video to be saved to Continue Watching list
    const val MAX_CONTENT_PROGRESS = 95.0

    // Position of Continue watching in Home List
    const val CONTINUE_WATCHING_POS = 2

    //DevType is the platform information
    const val DEV_TYPE= "androidtv"

    var position = -1
}