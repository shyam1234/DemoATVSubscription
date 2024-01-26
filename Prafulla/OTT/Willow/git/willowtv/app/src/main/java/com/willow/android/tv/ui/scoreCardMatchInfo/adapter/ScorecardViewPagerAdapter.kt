package com.willow.android.tv.ui.scoreCardMatchInfo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.willow.android.tv.ui.scoreCardMatchInfo.ScoreCardInnerFragment
import com.willow.android.tv.ui.scoreCardMatchInfo.model.ScorecardTableData


class ScorecardViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val scorecardTableData: List<ScorecardTableData>
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return scorecardTableData.size?:0
    }

    override fun createFragment(position: Int): Fragment {
    return ScoreCardInnerFragment.newInstance(scorecardTableData[position])
    }
}

