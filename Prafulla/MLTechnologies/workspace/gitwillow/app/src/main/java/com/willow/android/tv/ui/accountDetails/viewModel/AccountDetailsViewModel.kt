package com.willow.android.tv.ui.accountDetails.viewModel

import android.app.Application
import android.content.Context
import com.willow.android.R
import com.willow.android.tv.common.base.BaseAndroidViewModel
import com.willow.android.tv.common.genericDialogBox.GenericDialogModel
import com.willow.android.tv.data.room.db.AppDatabase
import com.willow.android.tv.ui.accountDetails.model.AccountDetailsModel
import com.willow.android.tv.utils.UserDetailsHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AccountDetailsViewModel(application: Application,private val appDatabase: AppDatabase) : BaseAndroidViewModel(application) {
    fun getAccountDetailModel(context: Context?): AccountDetailsModel {
        return UserDetailsHelper.getAccountDetails(context)
    }

    fun getLogOutDialogModel(): GenericDialogModel {
        return GenericDialogModel(
            titleText = getString(R.string.logout_title),
            descriptionText = getString(R.string.logout_disc),
            positveButtonText = getString(R.string.sign_out),
            negativeButtonText = getString(R.string.cancel),
            dialogTag = getString(R.string.sign_out)
        )
    }

    fun getCancelSubscriptionDialogModel(): GenericDialogModel {
        return GenericDialogModel(
            titleText = getString(R.string.unsbscripe_title),
            descriptionText = getString(R.string.unsbscripe_disc),
            positveButtonText = getString(R.string.back_to_home),
            negativeButtonText = getString(R.string.cancel_subscriptions),
            dialogTag = getString(R.string.cancel_subscriptions)
        )
    }

    fun clearAllTablesOnUserLogout(){
        GlobalScope.launch {
            clearTables()
        }
    }
    private suspend fun clearTables(){
        appDatabase.clearAllTables()
    }
}