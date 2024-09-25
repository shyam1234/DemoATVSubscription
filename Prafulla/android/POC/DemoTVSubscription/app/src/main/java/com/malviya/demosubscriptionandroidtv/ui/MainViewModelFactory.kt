//package com.malviya.demosubscriptionandroidtv.ui
//
//import android.app.Activity
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//
//class MainViewModelFactory(private val activity: Activity):
//ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
//            return MainViewModel(activity as MainActivity, null) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//
//}