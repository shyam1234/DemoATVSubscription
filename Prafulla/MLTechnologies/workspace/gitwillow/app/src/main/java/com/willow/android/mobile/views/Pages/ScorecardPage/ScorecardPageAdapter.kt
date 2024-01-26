package com.willow.android.mobile.views.pages.scorecardPage

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.willow.android.mobile.models.pages.ScorecardPageModel

class ScorecardPageAdapter(fm: FragmentManager, val scorecardPageModel: ScorecardPageModel): FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return scorecardPageModel.result.Innings.size
    }

    override fun getItem(position: Int): Fragment {
        val scorecardInningFragment = ScorecardInningFragment.newInstance(position)
        return scorecardInningFragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val inningData = scorecardPageModel.result.Innings[position]
        val inningName = inningData.BattingTeamShortName + " " + inningData.innName
        return inningName
    }
}