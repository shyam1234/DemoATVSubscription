package com.willow.android.tv.ui.resultspage.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.ui.resultspage.ResultsByDateFragment
import com.willow.android.tv.ui.resultspage.ResultsBySeriesFragment
import com.willow.android.tv.utils.GlobalConstants.NUM_TABS


public class ResultsViewPagerAdapter(fragmentManager: FragmentManager,
                                     lifecycle: Lifecycle,
                                     private val keyListener: KeyListener
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return ResultsByDateFragment.newInstance(keyListener)
            1 -> return ResultsBySeriesFragment.newInstance(keyListener)
        }
        return ResultsBySeriesFragment.newInstance(keyListener)
    }
}

