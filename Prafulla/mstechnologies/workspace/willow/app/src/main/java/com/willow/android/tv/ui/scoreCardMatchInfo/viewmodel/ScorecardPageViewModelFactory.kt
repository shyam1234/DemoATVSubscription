package com.willow.android.tv.ui.scoreCardMatchInfo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.willow.android.WillowApplication


class ScorecardPageViewModelFactory(
    val application: WillowApplication
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ScorecardPageViewModel(application) as T
    }
}

