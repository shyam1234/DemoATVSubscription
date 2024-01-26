package com.willow.android.mobile.configs

import org.json.JSONObject

object MessageConfig {
    var cannotMakePayments = "Payment is not allowed on this device"
    var pollerTitle = "Multiple Streaming Error"
    var pollerDescription = "You have started streaming from another device. Willow supports only single stream per authenticated user."
    var needSubscription = "Your account does not have a subscription. Please visit www.willow.tv/subscribe."
    var iapDescription = "Full Access to Willow website, mobile apps & IPTV's \nWatch Live Cricket in HD \nInteractive Scorecard and Commentary \nReplays and Highlights"
    var iapDescriptionDetail = "The Willow premium subscription is available for USD $9.99/month and there is no free trial period associated with the Willow premium subscription. \n \nThe Willow premium subscription will be automatically renewed every month unless auto-renew is turned off at least 24 hours before the end of the current period. \n \nPayment will be charged to Google Account at the confirmation."
    var iapSuccess = "Thank you for purchasing Willow Subscription"
    var iapError = "Could not purchase willow subscription. Please try again or contact our support team."
    var geoblock = "Willow app is not available in your region."
    var playbackFailMsg = "Could not play the video. Please try again later."
    var matchNotFound = "Could not get match details. Please try again later."

    var passwordResetFail = "Could not reset the password."
    var emptyCredentials = "Please provide all details"
    var wrongEmail = "Please provide valid email address"
    var passwordValidation = "& character is not allowed in the password field"
    var willowAccountNotFound = "Could not find the valid willow account"
    var wrongCredentials = "Please provide valid details"
    var signInToWatch = "Please sign in to watch"
    var subscribeToWatch = "Please subscribe to watch"
    var emptyCoupon = "Please enter the coupon code"
    var wrongCoupon = "Please provide the valid code"
    var appleSigninFailure = "Apple Sign in Failure"
    var inputEmail = "Please enter your email"
    var inputPassword = "Please provide your willow password to continue"
    var createAccount = "Your account doesn't exist with Willow. Please sign up to create a new account."
    var loginAccount = "Please login with your willow account"
    var logoutDecision = "Are you sure you want to Logout?"
    var subscriptionTitle = "Willow Premium Subscription"
    var subscriptionSubtitle = "Subscription valid till: "
    var deleteAccountTitle = "Are you sure you want to permanently delete your account?"
    var deleteAccountSubtitle = "You account will be deleted immediately. This action cannot be undone. \n \n If you have an active subscription with us, Please cancel the subscription before you delete your account permanently, to avoid billing issues."
    var deleteAccountTerms = "Yes, I want to permanently delete this Willow account."
    var deleteAccountConsent = "Please confirm by selecting the checkbox."
    var logoutSuccess = "Logout Success"

    var tveLoginInstruction = "Time Warner Cable and Bright House Networks subscribers may choose Spectrum to sign in"

    var emptyDataError = "No data available"
    var emptyFixturesError = "No matches scheduled. Please stay tuned."
    var tveOnlyMessage = "TV EVERYWHERE ONLY"
    var copyrightMessage = "2019.Times Internet (UK) Ltd. All rights reserved."
    var castingMessage = "Casting to TV"
    var enableNotification = "Please allow notifications from your device Settings."

    var newAppUpdateAvaialble = "A new version of Willow app is available. Please update it from App Store."

    var privacyPolicyUrl = "https://www.willow.tv/EventMgmt/PrivacyPolicy.asp?render=mobile_apps"
    var termsOfUseUrl = "https://www.willow.tv/EventMgmt/termsofuse.asp?render=mobile_apps"
    var videoNotFound = "Video Not Found!"
    var noMatchToday = "No Match Found!"
    var noDataFound = "No Data Found!"
    val unpredictedError = "Something Went Wrong!"

    fun setCloudData(messagesData: JSONObject) {
        try {
            val data = messagesData.getJSONObject("messages")

            try {
                cannotMakePayments = data.getString("cannotMakePayments")
            } catch (e: Exception)  {}

            try {
                pollerTitle = data.getString("pollerTitle")
            } catch (e: Exception)  {}

            try {
                pollerDescription = data.getString("pollerDescription")
            } catch (e: Exception) {}

            try {
                needSubscription = data.getString("needSubscription")
            } catch (e: Exception) {}

            try {
                iapDescription = data.getString("iapDescription")
            } catch (e: Exception)  {}

            try {
                iapDescriptionDetail = data.getString("iapDescriptionDetail")
            } catch (e: Exception)  {}
            try {
                iapSuccess = data.getString("iapSuccess")
            } catch (e: Exception)  {}

            try {
                iapError = data.getString("iapError")
            } catch (e: Exception)  {}

            try {
                geoblock = data.getString("geoblock")
            } catch (e: Exception)  {}

            try {
                playbackFailMsg = data.getString("playbackFailMsg")
            } catch (e: Exception)  {}

            try {
                matchNotFound = data.getString("matchNotFound")
            } catch (e: Exception)  {}

            try {
                passwordResetFail = data.getString("passwordResetFail")
            } catch (e: Exception)  {}

            try {
                emptyCredentials = data.getString("emptyCredentials")
            } catch (e: Exception)  {}

            try {
                wrongEmail = data.getString("wrongEmail")
            } catch (e: Exception)  {}

            try {
                passwordValidation = data.getString("passwordValidation")
            } catch (e: Exception)  {}

            try {
                willowAccountNotFound = data.getString("willowAccountNotFound")
            } catch (e: Exception)  {}

            try {
                wrongCredentials = data.getString("wrongCredentials")
            } catch (e: Exception)  {}

            try {
                signInToWatch = data.getString("signInToWatch")
            } catch (e: Exception)  {}

            try {
                subscribeToWatch = data.getString("subscribeToWatch")
            } catch (e: Exception)  {}

            try {
                emptyCoupon = data.getString("emptyCoupon")
            } catch (e: Exception)  {}

            try {
                wrongCoupon = data.getString("wrongCoupon")
            } catch (e: Exception)  {}

            try {
                appleSigninFailure = data.getString("appleSigninFailure")
            } catch (e: Exception)  {}

            try {
                inputEmail = data.getString("inputEmail")
            } catch (e: Exception)  {}

            try {
                inputPassword = data.getString("inputPassword")
            } catch (e: Exception)  {}

            try {
                createAccount = data.getString("createAccount")
            } catch (e: Exception)  {}

            try {
                loginAccount = data.getString("loginAccount")
            } catch (e: Exception)  {}

            try {
                logoutSuccess = data.getString("logoutSuccess")
            } catch (e: Exception)  {}

            try {
                logoutDecision = data.getString("logoutDecision")
            } catch (e: Exception)  {}

            try {
                subscriptionTitle = data.getString("subscriptionTitle")
            } catch (e: Exception)  {}

            try {
                subscriptionSubtitle = data.getString("subscriptionSubtitle")
            } catch (e: Exception)  {}

            try {
                deleteAccountTitle = data.getString("deleteAccountTitle")
            } catch (e: Exception)  {}

            try {
                deleteAccountSubtitle = data.getString("deleteAccountSubtitle")
            } catch (e: Exception)  {}

            try {
                deleteAccountTerms = data.getString("deleteAccountTerms")
            } catch (e: Exception)  {}

            try {
                deleteAccountConsent = data.getString("deleteAccountConsent")
            } catch (e: Exception)  {}

            try {
                emptyDataError = data.getString("emptyDataError")
            } catch (e: Exception)  {}

            try {
                emptyFixturesError = data.getString("emptyFixturesError")
            } catch (e: Exception)  {}

            try {
                tveOnlyMessage = data.getString("tveOnlyMessage")
            } catch (e: Exception)  {}

            try {
                copyrightMessage = data.getString("copyrightMessage")
            } catch (e: Exception)  {}

            try {
                castingMessage = data.getString("castingMessage")
            } catch (e: Exception)  {}

            try {
                enableNotification = data.getString("enableNotification")
            } catch (e: Exception)  {}

            try {
                newAppUpdateAvaialble = data.getString("newAppUpdateAvaialble")
            } catch (e: Exception)  {}

            try {
                privacyPolicyUrl = data.getString("privacyPolicyUrl")
            } catch (e: Exception)  {}

            try {
                termsOfUseUrl = data.getString("termsOfUseUrl")
            } catch (e: Exception)  {}
        } catch (e: Exception)  {}


    }
}