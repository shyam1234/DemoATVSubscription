package com.willow.android.mobile.views.pages.onboardingPage

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class OnboardingAdapter(fm: FragmentManager, screensList: List<Fragment>): FragmentPagerAdapter(fm) {
    val onboardingScreensList = screensList

    override fun getCount(): Int {
        return onboardingScreensList.size
    }

    override fun getItem(position: Int): Fragment {
        return onboardingScreensList[position]
    }
}