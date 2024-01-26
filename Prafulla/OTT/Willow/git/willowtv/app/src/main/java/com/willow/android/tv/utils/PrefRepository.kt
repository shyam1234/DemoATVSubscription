package com.willow.android.tv.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.willow.android.tv.data.repositories.loginpage.datamodel.APILoginDataModel
import com.willow.android.tv.data.repositories.loginpage.datamodel.Result
import com.willow.android.tv.data.repositories.mainactivity.datamodel.APICheckSubDataModel
import com.willow.android.tv.utils.PrefVariables.PREFERENCE_NAME
import com.willow.android.tv.utils.PrefVariables.PREF_ADS_CATEGORY
import com.willow.android.tv.utils.PrefVariables.PREF_ENABLE_DFP_FOR_LIVE
import com.willow.android.tv.utils.PrefVariables.PREF_ENABLE_DFP_FOR_VOD
import com.willow.android.tv.utils.PrefVariables.PREF_LOGGED_IN
import com.willow.android.tv.utils.PrefVariables.PREF_LOGGED_IN_USER_DATA
import com.willow.android.tv.utils.PrefVariables.PREF_MINIMUM_APP_VERSION
import com.willow.android.tv.utils.PrefVariables.PREF_SUBSCRIPTION_USER_DATA
import com.willow.android.tv.utils.PrefVariables.PREF_TVE_DEVICE_ID
import com.willow.android.tv.utils.PrefVariables.PREF_TVE_LOGGED_IN
import com.willow.android.tv.utils.PrefVariables.PREF_TVE_PROVIDER
import com.willow.android.tv.utils.PrefVariables.PREF_TVE_USER_ID
import com.willow.android.tv.utils.PrefVariables.PREF_USER_ID
import com.willow.android.tv.utils.PrefVariables.PREF_USER_SUBSCRIBED
import com.willow.android.tv.utils.config.GlobalTVConfig
import timber.log.Timber


class PrefRepository(val context: Context?) {

    private val pref: SharedPreferences? = context?.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    private val editor = pref?.edit()

    private val gson = Gson()


    private fun String.put(long: Long) {
        editor?.putLong(this, long)
        editor?.commit()
    }

    private fun String.put(int: Int) {
        editor?.putInt(this, int)
        editor?.commit()
    }

    private fun String.put(string: String) {
        editor?.putString(this, string)
        editor?.commit()
    }

    private fun String.put(boolean: Boolean) {
        editor?.putBoolean(this, boolean)
        editor?.commit()
    }

    private fun String.getLong() = pref?.getLong(this, 0)

    private fun String.getInt() = pref?.getInt(this, 0)

    private fun String.getString() = pref?.getString(this, "")!!

    private fun String.getBoolean() = pref?.getBoolean(this, false)


    fun setLoggedIn(isLoggedIn: Boolean) {
        PREF_LOGGED_IN.put(isLoggedIn)
        Timber.d("isDigitalCustomer  setLoggedIn ${isLoggedIn} ")
    }

    fun getLoggedIn() = PREF_LOGGED_IN.getBoolean()


    fun setTVELoggedIn(isTVELoggedIn: Boolean) {
        PREF_TVE_LOGGED_IN.put(isTVELoggedIn)
    }

    fun getTVELoggedIn() = PREF_TVE_LOGGED_IN.getBoolean()


    fun setMinimumAppVersion(version: Long) {
        PREF_MINIMUM_APP_VERSION.put(version)
    }

    fun getMinimumAppVersion() = PREF_MINIMUM_APP_VERSION.getLong()

    fun setUserData(date: APILoginDataModel) {
        PREF_LOGGED_IN_USER_DATA.put(gson.toJson(date))
    }

    fun getUserData(): APILoginDataModel? {
        PREF_LOGGED_IN_USER_DATA.getString().also {
            return if (it.isNotEmpty())
                gson.fromJson(PREF_LOGGED_IN_USER_DATA.getString(), APILoginDataModel::class.java)
            else
                null
        }
    }

    fun setUserSubscribedData(date: APICheckSubDataModel) {
        PREF_SUBSCRIPTION_USER_DATA.put(gson.toJson(date))
    }

    fun getUserSubscribedData(): APICheckSubDataModel? {
        return gson.fromJson(PREF_SUBSCRIPTION_USER_DATA.getString(), APICheckSubDataModel::class.java)
    }

    fun getUserDetails(): Result? {
        return getUserData()?.result
    }

    fun setTVEDeviceID(deviceID: String) {
        PREF_TVE_DEVICE_ID.put(deviceID)
    }

    fun getTVEDeviceID() = PREF_TVE_DEVICE_ID.getString()

    fun setTVEProvider(provider: String) {
        PREF_TVE_PROVIDER.put(provider)
    }

    fun getTVEProvider() = PREF_TVE_PROVIDER.getString()

    fun setUserID(userID: String) {
        PREF_USER_ID.put(userID)
    }

    fun getUserID() = PREF_USER_ID.getString()

    fun setTVEUserID(userID: String) {
        PREF_TVE_USER_ID.put(userID)
    }

    fun getTVEUserID() = PREF_TVE_USER_ID.getString()

    fun setAdsCategory(adsCategory: String) {
        PREF_ADS_CATEGORY.put(adsCategory)
    }

    fun getAdsCategory() = PREF_ADS_CATEGORY.getString()

    fun setEnableDFPForLive(provider: Boolean) {
        PREF_ENABLE_DFP_FOR_LIVE.put(provider)
    }
    fun getEnableDFPForLive() = PREF_ENABLE_DFP_FOR_LIVE.getBoolean()

    fun setEnableDFPForVOD(provider: Boolean) {
        PREF_ENABLE_DFP_FOR_VOD.put(provider)
    }
    fun getEnableDFPForVOD() = PREF_ENABLE_DFP_FOR_VOD.getBoolean()


    fun setUserSubscribed(userSubscribed: Boolean) {
        PREF_USER_SUBSCRIBED.put(userSubscribed)
    }

    fun getUserSubscribed() = PREF_USER_SUBSCRIBED.getBoolean()

    fun isTVEProviderSpectrum(): Boolean {
        return PREF_TVE_PROVIDER.equals("spectrum", ignoreCase = true)
    }

    /**
     * this method will return customer type > {digital_customer_month/digital_customer_year/tv_customer_tve_id/free}
     */
    fun getCustomerType(): String {
        val loggedIn = getLoggedIn()
        val tveLoggedIn = getTVELoggedIn()
        val userSubscribed = getUserSubscribed()
        return when {
            tveLoggedIn == true -> "${GlobalTVConfig.TV_CUSTOMER}_${getTVEProvider()}"
            loggedIn == true -> {
                // Fetch data from the login API for monthly/year
                // If subscription exists, return "${GlobalTVConfig.DIGITAL_CUSTOMER}_MONTHLY/YEARLY"
                // Otherwise, return "${GlobalTVConfig.DIGITAL_CUSTOMER}_FREE"
                //<Leela will update the Login api for MONTHLY/YEARLY info>
                if(userSubscribed == true){
                    GlobalTVConfig.DIGITAL_CUSTOMER_SUBSCRIBED
                }else {
                    GlobalTVConfig.DIGITAL_CUSTOMER_FREE
                }

            }
            else -> "guest"
        }.also { Timber.d("getCustomerType >>> $it") }
    }


    fun clearData() {
        editor?.clear()
        editor?.commit()
    }

    fun logoutUserDeatil() {
        editor?.apply {
            remove(PREF_LOGGED_IN)
            remove(PREF_TVE_LOGGED_IN)
            remove(PREF_MINIMUM_APP_VERSION)
            remove(PREF_TVE_DEVICE_ID)
            remove(PREF_LOGGED_IN_USER_DATA)
            remove(PREF_USER_ID)
            remove(PREF_TVE_USER_ID)
            remove(PREF_ADS_CATEGORY)
            remove(PREF_TVE_DEVICE_ID)
            remove(PREF_TVE_PROVIDER)
            remove(PREF_ENABLE_DFP_FOR_LIVE)
            remove(PREF_ENABLE_DFP_FOR_VOD)
            remove(PREF_USER_SUBSCRIBED)

            commit()
        }
    }

    fun setSubscriptionData(subscriptionData: APICheckSubDataModel) {
        setUserSubscribed(subscriptionData.isSubscribed())
        setUserSubscribedData(subscriptionData)
    }
}