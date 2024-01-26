package com.willow.android.mobile.services.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.google.common.collect.ImmutableList
import com.willow.android.mobile.models.auth.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob


class WiBillingLifecycle private constructor(private val applicationContext: Context, private val externalScope: CoroutineScope =
    CoroutineScope(SupervisorJob() + Dispatchers.Default)) : DefaultLifecycleObserver, PurchasesUpdatedListener,
    BillingClientStateListener, ProductDetailsResponseListener, PurchasesResponseListener {
    private var TAG = "WiBillingLifecycle"
    private lateinit var billingClient: BillingClient

    val purchases = MutableLiveData<List<Purchase>>()
    var productsWithProductDetails: ProductDetails? = null

    companion object {
        @Volatile
        private var INSTANCE: WiBillingLifecycle? = null

        fun getInstance(applicationContext: Context): WiBillingLifecycle =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: WiBillingLifecycle(applicationContext).also { INSTANCE = it }
            }
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        /** 1. Initialize Billing Client */
        billingClient = BillingClient.newBuilder(applicationContext)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        /** 2. Connect to Play Store */
        if (!billingClient.isReady) {
            billingClient.startConnection(this)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

    /** 3. Fetch Available Products */
    fun fetchAvailableProducts() {
        val productIdForUser = UserModel.iapProductId

        val productDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(ImmutableList.of(QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productIdForUser)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()))

        billingClient.queryProductDetailsAsync(productDetailsParams.build(), this)
    }

    /** 5. Launch Purchase Flow */
    fun launchPurchaseFlow(activity: Activity) {
        if (productsWithProductDetails != null) {
            val productDetails = productsWithProductDetails!!
            val offerToken = productDetails.subscriptionOfferDetails?.get(0)?.offerToken

            val productDetailsParamsList =
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .setOfferToken(offerToken!!)
                        .build()
                )
            val billingFlowParams =
                BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()

            val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        }
    }

    // Called by PurchasesUpdatedListener. Called by the Billing Library when new purchases are detected.
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.e("WiBillingLifecycle", debugMessage)
        when(responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchases == null) {
                    Log.e(TAG, "onPurchasesUpdated: null purchase list")
                    processPurchases(null)
                } else {
                    processPurchases(purchases)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.e(TAG, "onPurchasesUpdated: User canceled the purchase")
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Log.e(TAG, "onPurchasesUpdated: The user already owns this item")
            }
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                Log.e(
                    TAG, "onPurchasesUpdated: Developer error means that Google Play " +
                            "does not recognize the configuration. If you are just getting started, " +
                            "make sure you have configured the application correctly in the " +
                            "Google Play Console. The SKU product ID must match and the APK you " +
                            "are using must be signed with release keys."
                )
            }
        }
    }

    // ************ Called by BillingClientStateListener ************
    override fun onBillingServiceDisconnected() {
        if (!billingClient.isReady) {
            billingClient.startConnection(this)
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            // The billing client is ready. You can query product details and purchases here.
            fetchAvailableProducts()
            queryPurchases()
        }
    }
    // **************************************************************


    // Called by ProductDetailsResponseListener
    override fun onProductDetailsResponse(billingResult: BillingResult, productDetailsList: MutableList<ProductDetails>) {
        val response = BillingResponse(billingResult.responseCode)
        val debugMessage = billingResult.debugMessage
        when {
            response.isOk -> {
                if (productDetailsList.isNullOrEmpty()) {
                    productsWithProductDetails = null
                    Log.e(
                        TAG, "onProductDetailsResponse: " +
                                "Found null ProductDetails. " +
                                "Check to see if the products you requested are correctly published " +
                                "in the Google Play Console."
                    )
                } else {
                    for (productDetails in productDetailsList) {
                        productsWithProductDetails = productDetails
                    }
                }
            }
            response.isTerribleFailure -> {
                // These response codes are not expected.
                Log.wtf(TAG, "onProductDetailsResponse: ${response.code} $debugMessage")
            }
            else -> {
                Log.e(TAG, "onProductDetailsResponse: ${response.code} $debugMessage")
            }

        }
    }

    /** 6. Handle Purchase Response */
    // Called by PurchasesResponseListener
    override fun onQueryPurchasesResponse(billingResult: BillingResult, purchases: MutableList<Purchase>) {
        processPurchases(purchases)
    }


    /************************* Supporting Methods ************************************/

    /**
     * 4. Query Google Play Billing for existing purchases.
     *
     * New purchases will be provided to the PurchasesUpdatedListener.
     * You still need to check the Google Play Billing API to know when purchase tokens are removed.
    */
    fun queryPurchases() {
        if (!billingClient.isReady) {
            billingClient.startConnection(this)
        }
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build(), this)
    }

    fun processPurchases(purchaseList: MutableList<Purchase>?) {
        if (purchaseList == null) {
            Log.d(TAG, "processPurchases: Purchase list has not changed")
            return
        }

        purchases.postValue(purchaseList!!)
    }
}


@JvmInline
private value class BillingResponse(val code: Int) {
    val isOk: Boolean
        get() = code == BillingClient.BillingResponseCode.OK
    val canFailGracefully: Boolean
        get() = code == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED
    val isRecoverableError: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.ERROR,
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
        )
    val isNonrecoverableError: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
            BillingClient.BillingResponseCode.DEVELOPER_ERROR,
        )
    val isTerribleFailure: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED,
            BillingClient.BillingResponseCode.USER_CANCELED,
        )
}