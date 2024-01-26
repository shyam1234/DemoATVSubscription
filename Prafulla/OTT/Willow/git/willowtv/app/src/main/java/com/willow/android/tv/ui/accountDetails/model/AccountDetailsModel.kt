package com.willow.android.tv.ui.accountDetails.model

data class AccountDetailsModel(
    var userId: Int?,
    var tvuserId: String?,
    var fullName: String?,
    var emailID: String?,
    var subscriptionDetails: String?,
    var subscriptionStatus: Boolean?,
    var isUserLogin: Boolean?,
    var isTvuser:Boolean
)
