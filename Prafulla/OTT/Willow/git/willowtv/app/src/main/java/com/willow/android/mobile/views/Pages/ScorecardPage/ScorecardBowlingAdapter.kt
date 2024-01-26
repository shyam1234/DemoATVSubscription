package com.willow.android.mobile.views.pages.scorecardPage

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.databinding.ScorecardCellBinding
import com.willow.android.databinding.ScorecardTitleCellBinding
import com.willow.android.mobile.models.pages.BowlerInningSummary
import com.willow.android.mobile.models.pages.ScorecardInningModel
import com.willow.android.mobile.models.video.SuggestedVideosModel
import com.willow.android.mobile.views.pages.PagesNavigator

class ScorecardBowlingAdapter(val context: Context, val scorecardInningModel: ScorecardInningModel): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TITLE_CELL = 0
    val CELL = 1

    inner class ScorecardTitleViewHolder(val binding: ScorecardTitleCellBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData() {
            binding.batsman.text = "Bowlers"
            binding.runs.text = "O"
            binding.balls.text = "M"
            binding.fours.text = "R"
            binding.six.text = "W"
            binding.strikeRate.text = "Eco"
        }
    }

    inner class ScorecardInningViewHolder(val binding: ScorecardCellBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: BowlerInningSummary, isEven: Boolean) {
            if (isEven) {
                binding.root.setBackgroundColor(Color.WHITE)
            }
            binding.batsman.text = sectionData.Name
            binding.runs.text = sectionData.Overs
            binding.balls.text = sectionData.Maidens
            binding.fours.text = sectionData.Runs
            binding.six.text = sectionData.Wickets
            binding.strikeRate.text = sectionData.Economy

            if (sectionData.videos.size > 0) {
                itemView.setOnClickListener {
                    val suggestedVideosModel = SuggestedVideosModel()
                    suggestedVideosModel.setData(sectionData.videos)
                    PagesNavigator.launchVideoDetailPage(context, sectionData.videos[0], suggestedVideosModel, true)
                }
                binding.playIcon.setImageResource(R.drawable.play)
            } else {
                binding.playIcon.setImageResource(R.drawable.ic_play_26)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        when (viewType) {
            TITLE_CELL -> return ScorecardTitleViewHolder(ScorecardTitleCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
        return ScorecardInningViewHolder(ScorecardCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ScorecardInningViewHolder) {
            var isEven = false
            if ((position % 2) > 0) {
                isEven = true
            }
            val sectionData = scorecardInningModel.BowlerInningSummaries[position - 1]
            holder.setData(sectionData, isEven)
        } else if (holder is ScorecardTitleViewHolder) {
            holder.setData()
        }
    }

    override fun getItemCount(): Int {
        return scorecardInningModel.BowlerInningSummaries.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TITLE_CELL
        }

        return CELL
    }
}