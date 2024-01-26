package com.willow.android.mobile.views.pages

import android.content.Context
import android.content.Intent
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.configs.WiConfig
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.models.video.SuggestedVideosModel
import com.willow.android.mobile.models.video.VideoModel
import com.willow.android.mobile.services.DeeplinkService
import com.willow.android.mobile.views.pages.iAPPage.IAPPageActivity
import com.willow.android.mobile.views.pages.loginPage.LoginPageActivity
import com.willow.android.mobile.views.pages.videoDetailPage.VideoDetailPageActivity
import com.willow.android.mobile.views.popup.messagePopup.MessagePopupActivity

object PagesNavigator {
    fun showAuthScreensIfRequired(context: Context, videoModel: VideoModel) : Boolean {
        if (needToShowLoginForVideo(videoModel.needLogin)) {
            launchLoginPage(context)
            return true
        } else if (needToShowSubscriptionForVideo(videoModel.needSubscription)) {
            if (WiConfig.IAPEnabled || WiConfig.IAPRestoreEnabled) {
                launchIAPPage(context)
                return true
            } else {
                showPopupMessage(context, MessageConfig.needSubscription)
                return true
            }
        }

        return false
    }

    fun chooseAuthPlayController(context: Context, videoModel: VideoModel, suggestedVideos: SuggestedVideosModel, isDeeplinkVideo: Boolean = false) {
        val shouldShowAuthScreens = showAuthScreensIfRequired(context, videoModel)

        if (!shouldShowAuthScreens) {
            if (isAuthorizedToWatchVideo(videoModel.needLogin, videoModel.needSubscription)){
                choosePlaybackUrl(context, videoModel, suggestedVideos, isDeeplinkVideo)
            } else {
                showPlaybackFailError(context)
            }
        }
    }

    fun needToShowLoginForVideo(needLogin: Boolean): Boolean {
        if (needLogin && (!UserModel.isLoggedIn())) {
            return true
        }

        return false
    }

    fun needToShowSubscriptionForVideo(needSubscription: Boolean): Boolean {
        if (needSubscription && (!UserModel.isSubscribed)) {
            return true
        }

        return false
    }

    fun isAuthorizedToWatchVideo(needLogin: Boolean, needSubscription: Boolean): Boolean {
        if (!needLogin) {
            return true
        } else if (!needSubscription && UserModel.isLoggedIn()) {
            return true
        } else if (UserModel.isSubscribed) {
            return true
        }

        return false
    }

    fun choosePlaybackUrl(context: Context, videoModel: VideoModel, suggestedVideos: SuggestedVideosModel, isDeeplinkVideo: Boolean) {
        launchVideoDetailPage(context, videoModel, suggestedVideos, false, isDeeplinkVideo)
    }

    fun showPlaybackFailError(context: Context) {
        showPopupMessage(context, MessageConfig.playbackFailMsg)
    }

    fun launchVideoDetailPage(context: Context, videoModel: VideoModel, suggestedVideos: SuggestedVideosModel, isPlaylist: Boolean, isDeeplinkVideo: Boolean = false) {
        val intent = Intent(context, VideoDetailPageActivity::class.java).apply {}
        intent.putExtra("VDP_DATA", videoModel)
        intent.putExtra("SUGGESTED_VIDEOS", suggestedVideos)
        intent.putExtra("IS_PLAYLIST", isPlaylist)
        intent.putExtra("IS_DEEPLINK_VIDEO", isDeeplinkVideo)
        context.startActivity(intent)
    }

    fun launchLoginPage(context: Context) {
        val intent = Intent(context, LoginPageActivity::class.java).apply {}
        context.startActivity(intent)
    }

    fun launchIAPPage(context: Context) {
        if (WiConfig.IAPEnabled || WiConfig.IAPRestoreEnabled) {
            val intent = Intent(context, IAPPageActivity::class.java).apply {}
            context.startActivity(intent)
        } else {
            showPopupMessage(context, MessageConfig.needSubscription)
        }
    }
    
    fun showPopupMessage(context: Context, message: String) {
        val intent = Intent(context, MessagePopupActivity::class.java).apply {}
        intent.putExtra("MESSAGE", message)
        context.startActivity(intent)
    }

    /* ******************** Deeplink related method ******************** */
    fun launchSharePopup(context: Context, videoModel: VideoModel){
        DeeplinkService.generateShareUrl(context, videoModel)
    }
}