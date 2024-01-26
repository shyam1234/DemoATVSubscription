package com.willow.android.tv.ui.pointtable.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.willow.android.tv.data.repositories.pointtable.datamodel.Group
import com.willow.android.tv.ui.pointtable.PointTableFragment

class PointTableGroupPagerAdapter(fa: FragmentActivity, val groupList: List<Group>) :
    FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return groupList.size
    }

    override fun createFragment(position: Int): Fragment {
        return PointTableFragment.newInstance(groupList[position])
    }

}