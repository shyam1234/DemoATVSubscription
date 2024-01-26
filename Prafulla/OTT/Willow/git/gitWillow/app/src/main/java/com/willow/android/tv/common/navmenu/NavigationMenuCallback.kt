package com.willow.android.tv.common.navmenu

import com.willow.android.tv.common.Types

interface NavigationMenuCallback {
    fun navMenuToggle(toShow: Boolean)
    fun navMenuVisibility(visibility: Int)

    fun navMenuScreenName(screenName: Types.ScreenName)
}