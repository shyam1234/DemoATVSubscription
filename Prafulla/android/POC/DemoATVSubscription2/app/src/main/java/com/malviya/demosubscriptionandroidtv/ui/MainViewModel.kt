package com.malviya.demosubscriptionandroidtv.ui

import PurchaseState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.Purchase
import com.malviya.demosubscriptionandroidtv.domain.GoogleIAPUserCases
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class MainViewModel(
    val googleIAPUserCases : GoogleIAPUserCases
) : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Click to Subscribe"
    }
    val text: LiveData<String> = _text

    private val _isPurchaseInProgress = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isPurchaseInProgress: LiveData<Boolean> = _isPurchaseInProgress

    init {
        viewModelScope.launch {
            googleIAPUserCases.setupBillingClient()
        }
        viewModelScope.launch {
            googleIAPUserCases.onPurchaseCallback().onCompletion {
                _isPurchaseInProgress.value = false
            }.collect{ purchaseState ->
                when(purchaseState){
                    is PurchaseState.Loading -> {
                        _isPurchaseInProgress.value = true
                        purchaseState.message?.let {
                            _text.value = it
                        }

                    }
                    is PurchaseState.Success -> {
                        _isPurchaseInProgress.value = false
                        delay(500)
                        purchaseState.purchaseList?.forEach {
                            when(it.purchaseState){
                                Purchase.PurchaseState.PURCHASED -> {
                                    _text.value  = "Purchase PURCHASED ${it.products} \ncode: ${it.purchaseState}"
                                }
                                Purchase.PurchaseState.PENDING -> {
                                    _text.value  = "Purchase PENDING ${it.products} \ncode: ${it.purchaseState}"
                                }
                                Purchase.PurchaseState.UNSPECIFIED_STATE -> {
                                    _text.value  = "Purchase UNSPECIFIED_STATE ${it.products} \ncode: ${it.purchaseState}"
                                }
                            }
                        }?: kotlin.run {
                            //for getting update status of purchase
                            googleIAPUserCases.queryPurchases()
                        }
                    }
                    is PurchaseState.Failure -> {
                        _isPurchaseInProgress.value = false
                        _text.value = if(purchaseState.message.isNullOrEmpty()) "Subscription failed.\nTry again later.\ncode:${purchaseState.code}"  else purchaseState.message
                    }
                    else -> {
                        _isPurchaseInProgress.value = false
                        _text.value = "Developer Error"
                    }
                }
            }
        }
    }

    fun onSubscriptionBtnClicked(){
        viewModelScope.launch {
            googleIAPUserCases.execute()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            googleIAPUserCases.dispose()
        }
    }
}
