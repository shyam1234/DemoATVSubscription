package com.willow.android.mobile.services.analytics

import com.willow.android.WillowApplication
import com.willow.android.mobile.configs.Keys
import com.willow.android.mobile.models.auth.UserModel

object WiAnalyticsService {
    val deviceType = Keys.devType
    val replayCategory = "REPLAY"
    val highlightsCategory = "HIGHLIGHTS"
    val liveCategory = "LIVE"
    val eventName = "PLAY"

    fun playReplay(videoTitle: String, matchId: String) {
        val paramString = "json_data={\"DeviceType\":\"" + deviceType + "\",\"Category\":\"" + replayCategory + "\",\"EventName\":\"" + eventName + "\",\"VideoTitle\":\"" + videoTitle + "\",\"MatchId\":\"" + matchId + "\",\"UserId\":" + UserModel.userId + "}"
        WillowApplication.sendWiAnalyticsRequest(paramString)
    }

    fun playLive(videoTitle: String, matchId: String) {
        val paramString = "json_data={\"DeviceType\":\"" + deviceType + "\",\"Category\":\"" + liveCategory + "\",\"EventName\":\"" + eventName + "\",\"VideoTitle\":\"" + videoTitle + "\",\"MatchId\":\"" + matchId + "\",\"UserId\":" + UserModel.userId + "}"
        WillowApplication.sendWiAnalyticsRequest(paramString)
    }

    fun playHighlight(videoTitle: String, matchId: String, contenId: String) {
        val paramString = "json_data={\"DeviceType\":\"" + deviceType + "\",\"Category\":\"" + highlightsCategory + "\",\"EventName\":\"" + eventName + "\",\"VideoTitle\":\"" + videoTitle + "\",\"MatchId\":\"" + matchId + "\",\"YTRecordId\":\"" + contenId + "\",\"UserId\":" + UserModel.userId + "}"
        WillowApplication.sendWiAnalyticsRequest(paramString)
    }
}