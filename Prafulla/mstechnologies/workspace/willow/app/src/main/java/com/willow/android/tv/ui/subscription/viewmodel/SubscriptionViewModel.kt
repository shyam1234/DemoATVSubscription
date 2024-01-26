package com.willow.android.tv.ui.subscription.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.willow.android.tv.common.base.BaseAndroidViewModel
import com.willow.android.tv.data.repositories.RepositoryFactory
import com.willow.android.tv.data.repositories.commondatamodel.CommonCardRow
import com.willow.android.tv.ui.subscription.model.InAppReciptModel
import com.willow.android.tv.ui.subscription.model.SubscriptionChangeModel
import com.willow.android.tv.ui.subscription.model.SubscriptionChangePlan
import com.willow.android.tv.ui.subscription.model.SubscriptionPeriodButtonModel
import com.willow.android.tv.utils.LogUtils
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.config.GlobalTVConfig
import kotlinx.coroutines.launch
import willow.android.tv.data.repositories.IAppBilling.datamodel.IAPOfferModel
import willow.android.tv.data.repositories.IAppBilling.datamodel.IAPReceiptVerificationModel

class SubscriptionViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val _renderPage = MutableLiveData<Resource<CommonCardRow>>()
    val renderPage: LiveData<Resource<CommonCardRow>> = _renderPage
    var receiptData = MutableLiveData<IAPReceiptVerificationModel>()
    var offersData = MutableLiveData<IAPOfferModel>()

    fun onClickRestorePurchase(view: View?) {
        // integrate google in-app RestorePurchase
    }

    fun makeSyncAndroidReceiptCall(userId: String, receipt: String) {
        val data = RepositoryFactory.getIAppBillingRepository().getIAppBilling(getApplication())
        viewModelScope.launch {
            data.getIAppBilling(InAppReciptModel(userId, receipt)).data?.apply {
                if (status == "success") {
                    LogUtils.d("mySubscription", "status == success" + data.toString() )
                    receiptData.postValue(this)
                } else {
                    //error screen
                    LogUtils.d("mySubscription", "status != success" + data.toString() )
                }
            }
        }
    }

    fun getOffersData() {
        val data = RepositoryFactory.getIAppBillingRepository().getIAppBilling(getApplication())
        viewModelScope.launch {
            data.inAppBillingOffer().data.apply {
                offersData.postValue(this)
            }
        }
    }

    fun getInAppProductDetails(): List<SubscriptionPeriodButtonModel> {
        return GlobalTVConfig.getInAppProductDetails().map {
            SubscriptionPeriodButtonModel(
                productId = it.productId,
                subAmount = it.productPrice,
                details = it.details,
                subPeriod = it.title,
                subRealAmount = "",
                subSaveAmount = ""
            )
        }
    }

    fun getInAppSubscriptionChangePlanModel(): List<SubscriptionChangePlan> {
        return listOf()
    }

    fun getImageUrl(): String? {
        LogUtils.d(
            "GlobalTVConfig.getSubscriptionBannerImg()",
            GlobalTVConfig.getSubscriptionBannerImg()
        )
        return GlobalTVConfig.getSubscriptionBannerImg()
    }

    fun getChangeSubscriptionModel() = SubscriptionChangeModel("", "", "")
    fun onPlanChange() {

    }
}
