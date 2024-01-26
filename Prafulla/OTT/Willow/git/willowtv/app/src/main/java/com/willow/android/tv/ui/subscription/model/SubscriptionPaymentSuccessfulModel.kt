package com.willow.android.tv.ui.subscription.model

data class SubscriptionPaymentSuccessfulModel(
    var id:String="",
    var plan: String="",
    var transactionID: String="",
    var price: String="",
    var transactionDate: String="",
    var renewalDate: String="",
    var renewalPrice: String="",
    var paymentMethod: String=""
) : java.io.Serializable
