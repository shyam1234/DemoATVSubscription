package com.willow.android.tv.ui.scoreCardMatchInfo.viewHolder

import com.willow.android.databinding.ItemScoreboardRowBinding
import com.willow.android.tv.common.base.BaseViewHolder
import com.willow.android.tv.ui.scoreCardMatchInfo.model.ScorecardRow

class ScoreboardRowViewHolder(
    private val binding: ItemScoreboardRowBinding
) : BaseViewHolder(binding.root) {

    override fun bind(model: Any) {
        if (model is ScorecardRow)
            binding.model = model
    }
}
