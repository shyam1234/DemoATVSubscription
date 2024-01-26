package com.willow.android.tv.ui.scoreCardMatchInfo.viewHolder

import com.willow.android.databinding.ItemMatchinfoParadataBinding
import com.willow.android.tv.common.base.BaseViewHolder
import com.willow.android.tv.ui.scoreCardMatchInfo.model.MatchInfoParaData

class MatchInfoParaViewHolder(
    private val binding: ItemMatchinfoParadataBinding
) : BaseViewHolder(binding.root) {

    override fun bind(model: Any) {
        if (model is MatchInfoParaData)
            binding.model = model
    }
}
