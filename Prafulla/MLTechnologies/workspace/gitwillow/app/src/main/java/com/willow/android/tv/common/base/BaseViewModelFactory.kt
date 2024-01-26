package com.willow.android.tv.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.willow.android.WillowApplication


open class BaseViewModelFactory(
    val application: WillowApplication
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BaseAndroidViewModel(application) as T
    }
}

