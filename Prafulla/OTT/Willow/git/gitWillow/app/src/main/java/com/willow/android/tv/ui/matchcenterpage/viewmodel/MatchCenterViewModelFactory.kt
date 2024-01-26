package com.willow.android.tv.ui.matchcenterpage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.willow.android.WillowApplication


class MatchCenterViewModelFactory(
    val application: WillowApplication
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MatchCenterViewModel(application) as T
    }
}

