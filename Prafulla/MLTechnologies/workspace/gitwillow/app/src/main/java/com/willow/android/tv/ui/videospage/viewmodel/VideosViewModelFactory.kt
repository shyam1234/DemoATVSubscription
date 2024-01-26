package com.willow.android.tv.ui.videospage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.willow.android.WillowApplication


class VideosViewModelFactory(
    val application: WillowApplication
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VideosViewModel(application) as T
    }
}

