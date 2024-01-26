package com.willow.android.tv.ui.subscription.model



data class SubscriptionPeriodButtonModel(
    var productId: String?,
    var subPeriod: String?,
    var subAmount: String?,
    var subRealAmount: String?,
    val details: List<String>,
    var subSaveAmount: String?
)
