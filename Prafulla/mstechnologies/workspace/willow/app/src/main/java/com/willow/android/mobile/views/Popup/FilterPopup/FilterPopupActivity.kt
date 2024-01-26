package com.willow.android.mobile.views.popup.filterPopup

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.mobile.models.pages.FilterModel
import com.willow.android.mobile.services.ReloadService
import com.willow.android.mobile.views.pages.fixturesPage.FixturesFilterAdapter
import com.willow.android.mobile.views.pages.resultsPage.ResultsFilterAdapter

class FilterPopupActivity : Activity() {
    private var isFixtures: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_popup_activity)

        isFixtures = intent.getBooleanExtra("IS_FIXTURES", false)
        if (isFixtures) {
            setupFixturesRecycler()
            FilterModel.initTempFixturesTeams()
        } else {
            setupResultsRecycler()
            FilterModel.initTempResultsTeams()
        }


        handleFilterActivityButtonsAction()
    }

    private fun handleFilterActivityButtonsAction() {
        val filterCloseButton = findViewById<ImageView>(R.id.filter_close_button)
        filterCloseButton.setOnClickListener {
            finish()
        }

        val filterApplyButton = findViewById<AppCompatButton>(R.id.apply_button)
        filterApplyButton.setOnClickListener {
            if (isFixtures) {
                FilterModel.applyFixturesFilterSelections()
                ReloadService.reloadFixtures = true
            } else {
                FilterModel.applyResultsFilterSelections()
                ReloadService.reloadResults = true
            }
            finish()
        }

        val filterClearAllButton = findViewById<AppCompatButton>(R.id.clear_button)
        filterClearAllButton.setOnClickListener {
            if (isFixtures) {
                FilterModel.clearAllFixtureSelections()
                ReloadService.reloadFixtures = true
            } else {
                FilterModel.clearAllResultSelections()
                ReloadService.reloadResults = true
            }
            finish()
        }
    }

    private fun setupFixturesRecycler() {
        val filterRecyler = findViewById<RecyclerView>(R.id.filter_recycler)
        val categoryAdapter = FixturesFilterAdapter(this)
        val categoryLinearLayoutManager = LinearLayoutManager(this)
        categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        filterRecyler.layoutManager = categoryLinearLayoutManager
        filterRecyler.adapter = categoryAdapter
    }

    private fun setupResultsRecycler() {
        val filterRecyler = findViewById<RecyclerView>(R.id.filter_recycler)
        val categoryAdapter = ResultsFilterAdapter(this)
        val categoryLinearLayoutManager = LinearLayoutManager(this)
        categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        filterRecyler.layoutManager = categoryLinearLayoutManager
        filterRecyler.adapter = categoryAdapter
    }
}