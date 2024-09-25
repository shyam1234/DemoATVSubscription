package com.malviya.demosubscriptionandroidtv.data

import PRODUCT_ID
import PurchaseState
import android.app.Activity
import android.content.Context
import com.malviya.demosubscriptionandroidtv.data.utils.GoogleIAPHelper
import com.malviya.demosubscriptionandroidtv.domain.GoogleIAPUserCases
import kotlinx.coroutines.flow.Flow

class BillingRepository(
    private val context: Context,
    private val billingClient: GoogleIAPHelper
) : GoogleIAPUserCases {

    override suspend fun setupBillingClient() {
        billingClient.setupBillingClient(context)
    }

    override suspend fun execute(activity: Activity) {
        billingClient.makePurchase(activity, PRODUCT_ID)
    }

    override fun onPurchaseCallback(): Flow<PurchaseState> {
        return billingClient.purchaseUpdates
    }

    override fun dispose() {
        billingClient.onDestroyConnection()
    }

    override fun queryPurchases() {
        billingClient.queryPurchaseHistory()
    }
}
