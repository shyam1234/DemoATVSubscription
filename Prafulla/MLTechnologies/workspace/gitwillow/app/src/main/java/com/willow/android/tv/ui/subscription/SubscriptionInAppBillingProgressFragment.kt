package willow.android.tv.ui.subscription

import WiBillingLifecycle
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.billingclient.api.Purchase
import com.willow.android.R
import com.willow.android.WillowApplication
import com.willow.android.databinding.LayoutInappBillingProgressBinding
import com.willow.android.tv.ui.subscription.SubscriptionInAppBillingSuccessFragment
import com.willow.android.tv.ui.subscription.viewmodel.SubscriptionViewModel
import com.willow.android.tv.utils.GlobalConstants
import com.willow.android.tv.utils.LogUtils
import com.willow.android.tv.utils.NavigationUtils
import com.willow.android.tv.utils.PrefRepository
import willow.android.tv.data.repositories.IAppBilling.datamodel.IAPReceiptVerificationModel
import willow.android.tv.ui.subscription.viewmodel.SubscriptionViewModelFactory

class SubscriptionInAppBillingProgressFragment() : Fragment() {

    private lateinit var mViewModel: SubscriptionViewModel
    private lateinit var mBinding: LayoutInappBillingProgressBinding
    private lateinit var wiBillingLifecycle: WiBillingLifecycle

    init {
        LogUtils.d(messages = "called SubscriptionInAppBillingProgressFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = LayoutInappBillingProgressBinding.inflate(
            inflater.cloneInContext(context),
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        onLoading()
    }

    private fun onClickAction() {
        if (arguments?.getBoolean(GlobalConstants.KEY_IS_SUBSCRIBE) == true) {
            subscribe()
        }else{
            restore()
        }
    }

    private fun onLoading() {
        val imageViewAnimator = ObjectAnimator.ofFloat(mBinding.progressBar, View.ROTATION, 359f)
        imageViewAnimator.repeatCount = Animation.INFINITE
        imageViewAnimator.duration = 1000
        imageViewAnimator.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Billing APIs are all handled in the this lifecycle observer.
        LogUtils.d("mySubscription", "1.WiBillingLifecycle.getInstance called")
        wiBillingLifecycle = WiBillingLifecycle.getInstance(
            requireContext(), arguments?.getString(GlobalConstants.KEY_PRODUCT_ID, "") ?: ""
        )

        lifecycle.addObserver(wiBillingLifecycle)

        wiBillingLifecycle.purchases.observe(this) {
            if (it != null) {
                LogUtils.d("mySubscription", "makeSyncAndroidReceiptCall susscess" )
                makeSyncAndroidReceiptCall(it)
            } else {
                LogUtils.d("mySubscription", "loadFaliureScreen google api fail" )
                loadFaliureScreen()
            }
        }
        wiBillingLifecycle.purchasesListFetch.observe(this) {
            if(it) {
                onClickAction()
            }else{
                LogUtils.d("mySubscription", "loadFaliureScreen null list" )
                loadFaliureScreen()
            }
        }
//        wiBillingLifecycle.errorMgs.observe(this) {
//            loadFaliureScreen(it)
//        }
    }

    private fun makeSyncAndroidReceiptCall(purchase: Purchase) {
        LogUtils.d("mySubscription", "makeSyncAndroidReceiptCall called" )
        mViewModel.receiptData.observe(viewLifecycleOwner) {
            if (it?.accessValid != null) {
                wiBillingLifecycle.acknowledgePurchase(purchase)
                loadSuccessfulScreen(it)
            } else {
                LogUtils.d("ReceiptError", it.toString())
                LogUtils.d("mySubscription", "loadFaliureScreen api fail" )
                loadFaliureScreen()
            }
        }
        mViewModel.makeSyncAndroidReceiptCall(
            PrefRepository(context).getUserID(),
            purchase.originalJson
        )
    }

    private fun loadFaliureScreen(errorMgs: String? = null) {
        LogUtils.d("mySubscription", "loadFaliureScreen")
        NavigationUtils.removeFragment(activity, this)
        NavigationUtils.onAddToFragmentContainer(
            activity as AppCompatActivity?, R.id.fragment_container_view_holder,
            SubscriptionInAppBillingFaliureFragment.getInstance(errorMgs), true
        )
    }

    private fun loadSuccessfulScreen(it: IAPReceiptVerificationModel?) {
        LogUtils.d("mySubscription", "loadSuccessfulScreen")
        NavigationUtils.removeFragment(activity, this)
        NavigationUtils.onAddToFragmentContainer(
            activity as AppCompatActivity?, R.id.fragment_container_view_holder,
            SubscriptionInAppBillingSuccessFragment.getTnstance(R.string.payment_succesful), true
        )
    }

    private fun subscribe() {
        //loader on
        activity?.let {
            LogUtils.d("mySubscription", "5.onclick subscribe ")
            wiBillingLifecycle.launchPurchaseFlow(requireActivity())
        }
    }

    private fun restore() {
        wiBillingLifecycle.queryPurchases()
    }

    private fun setViewModel() {
        mViewModel = ViewModelProvider(
            this,
            SubscriptionViewModelFactory(WillowApplication.instance)
        )[(SubscriptionViewModel::class.java)]
    }

    override fun onDestroy() {
        super.onDestroy()
        wiBillingLifecycle.onDestroy(this)
    }

    companion object {
        @JvmStatic
        fun getInstance(productId: String?, isSubscribe: Boolean) =
            SubscriptionInAppBillingProgressFragment().apply {
                arguments = Bundle().apply {
                    putString(GlobalConstants.KEY_PRODUCT_ID, productId)
                    putBoolean(GlobalConstants.KEY_IS_SUBSCRIBE, isSubscribe)
                }
            }
    }
}