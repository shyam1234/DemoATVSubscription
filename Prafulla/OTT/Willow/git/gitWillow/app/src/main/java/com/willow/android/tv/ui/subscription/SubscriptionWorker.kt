package com.willow.android.tv.ui.subscription

import android.app.Application
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.willow.android.mobile.utils.Utils
import com.willow.android.tv.data.repositories.RepositoryFactory
import com.willow.android.tv.utils.UserDetailsHelper
import com.willow.android.tv.utils.events.RefreshActivityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import willow.android.tv.data.repositories.IAppBilling.datamodel.checkSubscriptionRequest

class SubscriptionWorker(
    val application: Application,
    val viewModelScope: CoroutineScope,
    val context: Context,
    parameters: WorkerParameters
) :
    Worker(context, parameters) {
    override fun doWork(): Result {
        checkSubscription()
        return Result.success()
    }

    fun checkSubscription() {
        val data = RepositoryFactory.getIAppBillingRepository().getIAppBilling(application)
        viewModelScope.launch {
            val uid = UserDetailsHelper.getUSerDetailsId(
                application.baseContext
            )
            uid?.let {
                data.checkSubscription(
                    checkSubscriptionRequest(
                        uid = it,
                        authToken = Utils.checkSubscriptionAuthToken(it.toString())
                    )
                ).data?.apply {
                    EventBus.getDefault().post(
                        RefreshActivityEvent(
                            UserDetailsHelper.isSubscriptionUpdated(
                                context,
                                this
                            )
                        )
                    )
                }
            }
        }
    }

}