package com.willow.android.mobile.views.pages.loginPage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.willow.android.R
import com.willow.android.mobile.services.analytics.AnalyticsService


class LoginPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginPageFragment.newInstance())
                .commitNow()
        }

        sendAnalyticsEvent()
    }

    fun sendAnalyticsEvent() {
         AnalyticsService.trackFirebaseScreen("AN_IAP_PAGE")
    }
}

