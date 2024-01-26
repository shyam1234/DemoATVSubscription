package com.willow.android.tv.data.repositories.InAppBilling.remote

import com.willow.android.tv.data.repositories.mainactivity.datamodel.APICheckSubDataModel
import retrofit2.Response
import retrofit2.http.*
import willow.android.tv.data.repositories.IAppBilling.datamodel.IAPOfferModel
import willow.android.tv.data.repositories.IAppBilling.datamodel.IAPReceiptVerificationModel
import willow.android.tv.data.repositories.IAppBilling.datamodel.checkSubscriptionRequest


interface IAppBillingAPIs {

    @POST("sync_android_receipt")
    @FormUrlEncoded
    suspend fun iAppBillingReceipt(
        @Field("user_id") userId: String,
        @Field("receipt") receipt: String
    ): Response<IAPReceiptVerificationModel>

    @GET("MobileV4/IAPOffers.json")
    suspend fun inAppBillingOffer(): Response<IAPOfferModel>

    @POST("EventMgmt/webservices/mobi_auth.asp")
    suspend fun checkSubscription(@Body checkSubscriptionRequest: checkSubscriptionRequest): Response<APICheckSubDataModel>
}