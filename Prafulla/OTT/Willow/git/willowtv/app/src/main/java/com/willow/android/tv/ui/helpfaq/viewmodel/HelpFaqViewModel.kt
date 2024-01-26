package com.willow.android.tv.ui.helpfaq.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.willow.android.tv.common.base.BaseAndroidViewModel
import com.willow.android.tv.data.repositories.RepositoryFactory
import com.willow.android.tv.data.repositories.helpfaq.datamodel.ApiHelpDataModel
import com.willow.android.tv.data.repositories.helpfaq.datamodel.Setting
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.config.GlobalTVConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HelpFaqViewModel(application: Application) :
    BaseAndroidViewModel(application) {

    private val _renderPage = MutableLiveData<Resource<ApiHelpDataModel>>()
    val renderPage: LiveData<Resource<ApiHelpDataModel>> = _renderPage
    val settingTitle = MutableLiveData<String>()
    val tvProviderUrl = MutableLiveData<String?>()
    val settingDescription = MutableLiveData<String>()


    init {
        loadHelpPageData()
    }

    private fun loadHelpPageData() {
        viewModelScope.launch(Dispatchers.IO) {
            _renderPage.postValue(Resource.Loading())
            fetchHelpPageData()
        }
    }

    fun showHideTveProviderImage(setting: Setting) {
        if (setting.title == ABOUT_US)
            tvProviderUrl.value = fetchTVConfig.value?.tveProviders
        else
            tvProviderUrl.value = ""
    }

    private suspend fun fetchHelpPageData() {
        val data = RepositoryFactory.getHelpFaqPageRepository().helpFaqPageData(application = getApplication())
        _renderPage.postValue(data.getHelpPageData(GlobalTVConfig.getHelpAndFAQUrl()))
    }

    companion object {
        const val ABOUT_US = "About Us"
    }


}