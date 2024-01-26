package com.willow.android.tv.common.carousel.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.common.carousel.CarouselRowItemFragment
import com.willow.android.tv.common.carousel.models.HeroBanner
import com.willow.android.tv.common.navmenu.NavigationMenuCallback


/**
For rendering the different pages of carousel
 */
class CarouselViewPagerAdapter(
    fragmentManager: FragmentManager, behavior: Int,
    var heroBannerModel: HeroBanner?,
    private val navigationMenuCallback: NavigationMenuCallback?,
    private val keyListener: KeyListener?
) : FragmentStatePagerAdapter(fragmentManager, behavior) {
    init {
        //sort the list based on the priority
        heroBannerModel?.also { it -> it.listItems?.sortedBy { it.priority } }
    }

    override fun getCount(): Int {
        return heroBannerModel?.listItems?.size ?: 0
    }

    override fun getItem(position: Int): Fragment {
        return  CarouselRowItemFragment.newInstance(heroBannerModel?.type, heroBannerModel?.listItems?.get(position), position,heroBannerModel?.isContinueWatching,navigationMenuCallback, keyListener)

    }

}