package com.willow.android.mobile.views.pages.preferencesPage

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.mobile.models.PreferencesModel

class PreferencesPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preferences_page_activity)

        val preferencesModel = PreferencesModel
        val categoryAdapter = PreferencesPageAdapter(this, preferencesModel)
        val categoryLinearLayoutManager = LinearLayoutManager(this)
        categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        val recycler: RecyclerView = this.findViewById(R.id.preferences_page_recycler)
        recycler.layoutManager = categoryLinearLayoutManager
        recycler.adapter = categoryAdapter

        // Add divider decorator
        val itemDecor = DividerItemDecoration(this, RecyclerView.VERTICAL)
        val dividerDrawable = this?.let { it1 -> ContextCompat.getDrawable(it1, R.drawable.vertical_divider) }
        if (dividerDrawable != null) {
            itemDecor.setDrawable(dividerDrawable)
        }
        recycler.addItemDecoration(itemDecor)

        setPageTitle()
    }


    private fun setPageTitle() {
        val header = findViewById<View>(R.id.preferences_page_header)
        val headerTitle = header.findViewById<TextView>(R.id.page_header_title)
        headerTitle.text = "Preferences"
    }
}