package com.willow.android.tv.ui.subscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.willow.android.R
import com.willow.android.WillowApplication
import com.willow.android.databinding.FragmentSubscripitionBinding
import com.willow.android.tv.ui.subscription.viewmodel.SubscriptionViewModel
import com.willow.android.tv.utils.LogUtils
import com.willow.android.tv.utils.NavigationUtils
import willow.android.tv.ui.subscription.SubscriptionPlanFragment
import willow.android.tv.ui.subscription.viewmodel.SubscriptionViewModelFactory

class SubscriptionFragment() : Fragment() {

    private lateinit var mViewModel: SubscriptionViewModel
    private lateinit var mBinding: FragmentSubscripitionBinding

    init {
        LogUtils.d(messages = "called SubscriptionFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            FragmentSubscripitionBinding.inflate(inflater.cloneInContext(context), container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        initView()
    }

    private fun setViewModel() {
        mViewModel = ViewModelProvider(
            this,
            SubscriptionViewModelFactory(WillowApplication.instance)
        )[(SubscriptionViewModel::class.java)]
    }

    private fun initView() {
        initOnClickListener()
    }

    private fun initOnClickListener() {
        mBinding.buttonSubscribeNow.setOnClickListener {
            NavigationUtils.onAddToFragmentContainer(
                activity as AppCompatActivity?, R.id.fragment_container_view_holder,
                SubscriptionPlanFragment.newInstance(),true
            )
        }
        mBinding.buttonRestorePurchase.setOnClickListener {
            NavigationUtils.onAddToFragmentContainer(
                activity as AppCompatActivity?, R.id.fragment_container_view_holder,
                SubscriptionPlanFragment.newInstance(),true
            )
        }
        mBinding.buttonSkip.setOnClickListener {
          activity?.finish()
        }
    }

    companion object {
        @JvmStatic
        fun getInstance() = SubscriptionFragment()
    }
}