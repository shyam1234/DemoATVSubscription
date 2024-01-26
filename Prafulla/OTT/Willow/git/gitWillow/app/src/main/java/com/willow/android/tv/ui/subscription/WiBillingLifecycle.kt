
import android.app.Activity
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.AcknowledgePurchaseParams
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
import com.android.billingclient.api.acknowledgePurchase
import com.google.common.collect.ImmutableList
import com.willow.android.tv.utils.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class WiBillingLifecycle private constructor(private val applicationContext: Context, private val externalScope: CoroutineScope =
    CoroutineScope(SupervisorJob() + Dispatchers.Default)) : DefaultLifecycleObserver, PurchasesUpdatedListener,
    BillingClientStateListener, ProductDetailsResponseListener, PurchasesResponseListener {
    private var TAG = "WiBillingLifecycle"
    private lateinit var billingClient: BillingClient

    val purchases = MutableLiveData<Purchase>()
    val acknowledgePurchaseResult = MutableLiveData<Boolean>()
    val purchasesListFetch = MutableLiveData<Boolean>()
    var productsWithProductDetails: ProductDetails? = null

    companion object {
        @Volatile
        private var INSTANCE: WiBillingLifecycle? = null
        private var PRODUCT_ID: String = ""
        private var IS_CALLED_ONCE: Boolean = false

        fun getInstance(applicationContext: Context, productId: String): WiBillingLifecycle {
            LogUtils.d("mySubscription", "getInstance")
            PRODUCT_ID = productId
//            PRODUCT_ID = "10_willow_monthly"
            IS_CALLED_ONCE = false
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WiBillingLifecycle(applicationContext)
                    .also { INSTANCE = it }
            }
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        /** 1. Initialize Billing Client */
        LogUtils.d("mySubscription", "onCreate")
        LogUtils.d("mySubscription", "2.billingClient Initialize ")
        billingClient = BillingClient.newBuilder(applicationContext)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        /** 2. Connect to Play Store */
        if (!billingClient.isReady) {
            LogUtils.d("mySubscription", "3.billingClient startConnection ")
            billingClient.startConnection(this)
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        LogUtils.d("mySubscription", "onBillingSetupFinished1")
        if(IS_CALLED_ONCE)
            return
        IS_CALLED_ONCE = true
        LogUtils.d("mySubscription", "onBillingSetupFinished2")
        val responseCode = billingResult.responseCode
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            // The billing client is ready. You can query product details and purchases here.
            fetchAvailableProducts()
            queryPurchases()
        }
    }

    /** 3. Fetch Available Products */
    fun fetchAvailableProducts() {//11
        val productIdForUser = PRODUCT_ID
        LogUtils.d("mySubscription", "fetchAvailableProducts" +PRODUCT_ID)

        //  val productIdForUser = "10_willow_monthly"
        //val productIdForUser = "10wllw-monthly"

        val productDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(ImmutableList.of(QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productIdForUser)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()))

        billingClient.queryProductDetailsAsync(productDetailsParams.build(), this)
    }

    // Called by ProductDetailsResponseListener//5//13
    override fun onProductDetailsResponse(billingResult: BillingResult, productDetailsList: MutableList<ProductDetails>) {
        val response = BillingResponse(billingResult.responseCode)
        val debugMessage = billingResult.debugMessage
        LogUtils.d("mySubscription", "4.onProductDetailsResponse . size =${productDetailsList.size}.debugMessage =   ${billingResult.debugMessage}  ")
        when {
            response.isOk -> {
                if (productDetailsList.isNullOrEmpty()) {
                    productsWithProductDetails = null
                    LogUtils.d(
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
                LogUtils.d(TAG, "onProductDetailsResponse: ${response.code} $debugMessage")
            }
            else -> {
                LogUtils.d(TAG, "onProductDetailsResponse: ${response.code} $debugMessage")
            }
        }
        LogUtils.d("mySubscription", "productsWithProductDetails :"+productsWithProductDetails.toString())
        if(productsWithProductDetails == null){
            purchasesListFetch.postValue(false)
        }else {
            purchasesListFetch.postValue(true)
        }
    }

    /**
     * 4. Query Google Play Billing for existing purchases.
     *
     * New purchases will be provided to the PurchasesUpdatedListener.
     * You still need to check the Google Play Billing API to know when purchase tokens are removed.
     */
    fun queryPurchases() {//6//12
        LogUtils.d("mySubscription", "queryPurchases" )
        if (!billingClient.isReady) {
            billingClient.startConnection(this)
        }
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build(), this)
    }

    /** 6. Handle Purchase Response */
    // Called by PurchasesResponseListener//7//14//17
    override fun onQueryPurchasesResponse(billingResult: BillingResult, purchases: MutableList<Purchase>) {
        LogUtils.d("mySubscription", "onQueryPurchasesResponse" )
        processPurchases(purchases)
    }

    fun processPurchases(purchaseList: MutableList<Purchase>?) {//8//16//18
        if (purchaseList.isNullOrEmpty()) {
            LogUtils.d("mySubscription", "processPurchases: Purchase list has not changed")
            return
        }

        LogUtils.d("mySubscription", "processPurchases" )
        purchases.postValue(purchaseList[0])
    }
//onBillingSetupFinished
    //onBillingSetupFinished
    //fetchAvailableProducts
    //queryPurchases
    //onProductDetailsResponse
    //onQueryPurchasesResponse

    /** 5. Launch Purchase Flow  submit*/
    fun launchPurchaseFlow(activity: Activity) {
        LogUtils.d("mySubscription", "6.launchPurchaseFlow . size =${productsWithProductDetails}  ")
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
            LogUtils.d("mySubscription", "7.launchBillingFlow ")

            val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        }
    }
//    processPurchases
    //onQueryPurchasesResponse
    //processPurchases

    // Called by PurchasesUpdatedListener. Called by the Billing Library when new purchases are detected.//a19
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        LogUtils.d("WiBillingLifecycle", debugMessage)
        LogUtils.d("mySubscription", "8.launchPurchaseFlow . size =${purchases?.size}.debugMessage =   ${billingResult.debugMessage}  ")
        when(responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchases.isNullOrEmpty()) {
                    LogUtils.d(TAG, "onPurchasesUpdated: null purchase list")
                    processPurchases(null)
                } else {
                    processPurchases(purchases)
                }
                return
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                LogUtils.d(TAG, "onPurchasesUpdated: User canceled the purchase")
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                LogUtils.d(TAG, "onPurchasesUpdated: The user already owns this item")
            }
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                LogUtils.d(
                    TAG, "onPurchasesUpdated: Developer error means that Google Play " +
                            "does not recognize the configuration. If you are just getting started, " +
                            "make sure you have configured the application correctly in the " +
                            "Google Play Console. The SKU product ID must match and the APK you " +
                            "are using must be signed with release keys."
                )
            }
        }
        purchasesListFetch.postValue(false)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        INSTANCE = null
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

    // ************ Called by BillingClientStateListener ************
    override fun onBillingServiceDisconnected() {
        if (!billingClient.isReady) {
            billingClient.startConnection(this)
        }
    }

    fun acknowledgePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                CoroutineScope(Dispatchers.IO).launch {
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
                }
            }
        }
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