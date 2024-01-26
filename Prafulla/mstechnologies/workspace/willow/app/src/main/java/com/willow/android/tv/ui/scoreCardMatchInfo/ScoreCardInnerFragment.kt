package com.willow.android.tv.ui.scoreCardMatchInfo

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.willow.android.databinding.FragmentScoreBoardBinding
import com.willow.android.tv.ui.scoreCardMatchInfo.adapter.ScoreCardAdapter
import com.willow.android.tv.ui.scoreCardMatchInfo.model.ScorecardTableData
import com.willow.android.tv.utils.GlobalConstants

class ScoreCardInnerFragment : Fragment() {

    private lateinit var mBinding: FragmentScoreBoardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentScoreBoardBinding.inflate(
            inflater.cloneInContext(context),
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val data: ScorecardTableData? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(
                GlobalConstants.Keys.SCORECARD_DATA,
                ScorecardTableData::class.java
            )
        } else {
            arguments?.getSerializable(
                GlobalConstants.Keys.SCORECARD_DATA
            ) as ScorecardTableData?
        }
        setTextData(data?.battingTable?.countryName,data?.FallOfWicket,data?.bowlingTable?.countryName)
        setMainTableAdapter(data?.battingTable?.tableData)
        setSideTableAdapter(data?.bowlingTable?.tableData)
    }

    private fun setTextData(battingcountryName: String?, fallOfWicket: String?,
                            bowllingcountryName: String?) {
        mBinding.tvMainTableTitle.text = battingcountryName
        mBinding.tvBottomText.text = fallOfWicket
        mBinding.tvSubTableTitle.text =bowllingcountryName
    }

    private fun setSideTableAdapter(bowlingTable: List<Any>?) {
        mBinding.rvSubTable.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ScoreCardAdapter(bowlingTable)
        }

    }

    private fun setMainTableAdapter(battingTable: List<Any>?) {
        mBinding.rvMainTable.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ScoreCardAdapter(battingTable)
        }
    }

    companion object {
        fun newInstance(data: ScorecardTableData) = ScoreCardInnerFragment().apply {
            arguments = Bundle().apply {
                putSerializable(GlobalConstants.Keys.SCORECARD_DATA, data)
            }
        }
    }
}