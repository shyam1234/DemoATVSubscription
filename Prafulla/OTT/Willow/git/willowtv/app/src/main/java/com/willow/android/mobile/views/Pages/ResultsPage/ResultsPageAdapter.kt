package com.willow.android.mobile.views.pages.resultsPage

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ResultsPageAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return ResultsByDateFragment()
            }

            else -> {
                return ResultsBySeriesFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                "BY DATE"
            }

            else -> {
                "BY SERIES"
            }
        }
    }
}