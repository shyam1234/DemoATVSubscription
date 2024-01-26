package com.willow.android.tv.ui.resultspage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.willow.android.WillowApplication
import com.willow.android.tv.data.repositories.tvconfig.datamodel.NavigationTabsDataModel


class ResultsViewModelFactory(
    val application: WillowApplication,
    val data: NavigationTabsDataModel?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ResultsViewModel(application, data) as T
    }
}

