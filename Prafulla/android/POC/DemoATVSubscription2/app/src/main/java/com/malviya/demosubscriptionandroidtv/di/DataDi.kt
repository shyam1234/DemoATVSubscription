package com.malviya.demosubscriptionandroidtv.di

import com.malviya.demosubscriptionandroidtv.data.BillingRepository
import com.malviya.demosubscriptionandroidtv.data.utils.GoogleIAPHelper
import com.malviya.demosubscriptionandroidtv.ui.MainActivity
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        GoogleIAPHelper()
    }
    single {
        MainActivity()
    }
    single {
        BillingRepository(androidContext(), get<MainActivity>(), get<GoogleIAPHelper>())
    }
}