package com.malviya.demosubscriptionandroidtv.domain

import PurchaseState
import android.app.Activity
import com.android.billingclient.api.Purchase
import com.malviya.demosubscriptionandroidtv.data.BillingRepository
import com.malviya.demosubscriptionandroidtv.data.utils.GoogleIAPHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface GoogleIAPUserCases {
    suspend fun setupBillingClient()
    suspend fun execute(activity : Activity)

    fun onPurchaseCallback() : Flow<PurchaseState>
    fun dispose()
    fun queryPurchases()
}

class GoogleIAPUserCasesImpl(
    private val billingRepository: BillingRepository
): GoogleIAPUserCases{
    override suspend fun setupBillingClient() {
        billingRepository.setupBillingClient()
    }

    override suspend fun execute(activity: Activity) {
        billingRepository.execute(activity)
    }

    override fun onPurchaseCallback() = billingRepository.onPurchaseCallback()

    override fun dispose() {
        billingRepository.dispose()
    }

    override fun queryPurchases() {
        billingRepository.queryPurchases()
    }

}