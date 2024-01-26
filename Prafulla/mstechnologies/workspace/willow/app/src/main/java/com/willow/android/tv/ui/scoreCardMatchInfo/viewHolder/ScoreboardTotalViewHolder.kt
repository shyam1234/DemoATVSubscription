package com.willow.android.tv.ui.scoreCardMatchInfo.viewHolder

import com.willow.android.databinding.ItemScoreboardTotalDataBinding
import com.willow.android.tv.common.base.BaseViewHolder
import com.willow.android.tv.ui.scoreCardMatchInfo.model.ScorecardTotal

class ScoreboardTotalViewHolder(
    private val binding: ItemScoreboardTotalDataBinding
) : BaseViewHolder(binding.root) {

    override fun bind(model: Any) {
        if (model is ScorecardTotal)
            binding.model = model
    }
}
