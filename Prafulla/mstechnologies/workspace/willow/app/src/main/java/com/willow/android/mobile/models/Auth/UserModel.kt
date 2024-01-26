package com.willow.android.mobile.models.auth

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.willow.android.mobile.configs.Keys
import com.willow.android.mobile.configs.WiConfig
import com.willow.android.mobile.services.ReloadService
import com.willow.android.mobile.services.StorageService
import com.willow.android.mobile.services.analytics.AnalyticsService
import com.willow.android.mobile.utils.sha256
import com.willow.android.mobile.views.pages.tVELoginPage.TVELoginService

object UserModel {
    var userId: String = ""
    var email: String = ""
    var name: String = ""
    var tveProvider: String = ""
    var cc: String = "NA"

    var isGoogleUser: Boolean = false
    var isAppleUser: Boolean = false
    var isTVEUser: Boolean = false
    var isSubscribed: Boolean = false
    var emailVerified: Boolean = false
    var nextRenewalDate: String = ""

    var googleUserId: String = ""
    var googleIDToken: String = ""
    var googleFullName: String = ""
    var googleGivenName: String = ""
    var googleFamilyName: String = ""

    var iapProductId = ""
    var iapProductPrice = ""
    var iapCurrency = ""
    var iapSubscriptionType = ""

    var displayTitle: String = "Sign In / Create Account"
    var displaySubtitle: String = "or Login with TV provider"

    var ads_category: String = "free"

    var shouldShowRequestPasswordOption = true
    

    fun initStoredProperties() {
        userId = StorageService.getUserId()
        email = StorageService.getUserEmail()
        ads_category = StorageService.getUserAdsCategory()
        isGoogleUser = StorageService.getGoogleUser()
        isAppleUser = StorageService.getAppleUser()
        isTVEUser = StorageService.getTVEUser()

        if (isLoggedIn()) {
            displayTitle = email
            displaySubtitle = "My Account"
        }

        if (isTVEUser) {
            tveProvider = StorageService.getTVEProvider()
            displayTitle = tveProvider
            isSubscribed = true
            shouldShowRequestPasswordOption = false
        }
    }

    fun setId(id: String) {
        userId = id
        StorageService.storeUserId(id)
    }

    fun setTVEProvider(providerValue: String) {
        tveProvider = providerValue
        shouldShowRequestPasswordOption = false
        displayTitle = tveProvider
        displaySubtitle = ""
        StorageService.storeTVEProvider(providerValue)
    }

    fun getUserIdValue(): String {
        if (userId.isEmpty()) {
            return "0"
        }
        return userId
    }

    fun setEmailValue(email: String) {
        displayTitle = email
        displaySubtitle = "My Account"
        UserModel.email = email
        StorageService.storeUserEmail(email)
    }

    fun setAdsCategory(ads_category: String) {
        UserModel.ads_category = ads_category
        StorageService.storeUserAdsCategory(ads_category)
    }

    fun setTVEUserValue(tveUserValue: Boolean) {
        isTVEUser = tveUserValue
        StorageService.storeTVEUser(true)
    }

    fun setNameValue(name: String) {
        UserModel.name = name
    }

    fun setCountryCode(countryCode: String) {
        if ((cc != countryCode) && (cc != "NA")) {
            ReloadService.reloadAppBecauseOfConfig()
        }

        cc = countryCode.lowercase().trim()
        if (cc == "us") {
            iapProductId = WiConfig.US_IAPProductId
            iapProductPrice = WiConfig.US_IAPPrice
            iapCurrency = WiConfig.US_IAPCurrency
            iapSubscriptionType = WiConfig.US_IAPSubscriptionType
        } else if (cc == "ca") {
            iapProductId = WiConfig.CA_IAPProductId
            iapProductPrice = WiConfig.CA_IAPPrice
            iapCurrency = WiConfig.CA_IAPCurrency
            iapSubscriptionType = WiConfig.CA_IAPSubscriptionType
        }
    }


    fun setCheckSubscriptionRespData(data: CheckSubscriptionUserModel) {
        if ( data.subscriptionStatus == 1 ) {
            isSubscribed = true
        }

        if (data.emailVerified == 1) {
            emailVerified = true
        }

        nextRenewalDate = data.nextRenewalDate
    }

    fun setLoginResponseData(data: LoginResponseResultModel) {
        setId(data.userId.toString())
        setEmailValue(data.email)
        setAdsCategory(data.ads_category)

        if (data.subscriptionStatus == 1) {
            isSubscribed = true
        }

        if (data.emailVerified == 1) {
            emailVerified = true
        }

        nextRenewalDate = data.nextRenewalDate
        loginSuccess()
    }

    fun setTVELoginResponseData(data: TVELoginResponseModel) {
        setId(data.userId.toString())
        setEmailValue(data.email)
        setTVEProvider(data.Provider)
        setAdsCategory(data.ads_category)

        setTVEUserValue(true)

        if (data.subscriptionStatus == 1) {
            isSubscribed = true
        }
    }

    fun setGoogleLoginResponseData(email: String, data: LoginResponseResultModel) {
        setLoginResponseData(data)
        setEmailValue(email)

        storeGoogleUser(isTrue = true)
        loginSuccess()
    }

    fun setAppleLoginResponseData(url: String) {
        val decodedUrl = Uri.decode(url)
        val uri = Uri.parse(decodedUrl)

        val status = uri.getQueryParameter("status")
        val subscriptionStatus = uri.getQueryParameter("subscriptionStatus")
        val userIdValue = uri.getQueryParameter("userId")
        val email = uri.getQueryParameter("email")
        val ads_category = uri.getQueryParameter("ads_category")
        val emailVerifiedValue = uri.getQueryParameter("emailVerified")
        val nextRenewalDateValue = uri.getQueryParameter("nextRenewalDate")


        if (status.equals("success", true)) {
            if (userIdValue != null) {
                setId(userIdValue)
            }

            if (email != null) {
                setEmailValue(email)
            }

            if (ads_category != null) {
                setAdsCategory(ads_category)
            }

            if ( subscriptionStatus.equals("1", true)) {
                isSubscribed = true
            }

            if (emailVerifiedValue.equals("1", true)) {
                emailVerified = true
            }

            if (nextRenewalDateValue != null) {
                nextRenewalDate = nextRenewalDateValue
            }


            val code = uri.getQueryParameter("code")
            val id_token = uri.getQueryParameter("id_token")

            storeAppleUser(isTrue = true)
            loginSuccess()
        }
    }

    fun storeGoogleUser(isTrue: Boolean) {
        isGoogleUser = isTrue
        StorageService.storeGoogleUser(isGoogleUser)
    }

    fun storeAppleUser(isTrue: Boolean) {
        isAppleUser = isTrue
        StorageService.storeAppleUser(isAppleUser)
    }

    fun isLoggedIn(): Boolean {
        if (userId != "") {
            return true
        }
        return false
    }

    fun isTVEProviderSpectrum(): Boolean {
        if (tveProvider.equals("spectrum", true)) {
            return true
        }
        return false
    }

    fun loginSuccess() {
        ReloadService.reloadAllScreens()
        AnalyticsService.sendUserLoginEvent()
    }

    fun logout(context: Context) {
        ReloadService.reloadAllScreens()
        AnalyticsService.sendUserLogoutEvent()

        if (isGoogleUser) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
            mGoogleSignInClient.signOut()
                .addOnCompleteListener {
                    Log.d("GoogleLogin", "Logout Success")
                }
        }

        if (isAppleUser) {

        }

        if (isTVEUser) {
            TVELoginService.logout()
        }

        StorageService.clearData()

        userId = ""
        email = ""
        name = ""
        tveProvider = ""
        isGoogleUser = false
        isAppleUser = false
        isTVEUser = false
        isSubscribed = false
        emailVerified = false
        nextRenewalDate = ""

        googleUserId = ""
        googleIDToken = ""
        googleFullName = ""
        googleGivenName = ""
        googleFamilyName = ""

        displayTitle = "Sign In / Create Account"
        displaySubtitle = "or Login with TV provider"

        ads_category = "free"
        shouldShowRequestPasswordOption = true
    }

    fun getEncryptedUserId(): String {
        val encryptedUserId = (userId + Keys.sha256Key).sha256()
        return encryptedUserId
    }
}

