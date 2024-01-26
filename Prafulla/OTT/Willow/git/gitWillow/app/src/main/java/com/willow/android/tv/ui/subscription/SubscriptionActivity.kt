package com.willow.android.tv.ui.subscription

import android.os.Bundle
import com.willow.android.databinding.ActivityMainBinding
import com.willow.android.tv.common.base.BaseActivity
import com.willow.android.tv.ui.playback.PlayerManager
import com.willow.android.tv.utils.CheckConnection
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.NavigationUtils
import timber.log.Timber

class SubscriptionActivity : BaseActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private val checkConnection by lazy { CheckConnection(application) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlayerManager.shouldPlayContent = false
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        loadSubscriptionFragment()
        checkConnection.observe(this@SubscriptionActivity){
            Timber.d("MainActivity Connection Check:: $it")
            if(!it){
                showError(mBinding.root, ErrorType.NONE,"NO INTERNET",  backBtnListener = {onBackPressed()},btnText = "Back")
            }else{
                hideError(mBinding.root)
            }
        }
    }

    private fun loadSubscriptionFragment() {
        NavigationUtils.onReplaceToFragmentContainer(
            this,
            mBinding.fragmentContainerViewHolder,
            SubscriptionFragment.getInstance()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        PlayerManager.shouldPlayContent = true
    }
}