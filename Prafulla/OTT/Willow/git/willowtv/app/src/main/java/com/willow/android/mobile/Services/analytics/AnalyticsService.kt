package com.willow.android.mobile.services.analytics

import android.content.Context
import com.clevertap.android.sdk.CleverTapAPI
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.willow.android.mobile.models.auth.UserModel

import com.willow.android.mobile.models.video.VideoModel

object AnalyticsService {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    var cleverTapDefaultInstance: CleverTapAPI? = null

    // Should be called from Main Activity
    fun initAnalyticsService(context: Context) {
        FirebaseApp.initializeApp(context)
        firebaseAnalytics = Firebase.analytics

        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(context)
    }

    fun sendUserLoginEvent() {
        firebaseAnalytics.setUserId(UserModel.getEncryptedUserId())
        firebaseAnalytics.setUserProperty(UserModel.getEncryptedUserId(), "id")
        firebaseAnalytics.logEvent("AN_LOGIN") {
            param("user_id", UserModel.getEncryptedUserId())
            param("is_tve", UserModel.isTVEUser.toString())
            param("is_subscribed", UserModel.isSubscribed.toString())
        }

        val userProfile = HashMap<String, Any>()
        userProfile["user_id"] = UserModel.getEncryptedUserId()
        userProfile["is_tve"] = UserModel.isTVEUser.toString()
        userProfile["is_subscribed"] = UserModel.isSubscribed.toString()
        cleverTapDefaultInstance?.onUserLogin(userProfile)
        cleverTapDefaultInstance?.pushEvent("AN_LOGIN")
    }

    fun sendUserSignupEvent() {
        firebaseAnalytics.setUserId(UserModel.getEncryptedUserId())
        firebaseAnalytics.setUserProperty(UserModel.getEncryptedUserId(), "id")
        firebaseAnalytics.logEvent("AN_SIGNUP") {
            param("user_id", UserModel.getEncryptedUserId())
            param("is_tve", UserModel.isTVEUser.toString())
            param("is_subscribed", UserModel.isSubscribed.toString())
        }

        val userProfile = HashMap<String, Any>()
        userProfile["user_id"] = UserModel.getEncryptedUserId()
        userProfile["is_tve"] = UserModel.isTVEUser.toString()
        userProfile["is_subscribed"] = UserModel.isSubscribed.toString()
        cleverTapDefaultInstance?.onUserLogin(userProfile)
        cleverTapDefaultInstance?.pushEvent("AN_SIGNUP")
    }

    fun sendUserLogoutEvent() {
        firebaseAnalytics.setUserId(UserModel.getEncryptedUserId())
        firebaseAnalytics.setUserProperty(UserModel.getEncryptedUserId(), "id")
        firebaseAnalytics.logEvent("AN_LOGOUT") {
            param("user_id", UserModel.getEncryptedUserId())
            param("is_tve", UserModel.isTVEUser.toString())
            param("is_subscribed", UserModel.isSubscribed.toString())
        }

        val parameters = mapOf(
            "user_id" to UserModel.getEncryptedUserId(),
            "is_tve" to UserModel.isTVEUser.toString(),
            "is_subscribed" to UserModel.isSubscribed.toString(),
        )
        cleverTapDefaultInstance?.pushEvent("AN_LOGOUT", parameters)
    }

    fun sendPlayEvent(screenType: String, videoModel: VideoModel) {
        firebaseAnalytics.logEvent("AN_PLAY") {
            param("screen_type", screenType)
            param("content_type", videoModel.contentType)
            param("series_id", videoModel.seriesId)
            param("match_id", videoModel.matchId)
            param("content_id", videoModel.contentId)
            param("slug_url", videoModel.slugUrl)
        }

        if (videoModel.contentType.lowercase().equals("live", true)) {
            WiAnalyticsService.playLive(videoModel.contentId, videoModel.matchId)
        } else if (videoModel.contentType.lowercase().equals("replay", true)) {
            WiAnalyticsService.playReplay(videoModel.contentId, videoModel.matchId)
        } else if (videoModel.contentType.lowercase().equals("highlight", true)) {
            WiAnalyticsService.playHighlight(
                videoModel.contentId,
                videoModel.matchId,
                videoModel.contentId
            )
        }


        val parameters = mapOf(
            "screen_type" to screenType,
            "content_type" to videoModel.contentType,
            "series_id" to videoModel.seriesId,
            "match_id" to videoModel.matchId,
            "content_id" to videoModel.contentId,
        )
        cleverTapDefaultInstance?.pushEvent("AN_PLAY", parameters)
    }

    fun sendPlayerStopEvent(screenType: String, videoModel: VideoModel) {
        firebaseAnalytics.logEvent("AN_PLAYER_STOP") {
            param("screen_type", screenType)
            param("content_type", videoModel.contentType)
            param("series_id", videoModel.seriesId)
            param("match_id", videoModel.matchId)
            param("content_id", videoModel.contentId)
            param("slug_url", videoModel.slugUrl)
        }

        val parameters = mapOf(
            "screen_type" to screenType,
            "content_type" to videoModel.contentType,
            "series_id" to videoModel.seriesId,
            "match_id" to videoModel.matchId,
            "content_id" to videoModel.contentId,
        )
        cleverTapDefaultInstance?.pushEvent("AN_PLAYER_STOP", parameters)
    }

    fun trackFirebaseScreen(type: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, type)
        }

        val parameters = mapOf("screen_name" to type)
        cleverTapDefaultInstance?.pushEvent("AN_SCREEN_VIEW", parameters)
    }
}