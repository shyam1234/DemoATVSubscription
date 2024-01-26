package com.willow.android.tv.data.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.willow.android.WillowApplication
import com.willow.android.tv.data.repositories.RepositoryFactory
import com.willow.android.tv.data.repositories.mainactivity.datamodel.APICheckSubDataModel
import com.willow.android.tv.ui.main.model.UserModel
import com.willow.android.tv.utils.Actions
import com.willow.android.tv.utils.CommonFunctions
import com.willow.android.tv.utils.PrefRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by eldhosepaul on 21/02/23.
 */
class PollerAPIWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    private val prefRepository = PrefRepository(appContext)

    override fun doWork(): Result {

        // Perform the API request and obtain the result...
        val result = checkSubscription()

        // Indicate that the work was successful
        return Result.success()
    }

    fun checkSubscription(){
        CoroutineScope(Dispatchers.IO).launch {
            callCheckSubscription()
        }
    }



    private suspend fun callCheckSubscription() {
        val checkSubData =  RepositoryFactory.getMainActivityRepository().getCheckSubData(WillowApplication.instance)

        val userId = if(prefRepository.getUserID()!="null"||prefRepository.getUserID()!="") prefRepository.getUserID() else prefRepository.getTVEUserID()

        val user = UserModel(
            Actions.CHECK_SUBSCRIPTION.action,userId,
            CommonFunctions.generateMD5Common(userId)
        )

        val dataResult = checkSubData.getCheckSubscription(user).data

        saveDataToSharedPref(dataResult)
    }

    private fun saveDataToSharedPref(dataResult: APICheckSubDataModel?) {

        dataResult?.apply {
            if(status =="success"){
                Timber.d("CHeck SubAPI")
                prefRepository.apply {

                    if(dataResult.tveUser==1){
                        setTVELoggedIn(true)
                        setTVEUserID(dataResult.tveUserId.toString())
                    }else{
                        setLoggedIn(true)
                        setUserID(dataResult.userId.toString())
                    }
                    setUserSubscribed(dataResult.subscriptionStatus==1)
                }
            }
        }
    }


}

