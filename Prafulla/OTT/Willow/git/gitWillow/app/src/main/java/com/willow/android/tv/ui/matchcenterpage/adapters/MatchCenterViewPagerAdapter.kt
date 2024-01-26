//package com.willow.android.tv.ui.matchcenterpage.adapters
//
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentManager
//import androidx.lifecycle.Lifecycle
//import androidx.viewpager2.adapter.FragmentStateAdapter
//import com.willow.android.tv.common.navmenu.NavigationMenuCallback
//import com.willow.android.tv.ui.matchcenterpage.MatchCenterVideosTabFragment
//import com.willow.android.tv.ui.matchcenterpage.UpcomingMatchesByDateFragment
//import com.willow.android.tv.ui.matchcenterpage.model.TabsDataModel
//import com.willow.android.tv.ui.scoreCardMatchInfo.MatchInfoFragment
//import com.willow.android.tv.ui.scoreCardMatchInfo.ScoreCardFragment
//import com.willow.android.tv.utils.GlobalConstants
//import com.willow.android.tv.utils.GlobalConstants.MATCH_CENTER_NUM_TABS
//
//
//class MatchCenterViewPagerAdapter(
//    fragmentManager: FragmentManager,
//    lifecycle: Lifecycle,
//    private val tabsList: ArrayList<TabsDataModel>,
//    val listner: NavigationMenuCallback
//) :
//    FragmentStateAdapter(fragmentManager, lifecycle) {
//
//    override fun getItemCount(): Int {
//        return MATCH_CENTER_NUM_TABS
//    }
//
//    override fun createFragment(position: Int): Fragment {
//        val tab = tabsList[position]
//        return when (tab.type) {
//            GlobalConstants.MatchCenterTab.VIDEOS ->
//                MatchCenterVideosTabFragment.newInstance()
//            GlobalConstants.MatchCenterTab.SCORECARD ->
//                ScoreCardFragment.getInstance(listner, tab.url)
//            GlobalConstants.MatchCenterTab.MATCH_INFO ->
//                MatchInfoFragment.newInstance(listner, tab.url)
//
//            GlobalConstants.MatchCenterTab.FIXTURES ->
//                UpcomingMatchesByDateFragment.newInstance(null)
//            //todo add GlobalConstants.MatchCenterTab.STANDINGS ->
////            GlobalConstants.MatchCenterTab.STANDINGS ->
////                PointTableGroupFragment.newInstance(listner, tab.url)
//
//            else -> MatchCenterVideosTabFragment.newInstance()
//        }
//    }
//}
//
