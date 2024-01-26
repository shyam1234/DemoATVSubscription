package com.willow.android.tv.ui.fixturespage.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.ui.fixturespage.FixturesByDateFragment
import com.willow.android.tv.ui.fixturespage.FixturesBySeriesFragment
import com.willow.android.tv.utils.GlobalConstants.NUM_TABS


public class ViewPagerAdapter(fragmentManager: FragmentManager,
                              lifecycle: Lifecycle,
                              private val keyListener: KeyListener
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return FixturesByDateFragment.newInstance(keyListener)
            1 -> return FixturesBySeriesFragment.newInstance(keyListener)
        }
        return FixturesBySeriesFragment.newInstance(keyListener)
    }
}

