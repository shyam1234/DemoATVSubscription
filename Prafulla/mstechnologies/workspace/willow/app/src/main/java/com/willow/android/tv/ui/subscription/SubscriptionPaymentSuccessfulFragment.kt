package willow.android.tv.ui.subscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.willow.android.WillowApplication
import com.willow.android.databinding.FragmentSubscripitionPaymentSuccessfulBinding
import com.willow.android.tv.ui.subscription.model.SubscriptionPaymentSuccessfulModel
import com.willow.android.tv.ui.subscription.viewmodel.SubscriptionViewModel
import com.willow.android.tv.utils.GlobalConstants
import com.willow.android.tv.utils.LogUtils
import willow.android.tv.ui.subscription.viewmodel.SubscriptionViewModelFactory

class SubscriptionPaymentSuccessfulFragment() : Fragment() {

    companion object {
        fun getTnstance(sucessModel: SubscriptionPaymentSuccessfulModel) =
            SubscriptionPaymentSuccessfulFragment().apply {
                arguments = bundleOf().apply {
                    putSerializable(GlobalConstants.PaymentSuccessfullModel, sucessModel)
                }
            }
    }

    private lateinit var mViewModel: SubscriptionViewModel
    private lateinit var mBinding: FragmentSubscripitionPaymentSuccessfulBinding
    //private lateinit var prefRepository: PrefRepository

    init {
        LogUtils.d(messages = "called SubscriptionPaymentSuccessfulFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            FragmentSubscripitionPaymentSuccessfulBinding.inflate(inflater.cloneInContext(context), container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProvider(
            this,
            SubscriptionViewModelFactory(WillowApplication.instance)
        )[(SubscriptionViewModel::class.java)]
        mBinding.model =
            arguments?.getSerializable(GlobalConstants.PaymentSuccessfullModel) as SubscriptionPaymentSuccessfulModel
        //PrefRepository(context).setUserSubscribed(true)
        //prefRepository = PrefRepository(activity?.applicationContext)
        //prefRepository.setSubscriptionStatus(1)
        mBinding.buttonHomeButton.setOnClickListener {
            activity?.finish()
        }
    }


}