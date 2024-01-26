package com.willow.android.mobile.views.pages.fixturesPage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.databinding.CardFilterPopupBinding
import com.willow.android.mobile.models.pages.FilterModel
import com.willow.android.mobile.models.pages.FilterTeamModel

class FixturesFilterAdapter(val context: Context): RecyclerView.Adapter<FixturesFilterAdapter.FixtureFilterItemModelViewHolder>() {
    inner class FixtureFilterItemModelViewHolder(val binding: CardFilterPopupBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(teamModel: FilterTeamModel) {
            binding.teamName.text = teamModel.name

            if (teamModel.isSelected) {
                binding.radioButton.setImageResource(R.drawable.radio_button_selected)
            } else {
                binding.radioButton.setImageResource(R.drawable.radio_button)
            }

            binding.radioButton.setOnClickListener {
                teamModel.isSelected = !teamModel.isSelected
                if (teamModel.isSelected) {
                    binding.radioButton.setImageResource(R.drawable.radio_button_selected)
                } else {
                    binding.radioButton.setImageResource(R.drawable.radio_button)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FixtureFilterItemModelViewHolder {
        return FixtureFilterItemModelViewHolder(CardFilterPopupBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: FixtureFilterItemModelViewHolder, position: Int) {
        val sectionData = FilterModel.tempFixturesTeamList[position]
        holder.setData(sectionData)
    }

    override fun getItemCount(): Int {
        return (FilterModel.tempFixturesTeamList.size)
    }
}