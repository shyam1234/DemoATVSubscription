package com.willow.android.mobile.services

object ReloadService {
    var reloadHome = false
    var reloadVideos = false
    var reloadFixtures = false
    var reloadResults = false
    var reloadMatchCenter = false
    var reloadSettings = false

    /**
     * This flag should always be false.
     * It should be true only if Auth Screen is launched from the video Detail Page
     */
    var reloadVideoDetail = false

    fun reloadAllScreens() {
        reloadHome = true
        reloadVideos = true
        reloadFixtures = true
        reloadResults = true
        reloadMatchCenter = true
        reloadSettings = true
    }

    fun dontReloadScreens() {
        reloadHome = false
        reloadVideos = false
        reloadFixtures = false
        reloadResults = false
        reloadMatchCenter = false
    }

    fun reloadAppBecauseOfConfig() {
        reloadAllScreens()
    }
}