package com.willow.android.mobile.services

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.lang.reflect.Type


object StorageService {
    private const val NAME = "WillowStorage"
    private const val MODE = Context.MODE_PRIVATE
    private const val userIdKey = "userId"
    private const val userEmailKey = "userEmail"
    private const val userAdsCategoryKey = "userAdsCategory"
    private const val googleUserKey = "googleUser"
    private const val appleUserKey = "appleUser"
    private const val tveUserKey = "tveUser"

    private const val tveProviderKey = "tveProvider"
    private const val tveDeviceIdKey = "tveDeviceId"
    private const val localNotificationIdsKey = "localNotificationIds"

    private const val showSourcesKey = "showSources"
    private const val showScoresKey = "showScores"
    private const val showResultsKey = "showResults"

    private const val showOnboardKey = "showOnboard"


    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    fun storeUserId(userId: String) {
        if(this::preferences.isInitialized) {
            preferences.edit().putString(userIdKey, userId).apply()
        }
    }

    fun getUserId() : String {
        Timber.tag("USERID").d("IN")
        if (this::preferences.isInitialized) {
            val storedValue = preferences.getString(userIdKey, "")

            if (storedValue != null) {
                return storedValue
            }
        }
        return ""
    }

    fun removeUserId() {
        if(this::preferences.isInitialized) {
            preferences.edit().remove(userIdKey).apply()
        }
    }

    fun storeUserAdsCategory(adsCategory: String) {
        if(this::preferences.isInitialized) {
            preferences.edit().putString(userAdsCategoryKey, adsCategory).apply()
        }
    }

    fun getUserAdsCategory() : String {
        if(this::preferences.isInitialized) {
            val storedValue = preferences.getString(userAdsCategoryKey, "free")
            if (storedValue != null) {
                return storedValue
            }
        }
        return "free"
    }

    fun storeUserEmail(email: String) {
        if(this::preferences.isInitialized) {
            preferences.edit().putString(userEmailKey, email).apply()
        }
    }

    fun getUserEmail() : String {
        if(this::preferences.isInitialized) {
            val storedValue = preferences.getString(userEmailKey, "")
            if (storedValue != null) {
                return storedValue
            }
        }
        return ""
    }

    fun removeUserEmail() {
        if(this::preferences.isInitialized) {
            preferences.edit().remove(userEmailKey).apply()
        }
    }


    fun storeGoogleUser(status: Boolean) {
        if(this::preferences.isInitialized) {
            preferences.edit().putBoolean(googleUserKey, status).apply()
        }
    }

    fun getGoogleUser() : Boolean {
        if(this::preferences.isInitialized) {
            return preferences.getBoolean(googleUserKey, false)
        }
        return false
    }

    fun removeGoogleUser() {
        if(this::preferences.isInitialized) {
            preferences.edit().remove(googleUserKey).apply()
        }
    }


    fun storeAppleUser(status: Boolean) {
        if(this::preferences.isInitialized) {
            preferences.edit().putBoolean(appleUserKey, status).apply()
        }
    }

    fun getAppleUser() : Boolean {
        if(this::preferences.isInitialized) {
            return preferences.getBoolean(appleUserKey, false)
        }
        return false
    }

    fun removeAppleUser() {
        if(this::preferences.isInitialized) {
            preferences.edit().remove(appleUserKey).apply()
        }
    }


    fun storeTVEUser(status: Boolean) {
        if(this::preferences.isInitialized) {
            preferences.edit().putBoolean(tveUserKey, status).apply()
        }
    }

    fun getTVEUser() : Boolean {
        if(this::preferences.isInitialized) {
            return preferences.getBoolean(tveUserKey, false)
        }
        return false
    }

    fun removeTVEUser() {
        if(this::preferences.isInitialized) {
            preferences.edit().remove(tveUserKey).apply()
        }
    }


    fun storeTVEProvider(provider: String) {
        if(this::preferences.isInitialized) {
            preferences.edit().putString(tveProviderKey, provider).apply()
        }
    }

    fun getTVEProvider() : String {
        if(this::preferences.isInitialized) {
            val storedValue = preferences.getString(tveProviderKey, "")
            if (storedValue != null) {
                return storedValue
            }
        }
        return ""
    }

    fun removeTVEProvider() {
        if(this::preferences.isInitialized) {
            preferences.edit().remove(tveProviderKey).apply()
        }
    }


    fun storeTVEDeviceId(id: String) {
        if(this::preferences.isInitialized) {
            preferences.edit().putString(tveDeviceIdKey, id).apply()
        }
    }

    fun getTVEDeviceId() : String {
        if(this::preferences.isInitialized) {
            val storedValue = preferences.getString(tveDeviceIdKey, "")
            if (storedValue != null) {
                return storedValue
            }
        }
        return ""
    }

    fun removeTVEDeviceId() {
        if(this::preferences.isInitialized) {
            preferences.edit().remove(tveDeviceIdKey).apply()
        }
    }

    fun storeLocalNotificationIds(ids: HashMap<String, String>) {
        if(this::preferences.isInitialized) {
            val gson = Gson()
            val json = gson.toJson(ids)
            preferences.edit().putString(localNotificationIdsKey, json).apply()
        }
    }

    fun getLocalNotificationIds() : HashMap<String, String> {
        if(this::preferences.isInitialized) {
            val storedStringValue = preferences.getString(localNotificationIdsKey, "")

            if (!storedStringValue.isNullOrEmpty()) {
                val gson = Gson()
                val type: Type = object : TypeToken<HashMap<String, String>>() {}.type

                return gson.fromJson(storedStringValue, type)
            }
        }
        return HashMap()
    }


    fun storeShowSources(show: Boolean) {
        if(this::preferences.isInitialized) {
            preferences.edit().putBoolean(showSourcesKey, show).apply()
        }
    }

    fun getShowSources() : Boolean {
        if(this::preferences.isInitialized) {
            return preferences.getBoolean(showSourcesKey, false)
        }
        return false
    }


    fun storeShowScores(show: Boolean) {
        if(this::preferences.isInitialized) {
            preferences.edit().putBoolean(showScoresKey, show).apply()
        }
    }

    fun getShowScores() : Boolean {
        return preferences.getBoolean(showScoresKey, true)
    }


    fun storeShowResults(show: Boolean) {
        if(this::preferences.isInitialized) {
            preferences.edit().putBoolean(showResultsKey, show).apply()
        }
    }

    fun getShowResults() : Boolean {
        if(this::preferences.isInitialized) {
            return preferences.getBoolean(showResultsKey, true)
        }
        return true
    }

    fun storeShowOnboard(show: Boolean) {
        if(this::preferences.isInitialized) {
            preferences.edit().putBoolean(showOnboardKey, show).apply()
        }
    }

    fun getShowOnboard() : Boolean {
        if(this::preferences.isInitialized) {
            return preferences.getBoolean(showOnboardKey, true)
        }
        return true
    }


    fun clearData() {
        if(this::preferences.isInitialized) {
            preferences.edit().clear().apply()
        }

        // Should not be reset this value unless app is re-installed
        storeShowOnboard(false)
    }
}