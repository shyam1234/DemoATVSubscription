package com.malviya.demosubscriptionandroidtv.di

import com.malviya.demosubscriptionandroidtv.data.BillingRepository
import com.malviya.demosubscriptionandroidtv.domain.GoogleIAPUserCases
import com.malviya.demosubscriptionandroidtv.domain.GoogleIAPUserCasesImpl
import org.koin.dsl.module

val domainModule = module {

    factory <GoogleIAPUserCases>{
        GoogleIAPUserCasesImpl(
            get<BillingRepository>()
        )
    }
}