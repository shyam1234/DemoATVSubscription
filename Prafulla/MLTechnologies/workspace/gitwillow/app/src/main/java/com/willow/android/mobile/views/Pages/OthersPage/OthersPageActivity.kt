package com.willow.android.mobile.views.pages.othersPage

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import tv.willow.Models.SettingsItemModel

class OthersPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.others_page_activity)
        setPageTitle()

        val settingsItemModel = intent.getSerializableExtra("SETTINGS_ITEM") as? SettingsItemModel

        if (settingsItemModel != null) {
            val categoryAdapter = OthersPageAdapter(this, settingsItemModel)
            val categoryLinearLayoutManager = LinearLayoutManager(this)
            categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL

            val recycler: RecyclerView = this.findViewById(R.id.others_page_recycler)
            recycler.layoutManager = categoryLinearLayoutManager
            recycler.adapter = categoryAdapter

            // Add divider decorator
            val itemDecor = DividerItemDecoration(this, RecyclerView.VERTICAL)
            val dividerDrawable = this?.let { it1 -> ContextCompat.getDrawable(it1, R.drawable.vertical_divider) }
            if (dividerDrawable != null) {
                itemDecor.setDrawable(dividerDrawable)
            }
            recycler.addItemDecoration(itemDecor)
        }
    }

    private fun setPageTitle() {
        val header = findViewById<View>(R.id.others_page_header)
        val headerTitle = header.findViewById<TextView>(R.id.page_header_title)
        headerTitle.text = "Others"
    }
}