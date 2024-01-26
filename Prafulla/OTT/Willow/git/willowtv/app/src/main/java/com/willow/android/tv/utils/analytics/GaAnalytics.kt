package com.willow.android.tv.utils.analytics

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.willow.android.WillowApplication
import com.willow.android.WillowApplication.Companion.appContext
import com.willow.android.mobile.services.WiVolleySingleton.Companion.getInstance
import timber.log.Timber
import java.util.Objects

/**
 * Created by abhishek on 14/07/17.
 */
object GaAnalytics {
    private val TAG = GaAnalytics::class.java.simpleName
    private const val baseUrl = "https://www.google-analytics.com/collect"
    private const val version = "1"
    private val deviceId = WillowApplication.instance.getDeviceId()
    private val trackingId = WillowApplication.instance.gaTrackingId()
    private const val event = "event"
    private const val deviceType = "ANDROIDTV"
    private const val highlightCategory = "HIGHLIGHT"
    private const val replayCategory = "REPLAY"
    private const val liveCategory = "LIVE"
    private const val loadCategory = "LOAD"
    private const val liveLabel = "LIVE"
    private const val replayLabel = "REPLAY"
    private const val archiveHighlightLabel = "HIGHLIGHT_ARCHIVE"
    private const val archiveTrendingLabel = "HIGHLIGHT_TRENDING"
    fun sendLoginEvent() {
        sendEvent(loadCategory, deviceType, "LOGIN")
    }

    fun sendLogoutEvent() {
        sendEvent(loadCategory, deviceType, "LOGOUT")
    }

    fun sendLiveLoadEvent() {
        sendEvent(loadCategory, deviceType, "LIVE")
    }

    fun sendArchiveLoadEvent() {
        sendEvent(loadCategory, deviceType, "ARCHIVE")
    }

    fun sendReplayLoadEvent() {
        sendEvent(loadCategory, deviceType, "REPLAY")
    }

    fun sendHighlightTrendingLoadEvent() {
        sendEvent(loadCategory, deviceType, "HIGHLIGHT_TRENDING")
    }

    fun sendHighlightArchiveLoadEvent() {
        sendEvent(loadCategory, deviceType, "HIGHLIGHT_ARCHIVE")
    }

    fun sendReplayPlayEvent(seriesId: String, matchId: String) {
        sendEvent(replayCategory, deviceType, matchId)
        sendMIDEvent(matchId, replayLabel)
        sendSIDEvent(seriesId, replayLabel)
    }

    fun sendLivePlayEvent(seriesId: String, matchId: String) {
        sendEvent(liveCategory, deviceType, matchId)
        sendSIDEvent(seriesId, liveLabel)
        sendMIDEvent(matchId, liveLabel)
    }

    fun sendArchiveHighlightPlayEvent(seriesId: String, matchId: String) {
        val archiveHighlightAction = deviceType + "_ARCHIVE"
        sendEvent(highlightCategory, archiveHighlightAction, matchId)
        sendMIDEvent(matchId, archiveHighlightLabel)
        sendSIDEvent(seriesId, archiveHighlightLabel)
    }

    fun sendTrendingHighlightPlayEvent(seriesId: String, matchId: String) {
        val trendingHighlightAction = deviceType + "_TRENDING"
        sendEvent(highlightCategory, trendingHighlightAction, matchId)
        sendMIDEvent(matchId, archiveTrendingLabel)
        sendSIDEvent(seriesId, archiveTrendingLabel)
    }

    fun sendMIDEvent(mid: String, midLabel: String) {
        sendEvent(mid, deviceType, midLabel)
    }

    fun sendSIDEvent(sid: String, sidLabel: String) {
        sendEvent(sid, deviceType, sidLabel)
    }

    private fun sendEvent(category: String, action: String, label: String) {
        val requestParams = HashMap<String, String>()
        requestParams["v"] = version
        requestParams["tid"] = trackingId
        requestParams["cid"] = deviceId
        requestParams["t"] = event
        requestParams["ec"] = category
        requestParams["ea"] = action
        requestParams["el"] = label
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            baseUrl,
            Response.Listener { },
            Response.ErrorListener { Timber.tag("DataFetchError:").e(baseUrl) }) {
            override fun getParams(): HashMap<String, String>? {
                return requestParams
            }
        }
        Objects.requireNonNull(appContext)?.let { getInstance(it).addToRequestQueue(stringRequest) }
    }

}