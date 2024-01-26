package com.willow.android.tv.ui.scoreCardMatchInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.willow.android.WillowApplication
import com.willow.android.databinding.FragmentFixturesBySeriesBinding
import com.willow.android.databinding.FragmentMatchinfoBinding
import com.willow.android.tv.common.base.BaseFragment
import com.willow.android.tv.ui.scoreCardMatchInfo.adapter.ScoreCardAdapter
import com.willow.android.tv.ui.scoreCardMatchInfo.model.MatchInfoData
import com.willow.android.tv.ui.scoreCardMatchInfo.viewmodel.ScorecardPageViewModel
import com.willow.android.tv.ui.scoreCardMatchInfo.viewmodel.ScorecardPageViewModelFactory
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.GlobalConstants
import timber.log.Timber

class MatchInfoFragment : BaseFragment() {

    private var mViewModel: ScorecardPageViewModel?=null
    private var mBinding: FragmentMatchinfoBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMatchinfoBinding.inflate(
            inflater.cloneInContext(context),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProvider(
            this,
            ScorecardPageViewModelFactory(WillowApplication.instance)
        )[(ScorecardPageViewModel::class.java)]


        if(arguments?.getString(GlobalConstants.Keys.TARGET_URL) == null|| arguments?.getString(GlobalConstants.Keys.TARGET_URL) == "null"){
            Timber.d("$$$$ Match Info Target URL:: "+ arguments?.getString(GlobalConstants.Keys.TARGET_URL))
            showError(binding.root, ErrorType.NO_DATA_FOUND)
        }else {
            arguments?.getString(GlobalConstants.Keys.TARGET_URL)
                ?.let { mViewModel?.makeMatchInfoPageDataRequest(context, it) }
            mViewModel?.matchInfoData?.observe(viewLifecycleOwner) {
                if (it?.paraData != null && it.rowData != null) {
                    initView(it)
                } else {
                    showError(binding.root, ErrorType.NO_DATA_FOUND)
                }
            }
        }
    }

    private fun initView(data: MatchInfoData?) {
        setTextData(data?.title)
        setMainTableAdapter(data?.rowData)
        setSideTableAdapter(data?.paraData)
    }

    private fun setTextData(title: String?) {
        binding.tvSubTableTitle.text =title
    }

    private fun setSideTableAdapter(bowlingTable: List<Any>?) {
        binding.rvSubTable.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ScoreCardAdapter(bowlingTable)
        }

    }

    private fun setMainTableAdapter(battingTable: List<Any>?) {
        binding.rvMainTable.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ScoreCardAdapter(battingTable)
        }
    }

    companion object {
        fun newInstance(url: String?) = MatchInfoFragment().apply {
            arguments=Bundle().apply {
                putString(GlobalConstants.Keys.TARGET_URL,url)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
        mViewModel = null
    }
}