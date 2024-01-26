package com.willow.android.tv.common.navmenu

interface NavigationStateListener {
    fun onStateChanged(expanded: Boolean, lastSelected: String?)
}