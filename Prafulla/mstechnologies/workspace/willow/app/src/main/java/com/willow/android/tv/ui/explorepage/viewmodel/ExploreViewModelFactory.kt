package com.willow.android.tv.ui.explorepage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.willow.android.WillowApplication
import com.willow.android.tv.data.repositories.tvconfig.datamodel.NavigationTabsDataModel
import com.willow.android.tv.data.room.db.VideoProgressDao


class ExploreViewModelFactory(
    val application: WillowApplication,
    val data: NavigationTabsDataModel?,
    private val videoProgressDao: VideoProgressDao,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExploreViewModel(application, data,videoProgressDao) as T
    }
}

