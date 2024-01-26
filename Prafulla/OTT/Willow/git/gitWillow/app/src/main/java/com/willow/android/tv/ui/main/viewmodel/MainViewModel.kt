package com.willow.android.tv.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.willow.android.tv.data.repositories.RepositoryFactory
import com.willow.android.tv.data.repositories.mainactivity.datamodel.APICheckSubDataModel
import com.willow.android.tv.data.repositories.tvconfig.datamodel.APITVConfigDataModel
import com.willow.android.tv.ui.main.model.UserModel
import com.willow.android.tv.utils.Actions
import com.willow.android.tv.utils.CommonFunctions.generateMD5Common
import com.willow.android.tv.utils.PrefRepository
import com.willow.android.tv.utils.config.GlobalTVConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(application : Application) : AndroidViewModel(application) {
    private val _fetchTVConfig  = MutableLiveData<APITVConfigDataModel>()
    val fetchTVConfig : LiveData<APITVConfigDataModel?> = _fetchTVConfig

    private val _refreshPageForSubscription  = MutableLiveData<Boolean>()
    val refreshPageForSubscription : LiveData<Boolean?> = _refreshPageForSubscription

    init {
        loadConfiguration()
    }
    private fun loadConfiguration(){
        _fetchTVConfig.postValue(GlobalTVConfig.tvConfig)
    }


    private val prefRepository = PrefRepository(application)

    fun checkSubscription(){
        viewModelScope.launch (Dispatchers.IO){
            callCheckSubscription()
        }
    }


    private suspend fun callCheckSubscription() {
        var isRefreshNeeded = false
        val checkSubData =
            RepositoryFactory.getMainActivityRepository().getCheckSubData(getApplication())
        lateinit var userId: String

        if (prefRepository.getLoggedIn() == true) {
            userId = prefRepository.getUserID()
        } else if (prefRepository.getTVELoggedIn() == true) {
            userId = prefRepository.getTVEUserID()
        }
        val user = UserModel(Actions.CHECK_SUBSCRIPTION.action, userId, generateMD5Common(userId))
        val dataResult = checkSubData.getCheckSubscription(user).data
        if (prefRepository.getUserSubscribed() != dataResult?.isSubscribed()) {
            isRefreshNeeded = true
        }
        saveDataToSharedPref(dataResult)
        if (isRefreshNeeded) {
            _refreshPageForSubscription.postValue(true)
        }
    }

    private fun saveDataToSharedPref(dataResult: APICheckSubDataModel?) {

        dataResult?.apply {
            if(status =="success"){
                Timber.d("CHeck SubAPI")
                prefRepository.apply {

                    if(dataResult.tveUser==1){
                        setTVELoggedIn(true)
                        setTVEUserID(dataResult.tveUserId.toString())
                    }else{
                        setLoggedIn(true)
                        setUserID(dataResult.userId.toString())
                    }
                    setUserSubscribed(dataResult.subscriptionStatus==1)
                }
            }
        }
    }


}