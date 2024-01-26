package com.willow.android.tv.utils

enum class Actions(val action:String) {
    LOGIN("login"),
    REGISTER("register"),
    CHECK_SUBSCRIPTION("checkSubscription"),
    CHECK_ACCOUNT("checkAccount"),
    FG_PASSWORD("fgpassword"),
}


enum class GoTo{
    LOGIN,
    SUBSCRIPTION,
    PLAY_VIDEO
}