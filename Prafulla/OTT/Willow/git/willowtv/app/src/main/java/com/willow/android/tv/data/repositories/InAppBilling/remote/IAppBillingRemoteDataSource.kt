package com.willow.android.tv.data.repositories.InAppBilling.remote

import android.app.Application
import com.willow.android.WillowApplication
import com.willow.android.tv.data.repositories.BaseRemoteDataSource
import com.willow.android.tv.data.repositories.InAppBilling.IInAppBillingRepository
import com.willow.android.tv.data.repositories.mainactivity.datamodel.APICheckSubDataModel
import com.willow.android.tv.ui.subscription.model.InAppReciptModel
import com.willow.android.tv.utils.Resource
import willow.android.tv.data.repositories.IAppBilling.datamodel.IAPOfferModel
import willow.android.tv.data.repositories.IAppBilling.datamodel.IAPReceiptVerificationModel
import willow.android.tv.data.repositories.IAppBilling.datamodel.checkSubscriptionRequest
import javax.inject.Inject


class IAppBillingRemoteDataSource(application: Application) : BaseRemoteDataSource(), IInAppBillingRepository {

    init {
        (application as WillowApplication).applicationComponent.inject(this)
    }

    @Inject
    lateinit var iAppBillingAPIs: IAppBillingAPIs
    override suspend fun getIAppBilling(body: InAppReciptModel): Resource<IAPReceiptVerificationModel> {
        return safeApiCall { iAppBillingAPIs.iAppBillingReceipt(body.userId, body.receipt) }
    }


    override suspend fun inAppBillingOffer(): Resource<IAPOfferModel> {
        return safeApiCall { iAppBillingAPIs.inAppBillingOffer() }
    }

    override suspend fun checkSubscription(checkSubscriptionRequest: checkSubscriptionRequest): Resource<APICheckSubDataModel> {
        return safeApiCall { iAppBillingAPIs.checkSubscription(checkSubscriptionRequest) }
    }

}
