package tv.willow.Views.Pages.HomePage

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.willow.android.mobile.models.pages.IAPOfferModel
import com.willow.android.mobile.models.pages.IAPReceiptVerificationModel
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.WiVolleySingleton
import org.json.JSONObject


class IAPPageViewModel() : ViewModel() {
    var receiptData: LiveData<IAPReceiptVerificationModel> = MutableLiveData<IAPReceiptVerificationModel>()
    var offersData: LiveData<IAPOfferModel> = MutableLiveData<IAPOfferModel>()

    fun makeSyncAndroidReceiptCall(context: Context, userId: String, receipt: String) {
        val _receiptData = MutableLiveData<IAPReceiptVerificationModel>().apply {
            val url = WiAPIService.iapReceiptValidationUrl
            val params = WiAPIService.getSyncReceiptParams(userId, receipt)

            val stringRequest = object: StringRequest(
                Method.POST, url,
                { response ->
                    val responseJson = JSONObject(response)
                    val iapReceiptVerificationModel = IAPReceiptVerificationModel()
                    iapReceiptVerificationModel.setData(responseJson)
                    value = iapReceiptVerificationModel
                },
                {
                    Log.e("DataFetchError:", url)
                }){
                override fun getParams(): Map<String, String> {
                    return params
                }
            }
            WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        }

        receiptData = _receiptData
    }

    fun getOffersData(context: Context) {
        val _offersData = MutableLiveData<IAPOfferModel>().apply {
            val url = WiAPIService.iapOffersUrl

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val iapOfferModel = IAPOfferModel()
                    iapOfferModel.setData(response)
                    value = iapOfferModel
                },
                {
                    Log.e("DataFetchError:", url)
                })
            WiVolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        }

        offersData = _offersData
    }

}