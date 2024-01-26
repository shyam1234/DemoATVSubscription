package com.willow.android.tv.ui.scoreCardMatchInfo.viewHolder

import com.willow.android.databinding.ItemScoreboardErxtraDataBinding
import com.willow.android.tv.common.base.BaseViewHolder
import com.willow.android.tv.ui.scoreCardMatchInfo.model.ScorecardExtra

class ScoreboardExtraViewHolder(
    private val binding: ItemScoreboardErxtraDataBinding
) : BaseViewHolder(binding.root) {

    override fun bind(model: Any) {
        if (model is ScorecardExtra)
            binding.model = model
    }
}
