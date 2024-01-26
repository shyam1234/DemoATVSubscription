package com.willow.android.tv.common.cards

import android.view.KeyEvent
import android.view.View

interface KeyListener {
    fun onKey(view: View?, keyCode: Int,  event: KeyEvent?): Boolean
}