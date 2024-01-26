package com.willow.android.mobile.configs

import org.json.JSONObject

object WiConfig {
    var isFirstTimeLoad: Boolean = true // Should be true only if Data is set First time

    var pollerEnabled: Boolean = true
    var pollerInterval: Int = 30 // In Seconds (All Intervals)
    var liveScoreUpdateTimeInterval: Int = 30
    var liveScorecardUpdateTimeInterval: Int = 30
    var liveCommentaryUpdateTimeInterval: Int = 30
    var tveAuthCheckInterval: Int = 60
    var homePageRefreshInterval: Int = 300
    var couponCodeEnabled: Boolean = true
    var dfpTag: String = "https://pubads.g.doubleclick.net/gampad/ads?sz=320x480|420x315|400x315|480x480|300x415|480x320|320x240|300x400|640x480&iu=/7176/WillowTV_APP_iOS/WillowTV_APP_iOS_Home/WillowTV_APP_iOS_Home_Video&impl=s&gdfp_req=1&env=vp&ad_rule=1&output=vast&cmsid=[DFP_CMS_ID]&vid=[DFP_CONTENT_ID]&unviewed_position_start=1&url=[referrer_url]&description_url=[description_url]&correlator=[timestamp]"
    var IAPEnabled: Boolean = true
    var IAPRestoreEnabled: Boolean = true
    var US_IAPPrice: String = "$9.99/month"
    var CA_IAPPrice: String = "$7.99/month"
    var US_IAPProductId: String = "willow_premium_subscription_dollar_10"
    var CA_IAPProductId: String = "willow_premium_subscription_dollar_10"
    var US_IAPCurrency: String = "USD"
    var CA_IAPCurrency: String = "CAD"
    var US_IAPSubscriptionType: String = "Monthly"
    var CA_IAPSubscriptionType: String = "Monthly"

    var tveProvidersList: String = "xfinity, dish, spectrum, Fios, Altice One, Optimum"

    // Share and Deeplinking
    var androidMinimumVersion: Int = 6 //For Dynamic Link support
    var deeplinkDomain: String = "https://www.willow.tv/"
    var deeplinkFallbackUrl: String = "https://www.willow.tv/devices"
    var deeplinkDomainUriPrefix: String = "https://willow.page.link"

    var latestAvailableAppVersion: String = "6.0" // Always update in the app before new release. Should be same as the current app version

    fun setCloudData(data: JSONObject) {
        try {
            isFirstTimeLoad = data.getBoolean("isFirstTimeLoad")
        } catch (e: Exception)  {}

        try {
            pollerEnabled = data.getBoolean("pollerEnabled")
        } catch (e: Exception)  {}

        try {
            pollerInterval = data.getInt("pollerInterval")
        } catch (e: Exception)  {}

        try {
            liveScoreUpdateTimeInterval = data.getInt("liveScoreUpdateTimeInterval")
        } catch (e: Exception)  {}

        try {
            liveScorecardUpdateTimeInterval = data.getInt("liveScorecardUpdateTimeInterval")
        } catch (e: Exception)  {}

        try {
            liveCommentaryUpdateTimeInterval = data.getInt("liveCommentaryUpdateTimeInterval")
        } catch (e: Exception)  {}

        try {
            tveAuthCheckInterval = data.getInt("tveAuthCheckInterval")
        } catch (e: Exception)  {}

        try {
            homePageRefreshInterval = data.getInt("homePageRefreshInterval")
        } catch (e: Exception)  {}

        try {
            couponCodeEnabled = data.getBoolean("couponCodeEnabled")
        } catch (e: Exception)  {}

        try {
            dfpTag = data.getString("dfpTag")
        } catch (e: Exception)  {}

        try {
            IAPEnabled = data.getBoolean("IAPEnabled")
        } catch (e: Exception)  {}

        try {
            IAPRestoreEnabled = data.getBoolean("IAPRestoreEnabled")
        } catch (e: Exception)  {}

        try {
            US_IAPPrice = data.getString("US_IAPPrice")
        } catch (e: Exception)  {}

        try {
            CA_IAPPrice = data.getString("CA_IAPPrice")
        } catch (e: Exception)  {}

        try {
            US_IAPProductId = data.getString("US_IAPProductId")
        } catch (e: Exception)  {}

        try {
            CA_IAPProductId = data.getString("CA_IAPProductId")
        } catch (e: Exception)  {}

        try {
            US_IAPCurrency = data.getString("US_IAPCurrency")
        } catch (e: Exception)  {}

        try {
            CA_IAPCurrency = data.getString("CA_IAPCurrency")
        } catch (e: Exception)  {}

        try {
            US_IAPSubscriptionType = data.getString("US_IAPSubscriptionType")
        } catch (e: Exception)  {}

        try {
            CA_IAPSubscriptionType = data.getString("CA_IAPSubscriptionType")
        } catch (e: Exception)  {}

        try {
            tveProvidersList = data.getString("tveProvidersList")
        } catch (e: Exception)  {}

        try {
            androidMinimumVersion = data.getInt("androidMinimumVersion")
        } catch (e: Exception)  {}

        try {
            deeplinkDomain = data.getString("deeplinkDomain")
        } catch (e: Exception)  {}

        try {
            deeplinkFallbackUrl = data.getString("deeplinkFallbackUrl")
        } catch (e: Exception)  {}

        try {
            deeplinkDomainUriPrefix = data.getString("deeplinkDomainUriPrefix")
        } catch (e: Exception)  {}

        try {
            latestAvailableAppVersion = data.getString("latestAvailableAppVersion")
        } catch (e: Exception)  {}
    }
}