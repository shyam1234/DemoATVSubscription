package willow.android.tv.data.repositories.IAppBilling

import android.app.Application
import com.willow.android.tv.data.repositories.InAppBilling.IInAppBillingRepository
import com.willow.android.tv.data.repositories.InAppBilling.remote.IAppBillingRemoteDataSource

class InAppBillingRepository {

    /**
     */
    fun getIAppBilling(application: Application): IInAppBillingRepository {

        return IAppBillingRemoteDataSource(application)

    }
}