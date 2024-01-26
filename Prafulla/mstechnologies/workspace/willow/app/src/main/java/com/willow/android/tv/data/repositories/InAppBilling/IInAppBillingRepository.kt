package com.willow.android.tv.data.repositories.InAppBilling

import com.willow.android.tv.data.repositories.mainactivity.datamodel.APICheckSubDataModel
import com.willow.android.tv.ui.subscription.model.InAppReciptModel
import com.willow.android.tv.utils.Resource
import willow.android.tv.data.repositories.IAppBilling.datamodel.IAPOfferModel
import willow.android.tv.data.repositories.IAppBilling.datamodel.IAPReceiptVerificationModel
import willow.android.tv.data.repositories.IAppBilling.datamodel.checkSubscriptionRequest

interface IInAppBillingRepository {
    suspend fun getIAppBilling(body: InAppReciptModel): Resource<IAPReceiptVerificationModel>
    suspend fun inAppBillingOffer(): Resource<IAPOfferModel>
    suspend fun checkSubscription(checkSubscriptionRequest: checkSubscriptionRequest): Resource<APICheckSubDataModel>
}