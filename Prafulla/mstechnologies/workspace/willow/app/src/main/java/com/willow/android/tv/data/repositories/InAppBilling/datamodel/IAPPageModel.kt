package willow.android.tv.data.repositories.IAppBilling.datamodel

import com.willow.android.tv.utils.Actions


data class IAPReceiptVerificationModel(
    var accessValid: Boolean = false,
    var message: String = "",
    var status: String = "",
    var user_id: String = "0"
)

data class checkSubscriptionRequest(
    val action:String = Actions.CHECK_SUBSCRIPTION.action,
    val uid:String,
    val authToken: String,
)
