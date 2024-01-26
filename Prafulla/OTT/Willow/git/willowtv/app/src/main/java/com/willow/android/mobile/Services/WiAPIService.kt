package com.willow.android.mobile.services

import android.util.Log
import com.willow.android.mobile.configs.Keys
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.models.video.VideoModel
import com.willow.android.mobile.utils.Utils
import com.willow.android.mobile.utils.sha256

object WiAPIService {
    const val shareFallbackUrl = "https://www.willow.tv/devices"
    const val cloudUrl = "https://willowfeedsv2.willow.tv/"
    const val willowUrl = "https://www.willow.tv/"

    const val configUrl = cloudUrl + "MobileV4/AndroidConfig.json"
    const val dfpConfigUrl = cloudUrl + "v1cxwi/v1_ads_user_config.json"
    const val tveConfigUrl = cloudUrl + "MobileV4/TVEProviders_android.json"
    const val iapOffersUrl = cloudUrl + "MobileV4/IAPOffers.json"
    const val homeUrlUSA = cloudUrl + "MobileV4/HomePage.json"
    const val videosUrlUSA = cloudUrl + "MobileV4/VideosPage.json"
    const val fixturesUrlUSA = cloudUrl + "MobileV4/FixturesPage.json"
    const val resultsUrlUSA = cloudUrl + "MobileV4/ResultsPage.json"
    const val homeUrlCA = cloudUrl + "MobileV4/HomePageCA.json"
    const val videosUrlCA = cloudUrl + "MobileV4/VideosPageCA.json"
    const val fixturesUrlCA = cloudUrl + "MobileV4/FixturesPageCA.json"
    const val resultsUrlCA = cloudUrl + "MobileV4/ResultsPageCA.json"
    const val settingsUrl = cloudUrl + "MobileV4/SettingsPageV1.json"
    const val matchCenterUrl = cloudUrl + "MobileV4/MatchCenter/"
    const val scorecardUrl = cloudUrl + "MobileV4/Scorecard/"
    const val commentaryUrl = cloudUrl + "MobileV4/Charts/CBB/"
    const val lastBallScoreUrl = cloudUrl + "MobileV4/Charts/CLB/"

    const val videoDetailPageUrl = willowUrl + "get_match_videos_by_content_slug"
    const val authUrl = willowUrl + "EventMgmt/webservices/mobi_auth.asp"
    const val socialAuthUrl = willowUrl + "facebook_google_login"
    const val appleAuthUrl = willowUrl + "apple_login"
    const val tveAuthUrl = willowUrl + "verify_tv_provider_login_mobile"
    const val iapReceiptValidationUrl = willowUrl + "sync_android_receipt"
    const val verifyEmailUrl = willowUrl + "send_email_verification_mail"
    const val deleteAccountUrl = willowUrl + "delete_user_data"

    const val wiAnalyticsUrl = "https://eventlog.willow.tv/mapplogs"
    const val pollerUrl = "https://plrdev.willow.tv/plMobile"
    const val countryCodeUrl = "https://ws.willow.tv/countryCode.asp"
    const val iapImageUrl = "https://aimages.willow.tv/NovCam_Devices.png"

    fun getVideoDetailParams(slug: String, duration: String, matchId: String, contentType: String): MutableMap<String, String> {
        var userIdValue = "0"
        if (UserModel.userId.isNotEmpty()) {
            userIdValue = UserModel.userId
        }

        val timestamp = Utils.getCurrentTimestamp()
        val tokenBaseString = (Keys.SOCIAL_SHARE_SECRET_KEY + slug.sha256() + userIdValue + duration  + matchId + contentType + timestamp)
        val token = tokenBaseString.sha256()

        val params: MutableMap<String, String> = HashMap()
        params["slug"] = slug
        params["match_id"] = matchId
        params["auth_token"] = token
        params["content_type"] = contentType
        params["user_id"] = userIdValue
        params["duration"] = duration
        params["ts"] = timestamp
        return params
    }

    fun getPlaybackStreamParams(videoModel: VideoModel): MutableMap<String, String> {
        val authToken = Utils.getPlaybackAuthToken(videoModel = videoModel)

        val params: MutableMap<String, String> = HashMap()
        params["mid"] = videoModel.matchId
        params["type"] = videoModel.contentType
        params["need_login"] = videoModel.needLogin.toString()
        params["need_subscription"] = videoModel.needSubscription.toString()
        params["auth_token"] = authToken
        params["devType"] = Keys.devType
        params["wuid"] = UserModel.getUserIdValue()

        if (videoModel.contentType.equals("highlight", true)) {
            params["id"] = videoModel.contentId
        } else if (videoModel.contentType.equals("live", true)) {
            params["pr"] = videoModel.livePriority
        } else {
            params["title"] = videoModel.contentId
        }

        return params
    }


    fun getTVEPlaybackStreamParams(token: String, videoModel: VideoModel): MutableMap<String, String> {
        val params: MutableMap<String, String> = HashMap()
        params["mid"] = videoModel.matchId
        params["type"] = videoModel.contentType
        params["need_login"] = videoModel.needLogin.toString()
        params["need_subscription"] = videoModel.needSubscription.toString()
        params["devType"] = Keys.devType
        params["token"] = token

        if (videoModel.contentType.equals("highlight", true)) {
            params["id"] = videoModel.contentId
        } else if (videoModel.contentType.equals("live", true)) {
            params["pr"] = videoModel.livePriority
        } else {
            params["title"] = videoModel.contentId
        }

        if (UserModel.isTVEProviderSpectrum()) {
            val playbackContentId = Utils.getTVEPlaybackEncryptedContentId(videoModel.contentId)
            val contentIdFeed = "<rss version=\"2.0\"><channel><title>WILLOW</title><item><title>" + playbackContentId + "</title></item></channel></rss>"
            params["requestor_content_id"] = contentIdFeed
            params["request_from_mvpd"] = UserModel.tveProvider
        }

        return params
    }

    fun getExistingEmailParams(email: String): MutableMap<String, String> {
        val md5BaseString = Keys.md5Key + "::" + email
        val authToken = Utils.generateMD5(md5BaseString)

        val params: MutableMap<String, String> = HashMap()
        params["action"] = "checkAccount"
        params["email"] = email
        params["authToken"] = authToken
        return params
    }

    fun getLoginParams(email: String, password: String): MutableMap<String, String> {
        val authToken = Utils.getLoginAuthToken(email, password)

        val params: MutableMap<String, String> = HashMap()
        params["action"] = "login"
        params["email"] = email
        params["password"] = password
        params["authToken"] = authToken
        return params
    }

    fun getSignupParams(email: String, password: String, fname: String): MutableMap<String, String> {
        val authToken = Utils.getLoginAuthToken(email, password)

        val params: MutableMap<String, String> = HashMap()
        params["action"] = "register"
        params["email"] = email
        params["password"] = password
        params["authToken"] = authToken
        return params
    }

    fun getForgotPasswordParams(email: String): MutableMap<String, String> {
        val authToken = Utils.getForgotPasswordAuthToken(email)

        val params: MutableMap<String, String> = HashMap()
        params["action"] = "fgpassword"
        params["email"] = email
        params["authToken"] = authToken
        return params
    }

    fun getCheckSubscriptionParams(): MutableMap<String, String> {
        val authToken = Utils.checkSubscriptionAuthToken(UserModel.userId)

        val params: MutableMap<String, String> = HashMap()
        params["action"] = "checkSubscription"
        params["uid"] = UserModel.userId
        params["authToken"] = authToken
        return params
    }

    fun getTVELoginParams(token: String): MutableMap<String, String> {
        val params: MutableMap<String, String> = HashMap()
        params["token"] = token
        params["devType"] = Keys.devType
        return params
    }

    fun getGoogleLoginParams(email: String): MutableMap<String, String> {
        val authToken = Utils.getGoogleLoginAuthToken(email = email)
        val socialData = "{\"sub\": \"" + UserModel.googleUserId + "\",  \"name\":\"" + UserModel.googleFullName + "\", \"email\": \"" + UserModel.email + "\", \"given_name\": \"" + UserModel.googleGivenName + "\" , \"family_name\": \"" + UserModel.googleFamilyName + "\"}";

        val params: MutableMap<String, String> = HashMap()
        params["email"] = email
        params["auth_token"] = authToken
        params["social_data"] = socialData
        params["provider"] = "google"
        params["devType"] = Keys.devType

        Log.e("Google Login params", params.toString())
        return params
    }

    fun getAppleLoginParams(status: String, email: String, state: String, code: String, id_token: String): MutableMap<String, String> {
        val params: MutableMap<String, String> = HashMap()
        params["status"] = status
        params["email"] = email
        params["state"] = state
        params["code"] = code
        params["id_token"] = id_token
        params["authorize_to_server"] = "1"
        return params
    }

    fun getSyncReceiptParams(userId: String, receipt: String): MutableMap<String, String> {
        val params: MutableMap<String, String> = HashMap()
        params["user_id"] = userId
        params["receipt"] = receipt
        return params
    }

    fun getVerifyEmailParams(email: String): MutableMap<String, String> {
        val authToken = Utils.getVerifyEmailAuthToken(email)

        val params: MutableMap<String, String> = HashMap()
        params["email"] = email
        params["auth_token"] = authToken
        return params
    }

    fun getDeleteAccountParams(email: String, userId: String): MutableMap<String, String> {
        val authToken = Utils.getDeleteAccountAuthToken(email, userId)

        val params: MutableMap<String, String> = HashMap()
        params["email"] = email
        params["auth_token"] = authToken
        params["user_id"] = userId
        return params
    }
}