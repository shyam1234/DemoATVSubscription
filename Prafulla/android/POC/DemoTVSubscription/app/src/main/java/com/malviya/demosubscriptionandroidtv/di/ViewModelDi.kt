package com.malviya.demosubscriptionandroidtv.di

import com.malviya.demosubscriptionandroidtv.domain.GoogleIAPUserCases
import com.malviya.demosubscriptionandroidtv.ui.MainActivity
import com.malviya.demosubscriptionandroidtv.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { (activity: MainActivity) ->
        MainViewModel(activity, get<GoogleIAPUserCases>())
    }
}