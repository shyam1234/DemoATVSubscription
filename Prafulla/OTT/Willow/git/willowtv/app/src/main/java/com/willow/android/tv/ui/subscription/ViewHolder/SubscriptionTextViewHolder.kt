package com.willow.android.tv.ui.subscription.ViewHolder

import com.willow.android.databinding.ItemSubscriptionPlanTextBinding
import com.willow.android.tv.common.base.BaseViewHolder

class SubscriptionTextViewHolder(
    private val binding: ItemSubscriptionPlanTextBinding
    ) : BaseViewHolder(binding.root) {

    override fun bind(model: Any) {
        if (model is String)
            binding.model = model
    }
}