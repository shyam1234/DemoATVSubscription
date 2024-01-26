package com.willow.android.tv.utils

import android.app.Application
import android.content.Context
import android.text.TextUtils
import com.willow.android.R
import com.willow.android.tv.data.repositories.loginpage.datamodel.Result
import com.willow.android.tv.data.repositories.mainactivity.datamodel.APICheckSubDataModel
import com.willow.android.tv.ui.accountDetails.model.AccountDetailsModel
import com.willow.android.tv.utils.GlobalConstants.ApiConstant.USER_IS_SUBSCRIBED

object UserDetailsHelper {
    private fun getFullName(context: Context?, user: Result?): String? {
        val userDetails = user?.game_on_request?.post_params
        return if (userDetails != null && !TextUtils.isEmpty(userDetails.first_name?.trim())) {
            "${userDetails.first_name?.trim()} ${userDetails.last_name?.trim()}"
        } else context?.resources?.getString(R.string.optional)
    }

    private fun getSubscriptionDetailText(context: Context?, userDetails: Result?): String? {
        return if (getIsTvUser(context)) {
            return PrefRepository(context).getTVEProvider()
        } else if (userDetails?.subscriptionStatus == USER_IS_SUBSCRIBED) {
            val nextRenewalDate = userDetails.nextRenewalDate
            if (TextUtils.isEmpty(nextRenewalDate))
                context?.resources?.getString(R.string.subscripe_active)
            else "${context?.resources?.getString(R.string.subscripe_till)} $nextRenewalDate"
        } else {
            context?.getString(R.string.Unsubscribed)
        }
    }

    fun getAccountDetails(context: Context?): AccountDetailsModel {
        val uSerDetails = getUSerDetails(context)
        return AccountDetailsModel(
            userId = uSerDetails?.userId,
            fullName = getFullName(context, uSerDetails),
            emailID = uSerDetails?.email,
            subscriptionDetails = getSubscriptionDetailText(context, uSerDetails),
            subscriptionStatus = isUserSubscribed(uSerDetails),
            isUserLogin = (PrefRepository(context).getLoggedIn() == true
                    || PrefRepository(context).getTVELoggedIn() == true),
            isTvuser = getIsTvUser(context),
            tvuserId = PrefRepository(context).getTVEUserID()
        )
    }

    private fun getIsTvUser(context: Context?) =
        PrefRepository(context).getTVELoggedIn() ?: false

    private fun isUserSubscribed(uSerDetails: Result?) =
        uSerDetails?.subscriptionStatus == USER_IS_SUBSCRIBED


    private fun getUSerDetails(context: Context?): Result? {
        return PrefRepository(context).getUserDetails()
    }

    fun getUSerDetailsId(context: Context?): String? {
        return if (PrefRepository(context).getLoggedIn() == true) {
            PrefRepository(context).getUserID()
        } else if (PrefRepository(context).getTVELoggedIn() == true) {
            PrefRepository(context).getTVEUserID()
        } else null
    }

    fun logout(context: Context?) {
        PrefRepository(context).logoutUserDeatil()
    }

    fun isSubscriptionUpdated(context: Context, subscriptionData: APICheckSubDataModel): Boolean {
        val isDataChanged =
            PrefRepository(context).getUserSubscribed() == subscriptionData.isSubscribed()
        if (isDataChanged || getSubscriptionData(context) == null)
            setSubscriptionData(context, subscriptionData)
        return isDataChanged
    }

    private fun getSubscriptionData(context: Context?) = PrefRepository(context).getUserSubscribedData()

    private fun setSubscriptionData(context: Context?, subscriptionData: APICheckSubDataModel) {
        PrefRepository(context).setSubscriptionData(subscriptionData)
    }

    fun isSubscriptionUpdated(context: Application, subscriptionData: APICheckSubDataModel): Boolean {
        val isNoDataChanged =
            getSubscriptionData(context)?.equals(subscriptionData)
        if (isNoDataChanged == false || getSubscriptionData(context) == null)
            setSubscriptionData(context, subscriptionData)
        return isNoDataChanged == false
    }
}