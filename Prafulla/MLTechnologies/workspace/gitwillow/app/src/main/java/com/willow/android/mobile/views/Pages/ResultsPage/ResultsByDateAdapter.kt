package com.willow.android.mobile.views.pages.resultsPage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.databinding.ResultsByDateSectionBinding
import com.willow.android.mobile.models.pages.ResultByDateModel
import com.willow.android.mobile.models.pages.ResultsPageModel

class ResultsByDateAdapter(val context: Context, val resultsPageModel: ResultsPageModel): RecyclerView.Adapter<ResultsByDateAdapter.ResultsByDateViewHolder>() {

    inner class ResultsByDateViewHolder(val binding: ResultsByDateSectionBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: ResultByDateModel) {
            binding.header.sectionHeaderTitle.text = sectionData.date

            val categoryAdapter = ResultsByDateSectionAdapter(context, sectionData.results)
            val categoryLinearLayoutManager = LinearLayoutManager(context)
            categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            binding.resultsByDateSectionRecycler.layoutManager = categoryLinearLayoutManager
            binding.resultsByDateSectionRecycler.adapter = categoryAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsByDateViewHolder {
        return ResultsByDateViewHolder(ResultsByDateSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ResultsByDateViewHolder, position: Int) {
        val sectionData = resultsPageModel.results_by_date[position]
        holder.setData(sectionData)
    }

    override fun getItemCount(): Int {
        return resultsPageModel.results_by_date.size
    }
}