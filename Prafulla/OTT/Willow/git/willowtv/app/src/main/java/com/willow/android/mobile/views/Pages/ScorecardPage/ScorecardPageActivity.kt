package com.willow.android.mobile.views.pages.scorecardPage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.willow.android.R

class ScorecardPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scorecard_page_activity)

        val seriesId = intent.getStringExtra("SERIES_ID")
        val matchId = intent.getStringExtra("MATCH_ID")

        if (matchId != null && seriesId != null) {
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container,
                        ScorecardPageFragment.newInstance(seriesId = seriesId, matchId = matchId)
                    )
                    .commitNow()
            }
        }
    }
}