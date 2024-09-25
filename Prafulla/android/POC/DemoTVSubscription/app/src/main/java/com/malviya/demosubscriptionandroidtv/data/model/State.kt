import com.android.billingclient.api.Purchase

sealed class PurchaseState {
    data class Loading(val message: String? = null) : PurchaseState()
    data class Success(val code: Int, val purchaseList: MutableList<Purchase>? = null, val message: String? = null) : PurchaseState()
    data class Failure(val code: Int, val message: String? = null) : PurchaseState()
}