package com.willow.android.tv.ui.subscription

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.willow.android.WillowApplication
import com.willow.android.databinding.FragmentChangeSubscriptionPlanListBinding
import com.willow.android.tv.common.base.BaseFragment
import com.willow.android.tv.ui.subscription.adapter.SubscriptionAdapter
import com.willow.android.tv.ui.subscription.`interface`.ISubscriptionListener
import com.willow.android.tv.ui.subscription.viewmodel.SubscriptionViewModel
import com.willow.android.tv.utils.LogUtils
import com.willow.android.tv.utils.Utils
import willow.android.tv.ui.subscription.viewmodel.SubscriptionViewModelFactory

class SubscriptionChangePalnListFragment() : BaseFragment() {


    private lateinit var viewModel: SubscriptionViewModel
    private lateinit var mBinding: FragmentChangeSubscriptionPlanListBinding

    init {
        LogUtils.d(messages = "called SubscriptionFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            FragmentChangeSubscriptionPlanListBinding
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
    }

    fun onPageSelected(position: Int, size: Int) {
        Utils.updateIndicator(context, mBinding.linIndicatorHolder, position, size)
    }

    private fun initRecycleView() {
        val dataSet = viewModel.getInAppSubscriptionChangePlanModel()

        mBinding.rvSubButton.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                foregroundGravity = Gravity.START
            }
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = SubscriptionAdapter(
                dataSet,
                object : ISubscriptionListener {
                    override fun onPlanChangeClick(planId: String) {

                    }
                    override fun onFocuseSubScripe(
                        discriptionText: List<String>,
                        absoluteAdapterPosition: Int
                    ) {
                        onPageSelected(absoluteAdapterPosition, dataSet.size)
                    }

                })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SubscriptionChangePalnListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

}