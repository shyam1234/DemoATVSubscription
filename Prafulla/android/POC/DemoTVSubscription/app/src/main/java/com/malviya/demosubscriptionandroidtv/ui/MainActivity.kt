package com.malviya.demosubscriptionandroidtv.ui

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.malviya.demosubscriptionandroidtv.bindings.decorators.UIDecorator
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.malviya.demosubscriptionandroidtv.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity: AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel { parametersOf(this) }
    private lateinit var binding: ActivityMainBinding
//    val defaultUIDecorator: UIDecorator by inject()
//    val greenUIDecorator: UIDecorator by inject()
//    val redUIDecorator: UIDecorator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)
        registerObserver()
    }

    private fun registerObserver() {
       binding.btnSubscription.setOnClickListener {
           viewModel.onSubscriptionBtnClicked()
       }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}