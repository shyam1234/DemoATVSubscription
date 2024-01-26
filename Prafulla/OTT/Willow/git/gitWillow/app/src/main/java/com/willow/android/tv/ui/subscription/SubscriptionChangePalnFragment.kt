package com.willow.android.tv.ui.subscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.willow.android.R
import com.willow.android.WillowApplication
import com.willow.android.databinding.FragmentChangePlanSubscriptionBinding
import com.willow.android.tv.common.base.BaseFragment
import com.willow.android.tv.ui.subscription.viewmodel.SubscriptionViewModel
import com.willow.android.tv.utils.LogUtils
import com.willow.android.tv.utils.NavigationUtils
import willow.android.tv.ui.subscription.viewmodel.SubscriptionViewModelFactory

class SubscriptionChangePalnFragment() : BaseFragment() {


    private lateinit var viewModel: SubscriptionViewModel
    private lateinit var mBinding: FragmentChangePlanSubscriptionBinding

    init {
        LogUtils.d(messages = "called SubscriptionChangePalnFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            FragmentChangePlanSubscriptionBinding
                .inflate(inflater.cloneInContext(context), container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(
            this,
            SubscriptionViewModelFactory(WillowApplication.instance)
        )[(SubscriptionViewModel::class.java)]
        mBinding.viewModel = viewModel

        initClickListner()
    }

    private fun initClickListner() {
        mBinding.btnChangePlan.setOnClickListener {
            viewModel.onPlanChange()
        }
        mBinding.btnCancel.setOnClickListener {
        }
    }


    fun loadPlanChangeSussesfulScreen(){
        NavigationUtils.onReplaceToFragmentContainer(
            activity as AppCompatActivity?, R.id.fragment_container_view_holder,
            SubscriptionInAppBillingSuccessFragment.getTnstance(R.string.plan_change_succesful)
        )
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            SubscriptionChangePalnFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

}