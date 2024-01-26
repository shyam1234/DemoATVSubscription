package com.willow.android.mobile.utils

import android.text.format.DateFormat
import com.willow.android.mobile.configs.Keys
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.models.video.VideoModel
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object Utils {
    fun getLoginAuthToken(email: String, password: String): String {
        val md5BaseString = Keys.md5Key + "::" + email + "::" + password
        return generateMD5(md5BaseString)
    }

    fun getGoogleLoginAuthToken(email: String): String {
        val md5BaseString = Keys.md5Key + "::" + email
        return generateMD5(md5BaseString)
    }

    fun getAppleLoginStateToken(timestampString: String): String {
        val sha256StringKey = Keys.md5Key + "::" + timestampString
        return (sha256StringKey).sha256()
    }

    fun getAppleLoginAuthToken(appleUserId: String): String {
        val md5BaseString = Keys.md5Key + "::" + appleUserId
        return generateMD5(md5BaseString)
    }

    fun getForgotPasswordAuthToken(email: String): String {
        val md5BaseString = Keys.md5Key + "::" + email + "::"
        return generateMD5(md5BaseString)
    }

    fun getVerifyEmailAuthToken(email: String): String {
        val md5BaseString = Keys.md5VerifyEmailKey + "::" + email
        return generateMD5(md5BaseString)
    }

    fun getDeleteAccountAuthToken(email: String, userId: String): String {
        val md5BaseString = Keys.md5Key + "::" + email + "::" + userId
        return generateMD5(md5BaseString)
    }

    fun checkSubscriptionAuthToken(userId: String): String {
        val md5BaseString = Keys.md5Key + "::" + userId
        return generateMD5(md5BaseString)
    }

    fun getPlaybackAuthToken(videoModel: VideoModel): String {
        var userId = UserModel.userId
        if (userId.isEmpty()) {
            userId = "0"
        }

        val needLoginString = videoModel.needLogin.toString().capitalize()
        val needSubscriptionString = videoModel.needSubscription.toString().capitalize()

        var md5BaseString = Keys.playbackMd5Key + "::" + userId + Keys.devType

        if (videoModel.contentId.equals("0", ignoreCase = true)) {
            md5BaseString =  md5BaseString + videoModel.matchId + videoModel.contentType + needSubscriptionString + needLoginString
        } else {
            md5BaseString =  md5BaseString + videoModel.matchId + videoModel.contentType + videoModel.contentId + needSubscriptionString + needLoginString
        }

        return generateMD5(md5BaseString)
    }

    fun generateMD5(md5BaseString: String): String{
        val MD5 = "MD5"
        try {
            // Create MD5 Hash
            val digest = MessageDigest
                .getInstance(MD5)
            digest.update(md5BaseString.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    fun areDatesEqual(timestampOne: Long, timestampTwo: Long): Boolean {
        var firstDate = Date(timestampOne * 1000)
        var secondDate = Date(timestampTwo * 1000)

        if (firstDate.date.equals(secondDate.date)) {
            return true
        }

        return false
    }

    fun getCurrentTimestamp(): String {
        val cal: Calendar = Calendar.getInstance(Locale.ENGLISH)
        return cal.timeInMillis.toString()
    }

    fun getFormattedDate(timestamp: Long): String {
        val cal: Calendar = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = timestamp * 1000L
        val date: String = DateFormat.format("MMM dd, yyyy", cal).toString()
        return date
    }

    fun getFormattedTime(timestamp: Long): String {
//        val cal: Calendar = Calendar.getInstance(Locale.ENGLISH)
//        cal.timeInMillis = timestamp * 1000L
//        val date: String = DateFormat.format("HH:mm zzz", cal).toString()
//
//        val cal: Calendar = Calendar.getInstance(Locale.US)
//        val zone: TimeZone = cal.timeZone
//        val id = zone.getDisplayName(false, 0)

        var timeZoneLocal = Locale.US
        if (UserModel.cc.lowercase().equals("ca")) {
            timeZoneLocal = Locale.CANADA
        }

        val timestampValue = Date(timestamp * 1000)
        val dateFormat = SimpleDateFormat("HH:mm z", timeZoneLocal)
        val formattedTime = dateFormat.format(timestampValue)

        return formattedTime
    }

    fun getFormattedDuration(duration: Int): String {
        var formattedDuration = ""

        val longDuration = duration.toLong()
        val hours = TimeUnit.SECONDS.toHours(longDuration)
        val minutes = TimeUnit.SECONDS.toMinutes(longDuration) - (TimeUnit.SECONDS.toHours(longDuration) * 60)
        val second = TimeUnit.SECONDS.toSeconds(longDuration) - (TimeUnit.SECONDS.toMinutes(longDuration) * 60)

        if (hours > 0) {
            formattedDuration = hours.toString() + ":"
        }

        if (minutes > 0) {
            if (minutes < 10) {
                formattedDuration = "0"
            }
            formattedDuration = formattedDuration + minutes.toString() + ":"
        } else {
            formattedDuration = "00:"
        }

        if (second > 0) {
            var secondsString = second.toString()
            if (second < 10) {
                secondsString = "0" + secondsString
            }
            formattedDuration = formattedDuration + secondsString
        } else {
            formattedDuration = formattedDuration + "00"
        }

        return formattedDuration
    }

    fun shouldShowPremiumIcon(needSubscription: Boolean): Boolean {
        if (needSubscription && !UserModel.isSubscribed) {
            return true
        }

        return false
    }


    //@Todo - Validate Email Locally
    fun isInvalidEmail(email: String): Boolean {
//        val pattern = Regex("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,64}")
//        val isValid = pattern.matches(email)
//        return !isValid
        return false
    }

    fun isAllowedInCountry(contentCountryCode: String): Boolean {
        if (contentCountryCode.lowercase().trim().contains(UserModel.cc, ignoreCase = true)) {
            return true
        }
        return false
    }

    fun getTVEPlaybackEncryptedContentId(contentId: String): String {
        val md5BaseString = Keys.tvePlaybackMd5Key + "" + contentId
        val encryptedContentId = generateMD5(md5BaseString)

        return encryptedContentId
    }
}

fun String.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}