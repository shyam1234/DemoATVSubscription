package com.willow.android.tv.ui.scoreCardMatchInfo.viewHolder

import com.willow.android.databinding.ItemMatchinfoRowdataBinding
import com.willow.android.tv.common.base.BaseViewHolder
import com.willow.android.tv.ui.scoreCardMatchInfo.model.MatchInfoRow

class MatchInfoRowViewHolder(
    private val binding: ItemMatchinfoRowdataBinding
) : BaseViewHolder(binding.root) {

    override fun bind(model: Any) {
        if (model is MatchInfoRow)
            binding.model = model
    }
}
