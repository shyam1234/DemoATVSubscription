package com.willow.android.tv.ui.subscription.`interface`

interface ISubscriptionListener {
    fun onFocuseSubScripe(discriptionText: List<String>, absoluteAdapterPosition: Int){}
    fun onClickSubScripe(productId: String){}
    fun onPlanChangeClick(planId: String){}
}