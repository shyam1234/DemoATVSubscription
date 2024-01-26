package com.willow.android.tv.ui.accountDetails.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.willow.android.WillowApplication
import com.willow.android.tv.data.room.db.AppDatabase


class AccountDetailsViewModelFactory(
    val application: WillowApplication,
    private val appDatabase: AppDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AccountDetailsViewModel(application, appDatabase) as T
    }
}

