package com.willow.android.tv.common.base

import android.app.Application
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.willow.android.tv.data.repositories.RepositoryFactory
import com.willow.android.tv.data.repositories.tvconfig.datamodel.APITVConfigDataModel
import com.willow.android.tv.utils.CommonFunctions
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.ImageUtility
import com.willow.android.tv.utils.UserDetailsHelper
import com.willow.android.tv.utils.config.GlobalTVConfig
import com.willow.android.tv.utils.events.RefreshActivityEvent
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import willow.android.tv.data.repositories.IAppBilling.datamodel.checkSubscriptionRequest

open class BaseAndroidViewModel(application: Application) : AndroidViewModel(application) {
    fun getString(resourceId: Int) = getApplication<Application>().resources.getString(resourceId)


    private val _fetchTVConfig = MutableLiveData<APITVConfigDataModel>()
    val fetchTVConfig: LiveData<APITVConfigDataModel> = _fetchTVConfig

    private val _refreshActivity = MutableLiveData<Boolean>()
    val refreshActivity: LiveData<Boolean> = _refreshActivity

    protected val _errorPageShow = MutableLiveData<ErrorType>()
    val errorPageShow: LiveData<ErrorType> = _errorPageShow

    init {
        loadConfiguration()
    }

    private fun loadConfiguration() {
        _fetchTVConfig.postValue(GlobalTVConfig.tvConfig)
    }

    fun checkSubscription() {
        val data = RepositoryFactory.getIAppBillingRepository().getIAppBilling(getApplication())
        viewModelScope.launch {
            val uid = UserDetailsHelper.getUSerDetailsId(
                getApplication<Application>().baseContext
            )
            uid?.let {
                FirebaseCrashlytics.getInstance().setUserId("$uid")
                data.checkSubscription(
                    checkSubscriptionRequest(
                        uid = it,
                        authToken = CommonFunctions.generateMD5Common(it)
                    )
                ).data?.apply {
                    EventBus.getDefault().post(
                        RefreshActivityEvent(
                        UserDetailsHelper.isSubscriptionUpdated(getApplication(),this)
                    )
                    )
                }
            }
        }
    }


    companion object {
        @JvmStatic
        @BindingAdapter("loadImage")
        fun loadImage(view: ImageView, imageUrl: String?) {
            if (!imageUrl.isNullOrBlank()) {
                ImageUtility.loadImageInto(imageUrl,view)
            }
        }
    }
}