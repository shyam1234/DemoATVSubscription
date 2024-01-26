package com.willow.android.mobile.views.pages.fixturesPage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.databinding.FixturesByDateSectionBinding
import com.willow.android.mobile.models.pages.FixtureByDateModel
import com.willow.android.mobile.models.pages.FixturesPageModel


class FixturesByDateAdapter(val context: Context, val fixturesPageModel: FixturesPageModel): RecyclerView.Adapter<FixturesByDateAdapter.FixturesByDateViewHolder>() {

    inner class FixturesByDateViewHolder(val binding: FixturesByDateSectionBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: FixtureByDateModel) {
            binding.fixturesSectionHeader.sectionHeaderTitle.text = sectionData.date

            val categoryAdapter = FixturesByDateSectionAdapter(context, sectionData.fixtures)
            val categoryLinearLayoutManager = LinearLayoutManager(context)
            categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            binding.fixturesByDateSectionRecycler.layoutManager = categoryLinearLayoutManager
            binding.fixturesByDateSectionRecycler.adapter = categoryAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FixturesByDateViewHolder {
        return FixturesByDateViewHolder(FixturesByDateSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FixturesByDateViewHolder, position: Int) {
        val sectionData = fixturesPageModel.fixtures_by_date[position]
        holder.setData(sectionData)
    }

    override fun getItemCount(): Int {
        return fixturesPageModel.fixtures_by_date.size
    }
}