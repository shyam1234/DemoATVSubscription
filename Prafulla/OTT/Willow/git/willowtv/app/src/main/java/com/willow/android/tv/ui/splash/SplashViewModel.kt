package com.willow.android.tv.ui.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.willow.android.tv.data.repositories.RepositoryFactory
import com.willow.android.tv.data.repositories.tvconfig.datamodel.APIDFPConfigDataModel
import com.willow.android.tv.data.repositories.tvconfig.datamodel.APITVConfigDataModel
import com.willow.android.tv.utils.GlobalConstants
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.config.GlobalTVConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


class SplashViewModel(application: Application): AndroidViewModel(application) {
    private var context: Application
    private val _fetchTVConfig  = MutableLiveData<Resource<APITVConfigDataModel>>()
    val fetchTVConfig : LiveData<Resource<APITVConfigDataModel>> = _fetchTVConfig

    private val _fetchDFPConfig  = MutableLiveData<Resource<APIDFPConfigDataModel>>()
    val fetchDFPConfig : LiveData<Resource<APIDFPConfigDataModel>> = _fetchDFPConfig

    private val _fetchCountryCode  = MutableLiveData<Resource<String>>()
    val fetchCountryCode : LiveData<Resource<String>> = _fetchCountryCode

    init {
        context = application
        loadConfiguration()
    }

    private fun loadConfiguration(){
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.IO) { fetchTVConfig() }
            withContext(Dispatchers.IO) { getDfpConfigData() }
            withContext(Dispatchers.IO) { getCountryCode() }
        }
    }

    private suspend fun fetchTVConfig() {
        //fetch from remote once TVConfig has screenType
        _fetchTVConfig.postValue(Resource.Loading())
        val tvConfigRepository = RepositoryFactory.getTVConfigRepository().getTVConfigData(getApplication(), true)
        var response  = tvConfigRepository.getTVConfig()

        if (response is Resource.Error) {
            response = RepositoryFactory.getTVConfigRepository().getTVConfigData(getApplication(), false).getTVConfig()
        }
        _fetchTVConfig.postValue(response)
    }

    private suspend fun getDfpConfigData() {
        _fetchDFPConfig.postValue(Resource.Loading())
        val tvConfigRepository = RepositoryFactory.getTVConfigRepository().getDFPConfigData(getApplication(), true)

        var response  = tvConfigRepository.getDfpConfig(GlobalTVConfig.getDFPConfigUrl())

        if (response is Resource.Error) {
            response = RepositoryFactory.getTVConfigRepository().getDFPConfigData(getApplication(), false).getDfpConfig(GlobalTVConfig.getDFPConfigUrl())
        }
        _fetchDFPConfig.postValue(response)
    }

    private suspend fun getCountryCode() {
        _fetchCountryCode.postValue(Resource.Loading())

        val tvConfigRepository = RepositoryFactory.getTVConfigRepository().getCountryCode(getApplication(), true)

        var config  = tvConfigRepository.getCountryConfig(GlobalTVConfig.getCountryCheckEndPoint())

        if (config is Resource.Error) {
            config = RepositoryFactory.getTVConfigRepository().getCountryCode(getApplication(), false).getCountryConfig(GlobalTVConfig.getCountryCheckEndPoint())
        }

        _fetchCountryCode.postValue(config)

    }


    fun isGeoBlocked() :Boolean {
        val flag = !GlobalConstants.ALLOWED_COUNTRIES.contains(GlobalTVConfig.country, ignoreCase = true)
        Timber.d("country >> isGeoBlocked >> $flag >> country ${GlobalTVConfig.country}")
        if(GlobalConstants.bypassCountryCheck){
            return false
        }
        return flag
    }
}