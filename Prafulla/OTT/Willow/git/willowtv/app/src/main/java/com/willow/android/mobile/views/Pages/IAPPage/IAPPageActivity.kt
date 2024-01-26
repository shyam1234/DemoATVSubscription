package com.willow.android.mobile.views.pages.iAPPage;

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.willow.android.R
import com.willow.android.mobile.services.analytics.AnalyticsService


class IAPPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.iap_page_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, IAPPageFragment.newInstance())
                .commitNow()
        }

        sendAnalyticsEvent()
    }


    fun sendAnalyticsEvent() {
         AnalyticsService.trackFirebaseScreen("AN_IAP_PAGE")
    }
}

