package com.willow.android.tv.ui.subscription.model

data class SubscriptionChangePlan(
    val plan1:String,
    val plan2:String,
    val plan3:String,
    val plan4:String,
    val planDuaration:String,
    val planAmt:String,
    val planUd:String
)

data class SubscriptionChangeModel(
    var oldPlan :String,
    var newPlan :String,
    var planTerm :String,
)
