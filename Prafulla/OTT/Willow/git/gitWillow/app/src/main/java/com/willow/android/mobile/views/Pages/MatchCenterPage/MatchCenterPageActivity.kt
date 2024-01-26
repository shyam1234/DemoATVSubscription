package com.willow.android.mobile.views.pages.matchCenterPage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.willow.android.R

class MatchCenterPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.match_center_page_activity)

        val seriesId = intent.getStringExtra("SERIES_ID")
        val matchId = intent.getStringExtra("MATCH_ID")
        val livePriority = intent.getStringExtra("LIVE_PRIORITY")

        if (matchId != null && seriesId != null) {
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container,
                        MatchCenterPageFragment.newInstance(
                            seriesId = seriesId,
                            matchId = matchId,
                            livePriority = livePriority
                        )
                    )
                    .commitNow()
            }
        }
    }
}