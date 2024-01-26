package willow.android.tv.ui.subscription.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.willow.android.WillowApplication
import com.willow.android.tv.ui.subscription.viewmodel.SubscriptionViewModel


class SubscriptionViewModelFactory(
    val application: WillowApplication
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SubscriptionViewModel(application) as T
    }
}

