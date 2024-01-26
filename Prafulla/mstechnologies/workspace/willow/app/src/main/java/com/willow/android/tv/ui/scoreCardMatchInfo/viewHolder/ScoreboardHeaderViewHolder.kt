package com.willow.android.tv.ui.scoreCardMatchInfo.viewHolder

import com.willow.android.databinding.ItemScoreboardHeaderBinding
import com.willow.android.tv.common.base.BaseViewHolder
import com.willow.android.tv.ui.scoreCardMatchInfo.model.ScorecardHeader

class ScoreboardHeaderViewHolder(
    private val binding: ItemScoreboardHeaderBinding
) : BaseViewHolder(binding.root) {

    override fun bind(model: Any) {
        if (model is ScorecardHeader)
            binding.model = model
    }
}
