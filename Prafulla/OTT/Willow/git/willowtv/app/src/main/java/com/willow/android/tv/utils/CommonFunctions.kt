package com.willow.android.tv.utils

import android.content.Context
import com.google.gson.JsonParser
import com.willow.android.BuildConfig
import com.willow.android.tv.data.repositories.commondatamodel.Card
import com.willow.android.tv.data.repositories.loginpage.datamodel.APILoginDataModel
import com.willow.android.tv.ui.playback.model.PlayerRequestModel
import com.willow.android.tv.utils.GlobalConstants.MAX_CONTENT_PROGRESS
import com.willow.android.tv.utils.GlobalConstants.MIN_CONTENT_PROGRESS
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KProperty1


object CommonFunctions {

    fun generateMD5(email: String?,password: String?): String {
        val baseString = BuildConfig.SECRET_SEED+ "::" + email + "::" + password
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(baseString.toByteArray())).toString(16).padStart(32, '0')
    }

    fun generateMD5Signup(email: String,password: String,name:String): String {
        val baseString = BuildConfig.SECRET_SEED+ "::" + email + "::" + password + "::" + name
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(baseString.toByteArray())).toString(16).padStart(32, '0')
    }

    fun generateMD5Common(baseStringTobeEcoded:String?): String {
        val baseString = BuildConfig.SECRET_SEED+ "::" + baseStringTobeEcoded
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(baseString.toByteArray())).toString(16).padStart(32, '0')
    }
    /**
    * Save progress only if user has watched more than 5% and less than 95% of total duration
     **/
    fun shouldSaveProgressToDb(progress: Double, totalDuration: Double): Boolean{
        Timber.d("progress:: "+progress + "totalDuration:: "+totalDuration)
        val percent = (progress/totalDuration)*100

        Timber.d("Percent:: "+percent)

        return (percent in (MIN_CONTENT_PROGRESS+1)..MAX_CONTENT_PROGRESS)
    }

    /**
    * Call this method with the return type of desired value to get any data from user data saved in shared pref
    * */
    @Suppress("UNCHECKED_CAST")
    fun <R> getUserData(context: Context, propertyName: String): R {
        val prefRepository = PrefRepository(context)

        val data = prefRepository.getUserData() as APILoginDataModel
        val result = data.result

        val property = result::class.members
            // don't cast here to <Any, R>, it would succeed silently
            .first { it.name == propertyName } as KProperty1<Any, *>
        // force a invalid cast exception if incorrect type here
        return property.get(result) as R
    }


    fun getRequestorContentId(contentId:Int?):String{
        val encryptedContentID = getTVEPlaybackEncryptedContentId(contentId.toString())
        val resource =
            "<rss version=\"2.0\"><channel><title>WILLOW</title><item><title>$encryptedContentID</title></item></channel></rss>"

        return resource
    }
    fun getTVEPlaybackEncryptedContentId(contentId: String): String? {
        val md5BaseString = BuildConfig.TVE_PLAYBACK_KEY + "" + contentId
        return generateMD5(md5BaseString)
    }

    fun generateMD5(baseString: String): String? {
        try {
            val digest = MessageDigest.getInstance("MD5")
            digest.update(baseString.toByteArray())
            val messageDigest = digest.digest()
            val hexString = StringBuffer()
            for (i in messageDigest.indices) {
                var h = Integer.toHexString(0xFF and messageDigest[i].toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    fun whereToGo(card: Card,context: Context):GoTo{

        val prefRepository = PrefRepository(context)
        val userLoggedIn  =  prefRepository.getLoggedIn() == true || prefRepository.getTVELoggedIn() == true
        val userSubscribed = prefRepository.getUserSubscribed()==true
        val isLiveVideo = card.isShowLiveTag()
        /**
         * isSubscriptionRequired() method returns false if its a live video.
         * But user needs to be subscribed to watch live video so the below condition
         * Even if isSubscriptionRequired() is false for a live video, if isLiveVideo is true,subscriptionReqd will be true
         * */
        val subscriptionReqd = card.isSubscriptionRequired() || isLiveVideo
        val loginReqd = card.isLoginRequired()

        /**
         * Check if video requires login and user is logged in
         * if login REQUIRED, check if user is logged in - if user logged in goto player else go to login flow
         * if login NOT REQUIRED, go to player and play video
         * */
        if(loginReqd){
            if(userLoggedIn) {
                /**
                 * If user satisfies Login criteria, Check if video requires subscription and user is subscribed
                 * if Subscription REQUIRED, check if user is subscribed - if user subscribed in goto player else go to Subscription flow
                 * if Subscription NOT REQUIRED, go to player and play video
                 * */
                return if (subscriptionReqd) {
                    if (userSubscribed)
                        GoTo.PLAY_VIDEO
                    else
                        GoTo.SUBSCRIPTION
                } else {
                    GoTo.PLAY_VIDEO
                }
            }else{
                return GoTo.LOGIN
            }
        }else{
            return GoTo.PLAY_VIDEO
        }
    }

    /**
     * Takes the gmt timestamp and Returns local time in format Mar 20, Wed @ 10:20 AM
     * */
    fun convertToLocaleDateTime(gmtTimestamp: Long): String {

        val timeZone = TimeZone.getDefault().id
        val gmtInstant = Instant.ofEpochSecond(gmtTimestamp)
        val gmtDateTime = LocalDateTime.ofInstant(gmtInstant, ZoneId.of(timeZone))
        val localeDateTime = gmtDateTime.atZone(ZoneId.of(timeZone)).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("MMM dd, EEE : yyyy @ HH:mm")

        return formatter.format(localeDateTime)+" "+ getTimeZoneShort()
    }

    /**
     * Takes the gmt timestamp and Returns local time in format March 2023
     * */
    fun convertToMonthFromGmtTs(gmtTimestamp: Long): String {

        val timeZone = TimeZone.getDefault().id
        val gmtInstant = Instant.ofEpochSecond(gmtTimestamp)
        val gmtDateTime = LocalDateTime.ofInstant(gmtInstant, ZoneId.of(timeZone))
        val localeDateTime = gmtDateTime.atZone(ZoneId.of(timeZone)).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")

        return formatter.format(localeDateTime)
    }

    /**
     * Takes the gmt timestamp and Returns local time in format Today 18:20PST, Tonight 18:20 PST , Tomorrow 18:20PST or
     * Mar 20, Wed @ 18:20 PST
     * */
    fun convertToLocaleDateTimeWithDay(gmtTimestamp: Long): String {
        val timeZone = TimeZone.getDefault().id
        val gmtInstant = Instant.ofEpochSecond(gmtTimestamp)
        val gmtDateTime = LocalDateTime.ofInstant(gmtInstant, ZoneId.of(timeZone))
        val localeDateTime = gmtDateTime.atZone(ZoneId.of(timeZone)).toLocalDateTime()

        // Determine if the timestamp corresponds to today, tonight, tomorrow, or another date
        val currentDate = LocalDate.now(ZoneId.of(timeZone))
        val tomorrowDate = currentDate.plusDays(1)
        val formatter = DateTimeFormatter.ofPattern("MMM dd, YYYY")

        return when {
            localeDateTime.toLocalDate() == currentDate -> "TODAY" /*+
                    timeformatter.format(localeDateTime) + " " + getTimeZoneShort()*/
//                    TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)

            localeDateTime.toLocalDate() == tomorrowDate -> "TOMORROW" /*+
                    timeformatter.format(localeDateTime) + " " + getTimeZoneShort()*/
//                    TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)
            localeDateTime.toLocalDate() == currentDate && localeDateTime.hour >= 17 -> "TONIGHT"/*+
                    timeformatter.format(localeDateTime) + " " +getTimeZoneShort()*/
//                    ZoneId.systemDefault().getDisplayName(false, TimeZone.SHORT)
            else -> {

                formatter.format(localeDateTime) /*+ " " + getTimeZoneShort()*/ /*TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)*/
            }
        }
    }

    fun convertToLocaleTime(gmtTimestamp: Long): String {
        val timeZone = TimeZone.getDefault().id
        val gmtInstant = Instant.ofEpochSecond(gmtTimestamp)
        val gmtDateTime = LocalDateTime.ofInstant(gmtInstant, ZoneId.of(timeZone))
        val localeDateTime = gmtDateTime.atZone(ZoneId.of(timeZone)).toLocalDateTime()

        // Determine if the timestamp corresponds to today, tonight, tomorrow, or another date

        val timeformatter = DateTimeFormatter.ofPattern("HH:mm")

        return timeformatter.format(localeDateTime) + " " + getTimeZoneShort()
    }
    fun getTimeZoneShort():String{
        val zone = ZoneId.systemDefault()
        val shortTimeZoneFormatter = DateTimeFormatter.ofPattern("zzz", Locale.getDefault())
        return ZonedDateTime.now(zone).format(shortTimeZoneFormatter)

    }
    /**
     * input format February 15
     * return format: Feb 15
     */
    fun convertToAbbreviatedMonthYear(inputString: String?): List<String>? {
        val inputFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())

        val date = inputString?.let { inputFormat.parse(it) }
        return date?.let { outputFormat.format(it) }?.split(" ")
    }

    /**
     * return format: Feb 15
     */
    fun convertToLocaleMonthDate(gmtTimestamp: Long): String {

        val timeZone = TimeZone.getDefault().id
        val gmtInstant = Instant.ofEpochSecond(gmtTimestamp)
        val gmtDateTime = LocalDateTime.ofInstant(gmtInstant, ZoneId.of(timeZone))
        val localeDateTime = gmtDateTime.atZone(ZoneId.of(timeZone)).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("MMM dd")

        return formatter.format(localeDateTime)
    }

    /**
     * return format: Feb 15, 2023
     */
    fun convertToLocaleMonthDateYear(gmtTimestamp: Long): String {

        val timeZone = TimeZone.getDefault().id
        val gmtInstant = Instant.ofEpochSecond(gmtTimestamp)
        val gmtDateTime = LocalDateTime.ofInstant(gmtInstant, ZoneId.of(timeZone))
        val localeDateTime = gmtDateTime.atZone(ZoneId.of(timeZone)).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("MMM dd, EEE")

        return formatter.format(localeDateTime)
    }


    /**
     * dateString will be in "2023-03-15T14:00:00"
     * return format will be in "tonight | 9 PM TIMEZONE"
     */
    fun convertTimeToHAFormat( dateString: String?) : String? {
        //val dateString =  "2023-03-18T14:00:00"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        dateString?.let {
            val date: Date? = dateFormat.parse(it)
            val timeFormat = SimpleDateFormat("h a")
            val timeString = timeFormat.format(date) + " " + getTimeZoneShort()
            val cal = Calendar.getInstance()
            cal.time = date
            val hourOfDay = cal[Calendar.HOUR_OF_DAY]

            val formattedString = if (hourOfDay >= 18) {
                "TONIGHT | $timeString"
            } else if (hourOfDay in 0..5) {
                "TONIGHT | $timeString"
            } else {
                val now = Calendar.getInstance()
                val daysDiff = cal[Calendar.DAY_OF_YEAR] - now[Calendar.DAY_OF_YEAR]
                if (daysDiff == 0) {
                    "TODAY | $timeString"
                } else if (daysDiff == 1) {
                    "TOMORROW | $timeString"
                } else {
                    val dateFormat2 = SimpleDateFormat("MMM d")
                    dateFormat2.format(date) + " | " + timeString
                }
            }
            Timber.d("convertTimeToHAFormat >> $formattedString")
            return formattedString
        }
        return null
    }

    /**
     * dateString will be in "2023-03-15T14:00:00"
     * return format will be in "TODAY | 21.00 PST"
     */
    fun convertTimeToDigitalFormat( dateString: String?) : String? {
        //val dateString =  "2023-03-18T14:00:00"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        dateString?.let {
            val date: Date? = dateFormat.parse(it)
            val timeFormat = SimpleDateFormat("HH:mm")
            val timeString = timeFormat.format(date) + " " + getTimeZoneShort()
            val cal = Calendar.getInstance()
            if (date != null) {
                cal.time = date
            }
            val now = Calendar.getInstance()
            val formattedString = when (cal[Calendar.DAY_OF_YEAR] - now[Calendar.DAY_OF_YEAR]) {
                0 -> {
                    "TODAY | $timeString"
                }
                1 -> {
                    "TOMORROW | $timeString"
                }
                else -> {
                    val dateFormat2 = SimpleDateFormat("MMM d")
                    dateFormat2.format(date) + " | " + timeString
                }
            }
            Timber.d("convertTimeToDigitalFormat >> $formattedString")
            return formattedString
        }
        return null
    }

    /**
     * Takes the progress in millis and duration in seconds and returns the percentage
     * */
    fun calculateProgressPercent(progressMillis: Double, totalDurationSeconds: Double): Int {
        val totalDurationMillis = totalDurationSeconds * 1000 // convert duration to milliseconds

        val progressPercent  = (progressMillis.toFloat() / totalDurationMillis.toFloat() * 100).toInt()
        Timber.d("calculateProgressPercent ::$progressPercent")

        return progressPercent
    }

    /**
    * Takes the progress in millis and duration in seconds and returns the remaining time in format "1 hour and 32 minutes remaining"
    * */
    fun getRemainingTime(progressInMilliSec: Double, durationInSec: Double): String {
        val remainingSeconds = durationInSec - progressInMilliSec/1000

        // Calculate remaining hours, minutes, and seconds
        val remainingHours = (remainingSeconds / 3600).toInt()
        val remainingMinutes = ((remainingSeconds % 3600) / 60).toInt()


        // Format the remaining time as a string
        val remainingTime = when {
            remainingHours > 1 -> "$remainingHours hours and $remainingMinutes minutes remaining"
            remainingHours == 1 -> "$remainingHours hour and $remainingMinutes minutes remaining"
            remainingMinutes > 0 -> "$remainingMinutes minutes remaining"
            else -> "Less than a minute remaining"
        }

        return remainingTime
    }


    fun getPlaybackParams(mid: String?, type: String?, contentId: Int?, priority: String?,context: Context, needLogin:Boolean?, needSubscription:Boolean?):  HashMap<String, String>  {
        val prefRepository = PrefRepository(context)
        val params = HashMap<String, String>()
        params["mid"] = mid.toString()
        params["type"] = type.toString()
        params["devType"] = WillowRestClient.DEV_TYPE
        params["id"] = contentId.toString()
        params["need_login"] = needLogin.toString()
        params["need_subscription"] = needSubscription.toString()
        if(type =="live" ){
            params["pr"] = priority.toString()
        }
        if (prefRepository.getTVELoggedIn()==true) {
            params["clientless"] = "true"
            if (prefRepository.isTVEProviderSpectrum() && type!="live") {
                params["request_from_mvpd"] = prefRepository.getTVEProvider()
                params["requestor_content_id"] = getRequestorContentId(contentId)
            }
        } else {
            params["wuid"] = prefRepository.getUserID()
        }
        return params
    }

    fun getPlayerReqModelFromHashMap(hashMap: Map<String, String>,context: Context): PlayerRequestModel {
        val prefRepository = PrefRepository(context)
        val matchId = hashMap["mid"] as String
        val contentType = hashMap["type"] as String
        val devType = hashMap["devType"] as String
        val contentId = hashMap["id"] as String

        val playerRequestModel = PlayerRequestModel (matchId = matchId.toInt(),contentId = contentId.toInt(), contentType = contentType, devType = devType, url = "")

        if(contentType =="live" ){
            playerRequestModel.priority = hashMap["pr"] as String
        }
        if (prefRepository.getTVELoggedIn()==true) {
            val clientless = hashMap["clientless"] as String
            val token = hashMap["token"] as String
            playerRequestModel.clientless = clientless
            playerRequestModel.mediaToken = token
            if (prefRepository.isTVEProviderSpectrum() && contentType!="live") {
                val requestFromMvpd = hashMap["request_from_mvpd"] as String
                val requestorContentId = hashMap["requestor_content_id"] as String
                playerRequestModel.requestFromMVPD = requestFromMvpd
                playerRequestModel.requestorContentId = requestorContentId
            }
        }else{
            val wuid = hashMap["wuid"] as String
            playerRequestModel.willowUserID = wuid
        }
        return playerRequestModel
    }



    fun writeJsonToRaw(context: Context, fileName: String, jsonString: String) {
        val outputStream = context.openFileOutput("raw/$fileName.json", Context.MODE_PRIVATE)
        val writer = BufferedWriter(OutputStreamWriter(outputStream))
        writer.write(jsonString)
        writer.close()
        outputStream.close()
    }


    fun isJsonResponse(responseBody: ResponseBody): Boolean {
        val jsonString = responseBody.string()
        return try {
            JsonParser.parseString(jsonString)
            true
        } catch (ex: Exception) {
            false
        }
    }

    /**
     * Given time in long and get the formatted time in " 31st Mar - 28th Mar,2023"
     */
    fun formatDDMMMStartAndEndTime(startTime: Long, endTime: Long): String {
        val timeZone = TimeZone.getDefault().id
        val gmtStartInstant = Instant.ofEpochSecond(startTime)
        val gmtStartDateTime = LocalDateTime.ofInstant(gmtStartInstant, ZoneId.of(timeZone))
        val localeStartDateTime = gmtStartDateTime.atZone(ZoneId.of(timeZone)).toLocalDateTime()

        val gmtEndInstant = Instant.ofEpochSecond(endTime)
        val gmtEndDateTime = LocalDateTime.ofInstant(gmtEndInstant, ZoneId.of(timeZone))
        val localeEndDateTime = gmtEndDateTime.atZone(ZoneId.of(timeZone)).toLocalDateTime()

        val startMonth =  DateTimeFormatter.ofPattern("MMM").format(localeStartDateTime)
        val endMonth =  DateTimeFormatter.ofPattern("MMM").format(localeEndDateTime)
        val year =  DateTimeFormatter.ofPattern("yyyy").format(localeEndDateTime)
        val startDayOfMonth =  DateTimeFormatter.ofPattern("dd").format(localeStartDateTime)
        val endDayOfMonth =  DateTimeFormatter.ofPattern("dd").format(localeEndDateTime)

        val startSuffix = when (startDayOfMonth.toInt()) {
            1, 21, 31 -> "st"
            2, 22 -> "nd"
            3, 23 -> "rd"
            else -> "th"
        }
        val endSuffix = when (endDayOfMonth.toInt()) {
            1, 21, 31 -> "st"
            2, 22 -> "nd"
            3, 23 -> "rd"
            else -> "th"
        }
      /*  val startDay = "$startDayOfMonth$startSuffix"
        val endDay = "$endDayOfMonth$endSuffix"*/
        val startDay = "$startDayOfMonth"
        val endDay = "$endDayOfMonth"

        // Combine the start and end dates and year into the desired format
        return "$startDay $startMonth - $endDay $endMonth, $year"
    }

    /**
     * Given time in long and get the formatted time in "May 04 - May 13, 2023"
     */
    fun formatMMMDDStartAndEndTime(startTime: Long, endTime: Long, justShowStartDate: Boolean = false): String {
        val timeZone = TimeZone.getDefault().id
        val gmtStartInstant = Instant.ofEpochSecond(startTime)
        val gmtStartDateTime = LocalDateTime.ofInstant(gmtStartInstant, ZoneId.of(timeZone))
        val localeStartDateTime = gmtStartDateTime.atZone(ZoneId.of(timeZone)).toLocalDateTime()

        val gmtEndInstant = Instant.ofEpochSecond(endTime)
        val gmtEndDateTime = LocalDateTime.ofInstant(gmtEndInstant, ZoneId.of(timeZone))
        val localeEndDateTime = gmtEndDateTime.atZone(ZoneId.of(timeZone)).toLocalDateTime()

        val startMonth =  DateTimeFormatter.ofPattern("MMM").format(localeStartDateTime)
        val endMonth =  DateTimeFormatter.ofPattern("MMM").format(localeEndDateTime)
        val year =  DateTimeFormatter.ofPattern("yyyy").format(localeEndDateTime)
        val startDayOfMonth =  DateTimeFormatter.ofPattern("dd").format(localeStartDateTime)
        val endDayOfMonth =  DateTimeFormatter.ofPattern("dd").format(localeEndDateTime)

        val startSuffix = when (startDayOfMonth.toInt()) {
            1, 21, 31 -> "st"
            2, 22 -> "nd"
            3, 23 -> "rd"
            else -> "th"
        }
        val endSuffix = when (endDayOfMonth.toInt()) {
            1, 21, 31 -> "st"
            2, 22 -> "nd"
            3, 23 -> "rd"
            else -> "th"
        }
     /*   val startDay = "$startDayOfMonth$startSuffix"
        val endDay = "$endDayOfMonth$endSuffix"*/
        val startDay = "$startDayOfMonth"
        val endDay = "$endDayOfMonth"

        // Combine the start and end dates and year into the desired format
        //May 04 - May 13, 2023
        if(justShowStartDate){
            return "$startMonth $startDay, $year"
        }
        return "$startMonth $startDay - $endMonth $endDay, $year"
    }

}