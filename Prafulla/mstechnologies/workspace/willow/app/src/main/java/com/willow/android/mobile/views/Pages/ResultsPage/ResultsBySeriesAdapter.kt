package com.willow.android.mobile.views.pages.resultsPage


import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.databinding.CardResultBinding
import com.willow.android.databinding.CompTeamScoreBinding
import com.willow.android.databinding.HeaderExpandableBinding
import com.willow.android.mobile.MainActivity
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.models.PreferencesModel
import com.willow.android.mobile.models.pages.ResultsPageModel
import com.willow.android.mobile.models.pages.UIResultModel
import com.willow.android.mobile.views.pages.videosPage.VideosSectionAdapter
import com.willow.android.tv.utils.ImageUtility


class ResultsBySeriesAdapter(val context: Context, val resultsPageModel: ResultsPageModel): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ResultsBySeriesHeaderViewHolder(val binding: HeaderExpandableBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(uiResult: UIResultModel, position: Int) {
            binding.eHeaderTitle.text = uiResult.result.series_name
            binding.eHeaderSubtitle.text = uiResult.result.seriesSubtitle
            binding.root.setOnClickListener {
                uiResult.isExpanded = !uiResult.isExpanded
                if (uiResult.isExpanded) {
                    expandSeries(position)
                } else {
                    collapseSeries(position)
                }
            }

            if (uiResult.isExpanded) {
                binding.eHeaderIcon.setImageResource(R.drawable.minus)
            } else {
                binding.eHeaderIcon.setImageResource(R.drawable.plus)
            }

        }
    }

    inner class ResultsBySeriesChildViewHolder(val binding: CardResultBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(resultsData: UIResultModel, position: Int) {
            binding.seriesName.text = resultsData.result.series_name

            if (PreferencesModel.showResults) {
                binding.matchName.text = resultsData.result.result_with_name
            } else {
                binding.matchName.text = resultsData.result.match_short_name
            }

            setTeamData(binding.teamOne, resultsData.result.team_one_logo, resultsData.result.team_one_fname, resultsData.result.team_one_score, resultsData.result.teamOneWon)
            setTeamData(binding.teamTwo, resultsData.result.team_two_logo, resultsData.result.team_two_fname, resultsData.result.team_two_score, resultsData.result.teamTwoWon)

            if (resultsData.result.tve_only_series) {
                binding.tveEverywhereText.text = MessageConfig.tveOnlyMessage
                binding.tveEverywhereText.visibility = View.VISIBLE
            } else {
                binding.tveEverywhereText.visibility = View.GONE
            }

            val categoryAdapter = VideosSectionAdapter(context, videosSectionModel = resultsData.result.videosSectionModel)
            val categoryLinearLayoutManager = LinearLayoutManager(context)
            categoryLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            binding.resultCardRecycler.layoutManager = categoryLinearLayoutManager
            binding.resultCardRecycler.adapter = categoryAdapter

            /** Change Button Color for Live Match */
            if (resultsData.result.is_live) {
                if (binding.matchCenterButton is ImageView) {
                    binding.matchCenterButton.setImageResource(R.drawable.mc_live_arrow)
                } else if (binding.matchCenterButton is AppCompatButton) {
                    binding.matchCenterButton.setBackgroundResource(R.drawable.live_red_button)
                    binding.matchCenterButton.text = "WATCH LIVE"
                }
            }

            if (resultsData.result.match_center_present) {
                binding.matchCenterButton.visibility = View.VISIBLE
            } else {
                binding.matchCenterButton.visibility = View.GONE
            }

            binding.matchCenterButton.setOnClickListener{
                if (context is MainActivity) {
                    context.launchMatchCenterActivity(resultsData.result.is_live, resultsData.result.match_id, resultsData.result.series_id, resultsData.result.sources)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            UIResultModel.TYPE_RESULT_HEADER -> {ResultsBySeriesHeaderViewHolder(HeaderExpandableBinding.inflate(LayoutInflater.from(parent.context), parent, false))}

            UIResultModel.TYPE_RESULT_ITEM -> { ResultsBySeriesChildViewHolder(CardResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))  }

            else -> {ResultsBySeriesHeaderViewHolder(HeaderExpandableBinding.inflate(LayoutInflater.from(parent.context), parent, false))}
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val uiResultData = resultsPageModel.uiResults[position]

        when(uiResultData.type){
            UIResultModel.TYPE_RESULT_HEADER -> {
                if(holder is ResultsBySeriesHeaderViewHolder) {
                    holder.setData(uiResultData, position)
                }
            }

            UIResultModel.TYPE_RESULT_ITEM -> {
                if(holder is ResultsBySeriesChildViewHolder) {
                    holder.setData(uiResultData, position)
                }
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return resultsPageModel.uiResults[position].type
    }
    override fun getItemCount(): Int {
        return resultsPageModel.uiResults.size
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

    private fun expandSeries(position: Int){
        val row = resultsPageModel.uiResults[position]
        row.isExpanded = true

        var selectedSeriesResults = mutableListOf<UIResultModel>()
        for (series in resultsPageModel.results_by_series) {
            if (series.series_id == row.result.series_id) {
                selectedSeriesResults = series.results
                break
            }
        }

        var nextPosition = position
        for (uiResult in selectedSeriesResults) {
            if (uiResult.type == UIResultModel.TYPE_RESULT_ITEM) {
                resultsPageModel.uiResults.add(++nextPosition, uiResult)
            }
        }
        notifyDataSetChanged()
    }

    private fun collapseSeries(position: Int) {
        val row = resultsPageModel.uiResults[position]

        var selectedSeriesResults = mutableListOf<UIResultModel>()
        for (series in resultsPageModel.results_by_series) {
            if (series.series_id == row.result.series_id) {
                selectedSeriesResults = series.results
                break
            }
        }

        val startIndex = position + 1
        val endIndex = position + selectedSeriesResults.size
        for (i in startIndex until endIndex) {
            if (resultsPageModel.uiResults[startIndex].type == UIResultModel.TYPE_RESULT_ITEM) {
                resultsPageModel.uiResults.removeAt(startIndex)
            }
        }

        notifyDataSetChanged()
    }
}