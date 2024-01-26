package com.willow.android.mobile.views.pages.preferencesPage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.databinding.PreferencesPageCardBinding
import com.willow.android.mobile.models.PreferencesModel

class PreferencesPageAdapter(val context: Context, val preferencesModel: PreferencesModel): RecyclerView.Adapter<PreferencesPageAdapter.PreferencesViewHolder>() {
    inner class PreferencesViewHolder(val binding: PreferencesPageCardBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(title: String) {
            binding.preferencesPageCardTitle.text = title

            when(title) {
                "Show Sources" -> binding.preferenceSwitch.isChecked = PreferencesModel.showSources
                "Show Scores" -> binding.preferenceSwitch.isChecked = PreferencesModel.showScores
                "Show Results" -> binding.preferenceSwitch.isChecked = PreferencesModel.showResults
            }

            binding.preferenceSwitch.setOnCheckedChangeListener {  _, isChecked ->
                when(title) {
                    "Show Sources" -> PreferencesModel.updateShowSources(isChecked)
                    "Show Scores" -> PreferencesModel.updateShowScores(isChecked)
                    "Show Results" -> PreferencesModel.updateShowResults(isChecked)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferencesViewHolder {
        return PreferencesViewHolder(PreferencesPageCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PreferencesViewHolder, position: Int) {
        val sectionData = preferencesModel.items[position]
        holder.setData(sectionData)
    }

    override fun getItemCount(): Int {
        return preferencesModel.items.size
    }
}