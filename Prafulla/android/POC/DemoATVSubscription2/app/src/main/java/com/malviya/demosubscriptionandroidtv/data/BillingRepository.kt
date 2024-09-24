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
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

    override suspend fun onPurchaseCallback(): Flow<PurchaseState> = flow {
        emit(PurchaseState.Loading(null))

        val result = suspendCoroutine<PurchaseState> { continuation ->
            val isResumed = AtomicBoolean(false) // Use AtomicBoolean to track state

            billingClient.registerCallback(object : GoogleIAPHelper.IAPCallback {
                override fun onPurchaseSuccess(
                    responseCode: Int,
                    purchaseList: MutableList<Purchase>?
                ) {
                    if (isResumed.compareAndSet(false, true)) {
                        continuation.resume(PurchaseState.Success(responseCode, purchaseList))
                    }
                }

                override fun onPurchaseFailed(responseCode: Int, message: String?) {
                    if (isResumed.compareAndSet(false, true)) {
                        continuation.resume(PurchaseState.Failure(responseCode, message))
                    }
                }
            })
        }

        emit(result)
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