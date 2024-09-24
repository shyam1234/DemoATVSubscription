package com.malviya.demosubscriptionandroidtv.data

import PRODUCT_ID
import PurchaseState
import android.app.Activity
import android.content.Context
import com.android.billingclient.api.Purchase
import com.malviya.demosubscriptionandroidtv.MyApplication
import com.malviya.demosubscriptionandroidtv.data.utils.GoogleIAPHelper
import com.malviya.demosubscriptionandroidtv.domain.GoogleIAPUserCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class BillingRepository(
    private val context: Context,
    private val activity: Activity,
    private val billingClient: GoogleIAPHelper
) : GoogleIAPUserCases {

    override suspend fun setupBillingClient() {
        billingClient.setupBillingClient(context)
    }

    override suspend fun execute() {
        billingClient.makePurchase(activity, PRODUCT_ID)
    }

    override suspend fun onPurchaseCallback() : Flow<PurchaseState> {
        billingClient.registerCallback(object : GoogleIAPHelper.IAPCallback{
            override fun onPurchaseSuccess(
                responseCode: Int,
                purchaseList: MutableList<Purchase>?
            ) {
              flow{
                  emit(PurchaseState.Success(responseCode, purchaseList))
              }
            }

            override fun onPurchaseFailed(responseCode: Int, message: String?) {
                flow{
                    emit(PurchaseState.Failure(responseCode, message ))
                }
            }

        })
        return flow{
            emit(PurchaseState.Loading(null))
        }
    }

    override fun dispose() {
        billingClient.onDestroyConnection()
    }

    override fun queryPurchases() {
        billingClient.queryPurchaseHistory()
    }

    fun registerCallback(callback: GoogleIAPHelper.IAPCallback) {
        billingClient.registerCallback(callback)
    }
}