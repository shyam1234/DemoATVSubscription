package com.willow.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.provider.Settings
import android.provider.Settings.Secure.ANDROID_ID
import android.util.Log
import androidx.room.Room
import com.adobe.adobepass.accessenabler.api.AccessEnabler
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.WiVolleySingleton
import com.willow.android.mobile.services.billing.WiBillingLifecycle
import com.willow.android.tv.data.room.db.AppDatabase
import com.willow.android.tv.di.ApplicationComponent
import com.willow.android.tv.di.DaggerApplicationComponent
import com.willow.android.tv.utils.Utils
import timber.log.Timber

class WillowApplication : Application() {
    lateinit var applicationComponent: ApplicationComponent
    companion object {
        lateinit var instance: WillowApplication
        lateinit var dbBuilder: AppDatabase

        @SuppressLint("StaticFieldLeak")
        var accessEnablerInstance: AccessEnabler? = null
        private var applicationContext: Context? = null
        val appContext: Context? get() = applicationContext
        fun setAccessEnabler(accessEnabler: AccessEnabler?) {
            accessEnablerInstance = accessEnabler
        }

        @JvmName("getAccessEnablerInstance1")
        fun getAccessEnablerInstance(): AccessEnabler? {
            return accessEnablerInstance
        }

        lateinit var wiBillingLifecycle: WiBillingLifecycle

        // @ToDo - handle app context properly
        lateinit var analyticsContext: Context

        fun sendWiAnalyticsRequest(paramString: String) {
            var url = WiAPIService.wiAnalyticsUrl

            val stringRequest = object: StringRequest(
                Method.POST, url,
                { response ->
                    Log.d("Willow Analytics", response)
                },
                {
                    Log.e("DataFetchError:", url)
                }) {
                override fun getParamsEncoding(): String {
                    return paramString
                }
            }
            WiVolleySingleton.getInstance(analyticsContext).addToRequestQueue(stringRequest)
        }
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        applicationComponent = DaggerApplicationComponent.builder().build()
        wiBillingLifecycle = WiBillingLifecycle.getInstance(this)
        analyticsContext = applicationContext

        dbBuilder = Room.databaseBuilder(instance, AppDatabase::class.java, "willow_app.db")
            .build()

        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .permitDiskWrites()
                .permitDiskReads()
                .detectNetwork()
                .penaltyLog()
                .build()
        )
    }

    fun getDeviceId(): String {
        return Settings.Secure.getString(appContext?.contentResolver, ANDROID_ID)
    }

    fun gaTrackingId() = "UA-69162035-12"

    override fun onTrimMemory(level: Int) {
        Timber.e("MemoryLog called onTrimMemory")
        Utils.memoryLogs()
        appContext?.let { Glide.get(it).clearMemory() }
        super.onTrimMemory(level)
    }

}
