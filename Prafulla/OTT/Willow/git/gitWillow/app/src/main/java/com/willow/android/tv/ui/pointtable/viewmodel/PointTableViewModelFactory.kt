package com.willow.android.tv.ui.pointtable.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.willow.android.WillowApplication
import com.willow.android.tv.data.repositories.tvconfig.datamodel.NavigationTabsDataModel

class PointTableViewModelFactory (
    val application: WillowApplication,
    val data: NavigationTabsDataModel? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PointTableViewModel(application) as T
    }
}