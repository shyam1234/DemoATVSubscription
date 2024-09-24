package com.malviya.demosubscriptionandroidtv.data.utils

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.ProductType.SUBS
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import com.malviya.demosubscriptionandroidtv.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class GoogleIAPHelper : PurchasesUpdatedListener {
    private var callback: IAPCallback? = null
    private var billingClient: BillingClient? = null

    /**
     * Step: Purchase updated
     */
    override fun onPurchasesUpdated(billingClient: BillingResult, purchaseList: MutableList<Purchase>?) {
        if(billingClient.responseCode == BillingClient.BillingResponseCode.OK && !purchaseList.isNullOrEmpty()){
            Timber.i("IAP >>> final step: Successfully subscription has been done. Purchase list: $purchaseList")
            acknowledgePurchase(purchaseList)
            callback?.onPurchaseSuccess(billingClient.responseCode, purchaseList)
        }else if(billingClient.responseCode == BillingClient.BillingResponseCode.USER_CANCELED){
            Timber.d("IAP >>>  User canceled purchase")
            callback?.onPurchaseFailed(billingClient.responseCode, billingClient.debugMessage)
        }else{
            Timber.e("IAP >>> Purchase failed with responseCode: ${billingClient.responseCode} debugMessage: ${billingClient.debugMessage}")
            callback?.onPurchaseFailed(billingClient.responseCode, billingClient.debugMessage)
        }
    }

    /**
     * Step: Setup billing client
     */
    fun setupBillingClient(context:Context) {
        Timber.d("IAP >>> 1. Setting up billing client")
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
               Timber.e("IAP >>> Billing client disconnected")
            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                Timber.d("IAP >>> 2. Billing client connected")
                queryPurchaseHistory()
            }
        })
    }

    /**
     * Step: Make purchase
     */
    suspend fun makePurchase(activity: Activity, productId: String){
        Timber.d("IAP >>> 3. Initiating purchase for productId : $productId")
        if(!isFeatureSupported()){
            callback?.onPurchaseFailed(BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED)
            return
        }
        if(billingClient?.isReady == false) {
            return
        }
        val productList = listOf(
            Product.newBuilder()
                .setProductId(productId)
                .setProductType(SUBS)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
        params.setProductList(productList)

        val productDetailsResult = withContext(Dispatchers.IO){
            billingClient?.queryProductDetails(params.build())
        }

        //process the result
        Timber.d("IAP >>> 4. Product details result: $productDetailsResult")
        productDetailsResult?.productDetailsList?.firstOrNull()?.let {productDetails ->
            launchBillingFlow(activity, productDetails, productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken)
        }?: run{
            //if product details are not available, show error
            callback?.onPurchaseFailed(BillingClient.BillingResponseCode.ITEM_UNAVAILABLE)
        }

    }

    /**
     * Step: Launch billing flow
     */
    private fun launchBillingFlow(activity: Activity, productDetails: ProductDetails, selectedOfferToken : String?){
        Timber.d("IAP >>> 5. called launchBillingFlow for\n productDetails: ${productDetails.productId}\n selectedOfferToken: $selectedOfferToken")
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(selectedOfferToken?:"")
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        Timber.d("IAP >>> 5a. initiating billing flow")
        //launch billing flow
       // billingClient?.launchBillingFlow(activity, billingFlowParams)
         MainActivity.instance?.let {
            Timber.d("IAP >>> 5a. initiating billing flow")
            billingClient?.launchBillingFlow(it, billingFlowParams)
        }
    }

    /**
     * Step1: Query purchases
     */
     fun queryPurchaseHistory(){
        Timber.d("IAP >>> Querying purchase history")
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(SUBS)
            .build()

        billingClient?.queryPurchasesAsync(params){
            billingResult, purchaseList ->
            if(billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                val activeSubscriptions = purchaseList.filter { purchase ->
                    purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                }
                val isSubscribed = activeSubscriptions.isNotEmpty()
                if(isSubscribed){
                    //process subscription
                    for(purchase in activeSubscriptions){
                        Timber.i("IAP >>> >>> Already has Active Subscription: ${purchase.products}")
                    }
                    callback?.onPurchaseSuccess(billingResult.responseCode, activeSubscriptions.toMutableList())
                }else{
                    //show offer
                    Timber.d("IAP >>> >>> No Active Subscription")
                    callback?.onPurchaseFailed(billingResult.responseCode)
                }
            }
        }
    }

    /**
     * is IAP supported
     */
    fun isFeatureSupported():Boolean = (billingClient?.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)?.responseCode == BillingClient.BillingResponseCode.OK)

    /**
     * last Step: Acknowledge purchase
     */
    private fun acknowledgePurchase(purchases: List<Purchase>?){
        purchases?.forEach{ purchase ->
            if(!purchase.isAcknowledged){
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                        .build()

                billingClient?.acknowledgePurchase(acknowledgePurchaseParams){ billingResult ->
                    if(billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        Timber.i("IAP >>> Purchase acknowledged")
                    }else{
                        Timber.e("IAP >>> Purchase not acknowledged responseCode: ${billingResult.responseCode} debugMessage: ${billingResult.debugMessage}")
                    }
                }
            }
        }
        callback?.onPurchaseSuccess(BillingClient.BillingResponseCode.OK, null)
    }


   fun registerCallback(callback: IAPCallback){
        this.callback = callback
    }

    fun unregisterCallback(){
        callback = null
    }

    fun onDestroyConnection(){
        unregisterCallback()
        billingClient?.endConnection()
    }

    interface IAPCallback{
        fun onPurchaseSuccess(responseCode: Int, purchaseList: MutableList<Purchase>?)
        fun onPurchaseFailed(responseCode: Int, message: String? = null)
    }
}