package com.willow.android.tv.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import timber.log.Timber

/**
 * Created by eldhosepaul on 24/04/23.
 */
class CheckConnection(private val cm: ConnectivityManager) : LiveData<Boolean>() {

    constructor(application: Application) : this(
        application.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
    )

    private val networkCallback =object : ConnectivityManager.NetworkCallback(){
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Timber.d("Connection Lost")
            postValue(false)
        }
    }

    override fun onActive() {
        super.onActive()
        val request= NetworkRequest.Builder()
        cm.registerNetworkCallback(request.build(),networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        cm.unregisterNetworkCallback(networkCallback)
    }

}