package com.willow.android.mobile.views.pages.commentaryPage

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.willow.android.mobile.models.pages.CommentaryPageModel

class CommentaryPageAdapter(fm: FragmentManager, val commentaryPageModel: CommentaryPageModel): FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return commentaryPageModel.Innings.size
    }

    override fun getItem(position: Int): Fragment {
        val commentaryInningFragment = CommentaryInningFragment.newInstance(position)
        return commentaryInningFragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val inningData = commentaryPageModel.Innings[position]
        val inningName = inningData.tnShort + " " + inningData.innName
        return inningName
    }
}