package com.willow.android.tv.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.willow.android.R
import com.willow.android.databinding.ActivitySplashTvBinding
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.tv.common.base.BaseActivity
import com.willow.android.tv.ui.main.MainActivity
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.config.GlobalTVConfig
import com.willow.android.tv.utils.extension.launchActivity
import com.willow.android.tv.utils.hide
import com.willow.android.tv.utils.show

class SplashActivity : BaseActivity() {

    private lateinit var mBinding: ActivitySplashTvBinding
    private lateinit var mViewModel: SplashViewModel
    private val timeToDelay: Long = 200
    private val handler: Handler = Handler(Looper.myLooper()!!)
    private var runnable: Runnable = Runnable {

        launchActivity<MainActivity> {  }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashTvBinding.inflate(layoutInflater)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mBinding.textviewMessage.visibility = View.GONE
        Glide.with(this).load(R.raw.loader).into(mBinding.progressBar);

        setContentView(mBinding.root)
        fetchConfiguration()
        FirebaseCrashlytics.getInstance().setCustomKey("deviceType", "AndroidTV")
    }

    private fun fetchConfiguration() {
        mViewModel = ViewModelProvider(this)[SplashViewModel::class.java]
        mViewModel.fetchDFPConfig.observe(this) {
            when (it){
                is Resource.Success->{
                    mBinding.progressBar.hide()

                    GlobalTVConfig.adsConfig = it.data
                }
                is Resource.Error->{
                    mBinding.progressBar.hide()
                }
                is Resource.Loading->{
                    onLoading()
                }
            }

        }
        mViewModel.fetchTVConfig.observe(this) {
            when (it){
                is Resource.Success->{
                    mBinding.progressBar.hide()
                    GlobalTVConfig.tvConfig = it.data
                }
                is Resource.Error->{
                    mBinding.progressBar.hide()
                }
                is Resource.Loading->{
                    onLoading()
                }
            }

        }

        mViewModel.fetchCountryCode.observe(this) {
            when (it){
                is Resource.Success->{
                    mBinding.progressBar.hide()
                    GlobalTVConfig.setCountryCode(it.data)
                    if (!mViewModel.isGeoBlocked()) {
                        handler.postDelayed(runnable, timeToDelay)
                    }else {
                        mBinding.textviewMessage.text = MessageConfig.geoblock
                        mBinding.textviewMessage.visibility = View.VISIBLE
                    }
                }
                is Resource.Error->{
                    mBinding.progressBar.hide()
                    showErrorPage(ErrorType.NONE, it.message.toString())
                }
                is Resource.Loading->{
                    onLoading()
                }
            }

        }
    }

    private fun onLoading() {
        if(!mBinding.progressBar.isVisible)
            mBinding.progressBar.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    private fun showErrorPage(errorType: ErrorType, errorMessage: String? = null) {
        showError(mBinding.root, errorType, errorMessage,  backBtnListener = {onBackPressed()},btnText = "Exit")
    }

}