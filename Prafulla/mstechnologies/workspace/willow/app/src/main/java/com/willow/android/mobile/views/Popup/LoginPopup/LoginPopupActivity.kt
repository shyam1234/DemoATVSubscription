package com.willow.android.mobile.views.popup.loginPopup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.willow.android.R

class LoginPopupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_popup_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginPopupFragment.newInstance())
                .commitNow()
        }
    }
}