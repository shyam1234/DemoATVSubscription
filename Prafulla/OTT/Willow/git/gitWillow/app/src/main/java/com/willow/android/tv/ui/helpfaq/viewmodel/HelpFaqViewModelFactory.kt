package com.willow.android.tv.ui.helpfaq.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.willow.android.WillowApplication
import com.willow.android.tv.data.repositories.tvconfig.datamodel.NavigationTabsDataModel

class HelpFaqViewModelFactory(
    val application: WillowApplication,
    val data: NavigationTabsDataModel?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HelpFaqViewModel(application) as T
    }
}