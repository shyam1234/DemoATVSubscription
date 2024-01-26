package com.willow.android.tv.utils.events

import android.view.KeyEvent

/**
 * Created by eldhosepaul on 02/03/23.
 */
data class KeyPressedEvent(
    val keyCode: Int,
    val keyEvent: KeyEvent?
)