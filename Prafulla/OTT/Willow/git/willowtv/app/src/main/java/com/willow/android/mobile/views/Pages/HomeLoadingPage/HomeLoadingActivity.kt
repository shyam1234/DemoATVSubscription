package com.willow.android.mobile.views.pages.homeLoadingPage

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.willow.android.R
import com.willow.android.mobile.views.pages.homePage.FirebaseDeeplinkState

class HomeLoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_loading_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HomeLoadingFragment.newInstance())
                .commitNow()
        }

        handleDeepLink()
        FirebaseCrashlytics.getInstance().setCustomKey("deviceType", "AndroidMobile")
    }

    private fun handleDeepLink() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    FirebaseDeeplinkState.receivedUri = deepLink
                }

            }
            .addOnFailureListener(this) { e -> Log.w("FirebaseDynamicLink", "getDynamicLink:onFailure", e) }
    }

}