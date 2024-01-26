package willow.android.tv.ui.subscription

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.willow.android.R
import com.willow.android.WillowApplication
import com.willow.android.databinding.LayoutInappBillingFailureBinding
import com.willow.android.tv.ui.subscription.viewmodel.SubscriptionViewModel
import com.willow.android.tv.utils.GlobalConstants
import com.willow.android.tv.utils.LogUtils
import com.willow.android.tv.utils.NavigationUtils
import willow.android.tv.ui.subscription.viewmodel.SubscriptionViewModelFactory

class SubscriptionInAppBillingFaliureFragment() : Fragment() {

    private lateinit var mViewModel: SubscriptionViewModel
    private lateinit var mBinding: LayoutInappBillingFailureBinding

    init {
        LogUtils.d(messages = "called SubscriptionInAppBillingFaliureFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = LayoutInappBillingFailureBinding.inflate(
            inflater.cloneInContext(context),
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        mBinding.tvErrorDisc.text =
            arguments?.getString(GlobalConstants.KEY_ERROR_MGS,
                getString(R.string.transaction_unsuccessful_mgs)
        )?:getString(R.string.transaction_unsuccessful_mgs)
        mBinding.buttonRetry.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                focusable = View.FOCUSABLE
            }else {
                isFocusable = true
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                isFocusedByDefault = true
            }
            requestFocus()
        }.setOnClickListener {
            NavigationUtils.refreshActivity(activity)
        }
    }
    private fun setViewModel() {
        mViewModel = ViewModelProvider(
            this,
            SubscriptionViewModelFactory(WillowApplication.instance)
        )[(SubscriptionViewModel::class.java)]
    }


    companion object {
        @JvmStatic
        fun getInstance(errorMgs: String?) =
            SubscriptionInAppBillingFaliureFragment().apply {
                arguments = Bundle().apply {
                    putString(GlobalConstants.KEY_ERROR_MGS,errorMgs)
                }
            }

    }
}