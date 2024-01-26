package com.willow.android.tv.common.base

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.ErrorUtils
import com.willow.android.tv.utils.ErrorUtils.showErrorPage
import com.willow.android.tv.utils.NavigationUtils
import com.willow.android.tv.utils.events.RefreshActivityEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

open class BaseActivity : AppCompatActivity() {
    private var mViewModel: BaseAndroidViewModel? = null

    fun showError(view: View, error: ErrorType, errorMessage: String? = null, errorDetail: String? =null, backBtnListener: View.OnClickListener? = null,btnText:String? = null) {
        showErrorPage(view, error, errorMessage, errorDetail, backBtnListener,btnText)
    }
    fun hideError(view: View) {
        ErrorUtils.hideErrorPage(view)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        mViewModel = ViewModelProvider(this)[BaseAndroidViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        mViewModel?.checkSubscription()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RefreshActivityEvent) {
        if (event.isRefresh)
            NavigationUtils.refreshActivity(this)
    }


}