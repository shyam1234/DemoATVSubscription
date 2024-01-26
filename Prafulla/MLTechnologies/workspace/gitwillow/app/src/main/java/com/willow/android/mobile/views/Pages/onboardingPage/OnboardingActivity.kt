package com.willow.android.mobile.views.pages.onboardingPage

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.willow.android.R

class OnboardingActivity : AppCompatActivity() {
    private lateinit var skipButton: Button
    private lateinit var nextButton: Button

    var onboardingViewPager: ViewPager? = null
    var onboardingAdapter: OnboardingAdapter? = null
    val onboardingScreensList = listOf(OnboardingLiveFragment(), OnboardingHighlightsFragment(), OnboardingBallByBallFragment())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_activity)

        onboardingViewPager = findViewById<ViewPager>(R.id.onboarding_view_pager)
        onboardingAdapter = OnboardingAdapter(supportFragmentManager, onboardingScreensList)
        onboardingViewPager?.adapter = onboardingAdapter

        skipButton = findViewById<Button>(R.id.skip_button)
        nextButton = findViewById<Button>(R.id.next_button)
        skipButton.visibility = View.INVISIBLE

        nextButton.setOnClickListener {
            gotoNextScreen()
        }

        skipButton.setOnClickListener {
            gotoPreviousScreen()
        }
    }

    fun gotoNextScreen() {
        if ((onboardingAdapter != null) && (onboardingViewPager != null)) {
            if (onboardingViewPager!!.currentItem < onboardingScreensList.size) {
                onboardingViewPager?.setCurrentItem(onboardingViewPager!!.currentItem + 1 )
                onboardingAdapter?.notifyDataSetChanged()

                handleNextButtonVisibility()
            }
        }
    }

    fun gotoPreviousScreen() {
        if ((onboardingAdapter != null) && (onboardingViewPager != null)) {
            if (onboardingViewPager!!.currentItem > 0) {
                onboardingViewPager?.setCurrentItem(onboardingViewPager!!.currentItem - 1 )
                onboardingAdapter?.notifyDataSetChanged()

                handleNextButtonVisibility()
            }
        }
    }

    private fun handleNextButtonVisibility() {
        if (onboardingViewPager!!.currentItem == (onboardingScreensList.size - 1)) {
            nextButton.visibility = View.INVISIBLE
        } else {
            nextButton.visibility = View.VISIBLE
        }

        if (onboardingViewPager!!.currentItem == 0) {
            skipButton.visibility = View.INVISIBLE
        } else {
            skipButton.visibility = View.VISIBLE
        }
    }
}