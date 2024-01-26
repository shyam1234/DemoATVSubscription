package com.willow.android.mobile.views.pages.tVELoginPage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.willow.android.R

class TVELoginPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tve_login_page_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TVELoginPageFragment.newInstance())
                .commitNow()
        }
    }
}