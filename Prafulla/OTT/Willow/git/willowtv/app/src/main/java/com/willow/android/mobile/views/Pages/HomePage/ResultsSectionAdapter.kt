package com.willow.android.mobile.views.pages.homePage

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.databinding.CardResultBinding
import com.willow.android.databinding.CompTeamScoreBinding
import com.willow.android.mobile.MainActivity
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.models.PreferencesModel
import com.willow.android.mobile.models.pages.ResultModel
import com.willow.android.mobile.views.pages.videosPage.VideosSectionAdapter
import com.willow.android.tv.utils.ImageUtility

class ResultsSectionAdapter(val context: Context, val resultsList: MutableList<ResultModel>): RecyclerView.Adapter<ResultsSectionAdapter.ResultsSectionViewHolder>() {

    inner class ResultsSectionViewHolder(val binding: CardResultBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: ResultModel) {
            binding.seriesName.text = sectionData.series_name

            if (PreferencesModel.showResults) {
                binding.matchName.text = sectionData.result_with_name
            } else {
                binding.matchName.text = sectionData.match_short_name
            }

            setTeamData(binding.teamOne, sectionData.team_one_logo, sectionData.team_one_fname, sectionData.team_one_score, sectionData.teamOneWon)
            setTeamData(binding.teamTwo, sectionData.team_two_logo, sectionData.team_two_fname, sectionData.team_two_score, sectionData.teamTwoWon)

            if (sectionData.tve_only_series) {
                binding.tveEverywhereText.text = MessageConfig.tveOnlyMessage
                binding.tveEverywhereText.visibility = View.VISIBLE
            } else {
                binding.tveEverywhereText.visibility = View.GONE
            }

            val categoryAdapter = VideosSectionAdapter(context, videosSectionModel = sectionData.videosSectionModel)
            val categoryLinearLayoutManager = LinearLayoutManager(context)
            categoryLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            binding.resultCardRecycler.layoutManager = categoryLinearLayoutManager
            binding.resultCardRecycler.adapter = categoryAdapter

            if (sectionData.match_center_present) {
                binding.matchCenterButton.visibility = View.VISIBLE
            } else {
                binding.matchCenterButton.visibility = View.GONE
            }

            binding.matchCenterButton.setOnClickListener {
                if (context is MainActivity) {
                    context.launchMatchCenterActivity(sectionData.is_live, sectionData.match_id, sectionData.series_id, sectionData.sources)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsSectionViewHolder {
        return ResultsSectionViewHolder(CardResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ResultsSectionViewHolder, position: Int) {
        val sectionData = resultsList[position]
        holder.setData(sectionData)
    }

    override fun getItemCount(): Int {
        return resultsList.size
    }

    private fun setTeamData(binding: CompTeamScoreBinding, logo: String, name: String, score: String, isWinningTeam: Boolean) {
        ImageUtility.loadImageInto(logo,binding.icon)

        binding.name.text = name
        if (PreferencesModel.showScores) {
            binding.score.text = score
        }

        if (isWinningTeam && PreferencesModel.showResults) {
            binding.name.typeface = Typeface.DEFAULT_BOLD
            binding.score.typeface = Typeface.DEFAULT_BOLD

            binding.name.setTextColor(context.resources.getColor(R.color.team_won))
            binding.score.setTextColor(context.resources.getColor(R.color.team_won))
        }
    }
}