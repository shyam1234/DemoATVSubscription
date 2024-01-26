package com.willow.android.mobile.views.pages.forgotPasswordPage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.willow.android.R

class ForgotPasswordPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password_page_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.forgot_password_page, ForgotPasswordPageFragment.newInstance())
                .commitNow()
        }
    }
}