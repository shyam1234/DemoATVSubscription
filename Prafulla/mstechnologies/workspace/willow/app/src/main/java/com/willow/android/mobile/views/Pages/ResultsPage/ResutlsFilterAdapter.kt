package com.willow.android.mobile.views.pages.resultsPage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.databinding.CardFilterPopupBinding
import com.willow.android.mobile.models.pages.FilterModel
import com.willow.android.mobile.models.pages.FilterTeamModel

class ResultsFilterAdapter(val context: Context): RecyclerView.Adapter<ResultsFilterAdapter.ResultFilterItemModelViewHolder>() {
    inner class ResultFilterItemModelViewHolder(val binding: CardFilterPopupBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(teamModel: FilterTeamModel) {
            binding.teamName.text = teamModel.name

            if (teamModel.isSelected) {
                binding.radioButton.setImageResource(R.drawable.radio_button_selected)
            } else {
                binding.radioButton.setImageResource(R.drawable.radio_button)
            }

            binding.radioButton.setOnClickListener {
                if (teamModel.isSelected) {
                    teamModel.isSelected = false
                    binding.radioButton.setImageResource(R.drawable.radio_button)
                } else {
                    teamModel.isSelected = true
                    binding.radioButton.setImageResource(R.drawable.radio_button_selected)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultFilterItemModelViewHolder {
        return ResultFilterItemModelViewHolder(CardFilterPopupBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ResultFilterItemModelViewHolder, position: Int) {
        val sectionData = FilterModel.tempResultsTeamList[position]
        holder.setData(sectionData)
    }

    override fun getItemCount(): Int {
        return (FilterModel.tempResultsTeamList.size)
    }
}