package com.willow.android.tv.ui.subscription

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.willow.android.R
import com.willow.android.WillowApplication
import com.willow.android.databinding.LayoutInappBillingSucessBinding
import com.willow.android.tv.ui.subscription.viewmodel.SubscriptionViewModel
import com.willow.android.tv.utils.GlobalConstants
import com.willow.android.tv.utils.LogUtils
import willow.android.tv.ui.subscription.viewmodel.SubscriptionViewModelFactory

class SubscriptionInAppBillingSuccessFragment() : Fragment() {

    private lateinit var mViewModel: SubscriptionViewModel
    private lateinit var mBinding: LayoutInappBillingSucessBinding
    //private lateinit var prefRepository: PrefRepository

    init {
        LogUtils.d(messages = "called SubscriptionInAppBillingFaliureFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = LayoutInappBillingSucessBinding.inflate(
            inflater.cloneInContext(context),
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        //PrefRepository(context).setUserSubscribed(true)
        //prefRepository = PrefRepository(activity?.applicationContext)
        //prefRepository.setSubscriptionStatus(1)
        mBinding.title.setText(
            arguments?.getInt(GlobalConstants.Keys.SUCCESSFULE_TITLE)
                ?: R.string.success_title
        )
        mBinding.buttonRetry.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                focusable = View.FOCUSABLE
            } else {
                isFocusable = true
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                isFocusedByDefault = true
            }
            requestFocus()
        }.setOnClickListener {
            finish()
        }
        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                finish()
            }, 6000)
        }
    }

    private fun finish() {
        activity?.finish()
    }

    private fun setViewModel() {
        mViewModel = ViewModelProvider(
            this,
            SubscriptionViewModelFactory(WillowApplication.instance)
        )[(SubscriptionViewModel::class.java)]
    }


    companion object {
        @JvmStatic
        fun getTnstance(titleRes: Int) =
            SubscriptionInAppBillingSuccessFragment().apply {
                arguments = Bundle().apply {
                    putInt(GlobalConstants.Keys.SUCCESSFULE_TITLE, titleRes)
                }
            }

    }
}