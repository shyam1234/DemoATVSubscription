package com.malviya.demosubscriptionandroidtv

import android.app.Application
import android.os.Build
import com.malviya.demosubscriptionandroidtv.di.dataModule
import com.malviya.demosubscriptionandroidtv.di.domainModule
import com.malviya.demosubscriptionandroidtv.di.uiDecoratorModule
import com.malviya.demosubscriptionandroidtv.di.viewModelModule
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.Forest.plant


class MyApplication : Application() {

    object INSTANCE{
        val instance = MyApplication()
    }
    override fun onCreate() {
        super.onCreate()
        initTimber()
        initCoin()
    }

    private fun initTimber() {
        plant(Timber.DebugTree())
    }

    private fun initCoin() {
        startKoin{
            androidLogger()
            androidContext(this@MyApplication)
            getKoin().setProperty("appStartTime", System.currentTimeMillis())
            modules(
                domainModule,
                viewModelModule,
                uiDecoratorModule,
                dataModule
            )
        }
    }

}