package com.willow.android.tv.ui.playback

/**
 * to handle the event based on the player playback states
 */
interface IPlayerStatus {
        fun onPrepare()
        fun onPlay()
        fun onEnd()
}