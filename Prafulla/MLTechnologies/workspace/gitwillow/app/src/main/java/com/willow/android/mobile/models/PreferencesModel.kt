package com.willow.android.mobile.models

import com.willow.android.mobile.services.StorageService

object PreferencesModel {
    var showSources: Boolean = false
    var showScores: Boolean = true
    var showResults: Boolean = true
    val items: List<String> = listOf("Show Sources", "Show Scores", "Show Results")

    fun initShowValues() {
        showSources = StorageService.getShowSources()
        showScores = StorageService.getShowScores()
        showResults = StorageService.getShowResults()
    }

    fun updateShowSources(show: Boolean) {
        showSources = show
        StorageService.storeShowSources(show)
    }

    fun updateShowScores(show: Boolean) {
        showScores = show
        StorageService.storeShowScores(show)
    }

    fun updateShowResults(show: Boolean) {
        showResults = show
        StorageService.storeShowResults(show)
    }
}