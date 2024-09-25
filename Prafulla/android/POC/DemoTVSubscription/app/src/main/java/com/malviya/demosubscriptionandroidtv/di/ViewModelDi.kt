package com.malviya.demosubscriptionandroidtv.di

import com.malviya.demosubscriptionandroidtv.data.BillingRepository
import com.malviya.demosubscriptionandroidtv.data.utils.GoogleIAPHelper
import com.malviya.demosubscriptionandroidtv.domain.GoogleIAPUserCases
import com.malviya.demosubscriptionandroidtv.domain.GoogleIAPUserCasesImpl
import com.malviya.demosubscriptionandroidtv.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        MainViewModel(
            get<GoogleIAPUserCases>()
        )
    }
}