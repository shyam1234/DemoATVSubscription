package com.willow.android.mobile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.cast.framework.CastContext
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.willow.android.R
import com.willow.android.databinding.ActivityMainNewBinding
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.configs.ResultCodes
import com.willow.android.mobile.models.PreferencesModel
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.models.pages.MultipleLiveSourcesModel
import com.willow.android.mobile.services.LocalNotificationService
import com.willow.android.mobile.services.ReloadService
import com.willow.android.mobile.services.analytics.AnalyticsService
import com.willow.android.mobile.views.pages.matchCenterPage.MatchCenterPageActivity
import com.willow.android.mobile.views.popup.livePopup.LiveSourcesDialog
import com.willow.android.mobile.views.popup.messagePopup.MessagePopupActivity

interface MainTabsSelectionInterface {
    fun setCurrentFragment(fragmentId: Int)
}

class MainActivity : AppCompatActivity(), MainTabsSelectionInterface {
    private lateinit var binding: ActivityMainNewBinding
    private lateinit var navView: BottomNavigationView

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Disable dark mode in the app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityMainNewBinding.inflate(layoutInflater)
        navView = binding.navView
        setContentView(binding.root)

        setupNavBar()
        setupCast()
        setupLocalNotification()

        AnalyticsService.initAnalyticsService(getApplicationContext())
    }

    override fun onPause() {
        super.onPause()
        ReloadService.reloadHome = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == ResultCodes.POLLER_ERROR) {
            showMessage(MessageConfig.pollerDescription)
        }
    }

    fun setupNavBar() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
        navView.setItemIconTintList(null)

        if (UserModel.cc.lowercase() == "ca") {
            binding.pageHeader.pageHeaderLogo.setImageResource(R.drawable.willow_ca_white)
        }
    }

    override fun setCurrentFragment(fragmentId: Int) {
        navView.selectedItemId = fragmentId
    }

    fun setPageTitle(title: String) {
        binding.pageHeader.pageHeaderTitle.text = title
    }

    fun setupCast() {
        val castContext = CastContext.getSharedInstance(this)
        Log.d("castContext", castContext.toString())
    }

    fun setupLocalNotification() {
        LocalNotificationService.initNotificationChannel(this)
        LocalNotificationService.initNotificationListFromLocalStorage()
    }

    fun showMessage(message: String) {
        if (message.isEmpty()) { return }

        val intent = Intent(this, MessagePopupActivity::class.java).apply {}
        intent.putExtra("MESSAGE", message)
        startActivity(intent)
    }

    /** !!!!!!!! Match Center Helper Methods !!!!!!!! */
    fun launchMatchCenterActivity(isLive: Boolean, matchId: String, seriesId: String, streamModel: MultipleLiveSourcesModel?) {
        if (isLive && (PreferencesModel.showSources) && (streamModel != null)) {
            val newFragment = LiveSourcesDialog.newInstance(streamModel)
            newFragment.onResult = {
                newFragment.dismiss()
                val intent = Intent(this, MatchCenterPageActivity::class.java).apply {
                    putExtra("MATCH_ID", matchId)
                    putExtra("SERIES_ID", seriesId)
                    putExtra("LIVE_PRIORITY", it.priority)
                }

                startActivityForResult(intent, ResultCodes.POLLER_ERROR)
            }

            newFragment.show(supportFragmentManager, "dialog")
        } else {
            launchMatchCenter(matchId, seriesId)
        }
    }

    fun launchMatchCenter(matchId: String, seriesId: String) {
        val intent = Intent(this, MatchCenterPageActivity::class.java).apply {
            putExtra("MATCH_ID", matchId)
            putExtra("SERIES_ID", seriesId)
        }

        startActivityForResult(intent, ResultCodes.POLLER_ERROR)
    }
    /** !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */

}