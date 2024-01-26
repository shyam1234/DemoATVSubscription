package com.willow.android.tv.ui.scoreCardMatchInfo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.databinding.ItemMatchinfoParadataBinding
import com.willow.android.databinding.ItemMatchinfoRowdataBinding
import com.willow.android.databinding.ItemScoreboardErxtraDataBinding
import com.willow.android.databinding.ItemScoreboardHeaderBinding
import com.willow.android.databinding.ItemScoreboardRowBinding
import com.willow.android.databinding.ItemScoreboardTotalDataBinding
import com.willow.android.tv.common.base.BaseViewHolder
import com.willow.android.tv.ui.scoreCardMatchInfo.model.MatchInfoParaData
import com.willow.android.tv.ui.scoreCardMatchInfo.model.MatchInfoRow
import com.willow.android.tv.ui.scoreCardMatchInfo.model.ScorecardExtra
import com.willow.android.tv.ui.scoreCardMatchInfo.model.ScorecardHeader
import com.willow.android.tv.ui.scoreCardMatchInfo.model.ScorecardRow
import com.willow.android.tv.ui.scoreCardMatchInfo.model.ScorecardTotal
import com.willow.android.tv.ui.scoreCardMatchInfo.viewHolder.MatchInfoParaViewHolder
import com.willow.android.tv.ui.scoreCardMatchInfo.viewHolder.MatchInfoRowViewHolder
import com.willow.android.tv.ui.scoreCardMatchInfo.viewHolder.ScoreboardExtraViewHolder
import com.willow.android.tv.ui.scoreCardMatchInfo.viewHolder.ScoreboardHeaderViewHolder
import com.willow.android.tv.ui.scoreCardMatchInfo.viewHolder.ScoreboardRowViewHolder
import com.willow.android.tv.ui.scoreCardMatchInfo.viewHolder.ScoreboardTotalViewHolder
import com.willow.android.tv.utils.GlobalConstants

class ScoreCardAdapter(
    private val dataSet: List<Any>?
) :
    RecyclerView.Adapter<BaseViewHolder>() {
    override fun getItemCount() = dataSet?.size?:0
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder {
        return when (viewType) {
            GlobalConstants.ScoreCard.SCORECARD_HEADER.ordinal -> {
                ScoreboardHeaderViewHolder(
                    ItemScoreboardHeaderBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            GlobalConstants.ScoreCard.SCORECARD_ROW.ordinal -> {
                ScoreboardRowViewHolder(
                    ItemScoreboardRowBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            GlobalConstants.ScoreCard.MATCHINFO_ROW.ordinal -> {
                MatchInfoRowViewHolder(
                    ItemMatchinfoRowdataBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            GlobalConstants.ScoreCard.MATCHINFO_PARA_DATA.ordinal -> {
                MatchInfoParaViewHolder(
                    ItemMatchinfoParadataBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            GlobalConstants.ScoreCard.SCORECARD_EXTRA.ordinal -> {
                ScoreboardExtraViewHolder(
                    ItemScoreboardErxtraDataBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            GlobalConstants.ScoreCard.SCORECARD_TOTAL.ordinal -> {
                ScoreboardTotalViewHolder(
                    ItemScoreboardTotalDataBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            else -> {
                ScoreboardHeaderViewHolder(
                    ItemScoreboardHeaderBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val data = dataSet?.get(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (dataSet?.get(position)) {
            is ScorecardHeader -> GlobalConstants.ScoreCard.SCORECARD_HEADER.ordinal
            is ScorecardRow -> GlobalConstants.ScoreCard.SCORECARD_ROW.ordinal
            is ScorecardExtra -> GlobalConstants.ScoreCard.SCORECARD_EXTRA.ordinal
            is ScorecardTotal -> GlobalConstants.ScoreCard.SCORECARD_TOTAL.ordinal
            is MatchInfoRow -> GlobalConstants.ScoreCard.MATCHINFO_ROW.ordinal
            is MatchInfoParaData -> GlobalConstants.ScoreCard.MATCHINFO_PARA_DATA.ordinal
            else -> GlobalConstants.ScoreCard.SCORECARD_HEADER.ordinal
        }
    }
}
