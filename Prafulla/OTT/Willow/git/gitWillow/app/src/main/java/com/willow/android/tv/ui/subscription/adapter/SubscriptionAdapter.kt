package com.willow.android.tv.ui.subscription.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.databinding.ItemChangesSubscriptionPlanBinding
import com.willow.android.databinding.ItemSubscriptionPlanButtonBinding
import com.willow.android.databinding.ItemSubscriptionPlanTextBinding
import com.willow.android.tv.common.base.BaseViewHolder
import com.willow.android.tv.ui.subscription.ViewHolder.SubscriptionTextViewHolder
import com.willow.android.tv.ui.subscription.`interface`.ISubscriptionListener
import com.willow.android.tv.ui.subscription.model.SubscriptionChangePlan
import com.willow.android.tv.ui.subscription.model.SubscriptionPeriodButtonModel
import com.willow.android.tv.utils.GlobalConstants
import willow.android.tv.ui.subscription.ViewHolder.SubscriptionButtonViewHolder
import willow.android.tv.ui.subscription.ViewHolder.SubscriptionPlanChangeViewHolder

class SubscriptionAdapter(
    private val dataSet: List<Any>,
    private val listner: ISubscriptionListener?
) :
    RecyclerView.Adapter<BaseViewHolder>() {
    override fun getItemCount() = dataSet.size
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder {
        return when (viewType) {
            GlobalConstants.AdapterViewType.SUB_BUTTON.ordinal -> {
                SubscriptionButtonViewHolder(
                    ItemSubscriptionPlanButtonBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ), listner = listner
                )
            }
            GlobalConstants.AdapterViewType.SUB_TEXT.ordinal -> {
                SubscriptionTextViewHolder(
                    ItemSubscriptionPlanTextBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            GlobalConstants.AdapterViewType.SUB_PLAN_CHANGE.ordinal -> {
                SubscriptionPlanChangeViewHolder(
                    ItemChangesSubscriptionPlanBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ),
                    listner
                )
            }
            else -> {
                SubscriptionTextViewHolder(
                    ItemSubscriptionPlanTextBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val data = dataSet[position]
        holder.bind(data)
    }

    override fun getItemViewType(position: Int): Int {
        return when (dataSet.get(position)) {
            is SubscriptionPeriodButtonModel -> GlobalConstants.AdapterViewType.SUB_BUTTON.ordinal
            is String -> GlobalConstants.AdapterViewType.SUB_TEXT.ordinal
            is SubscriptionChangePlan -> GlobalConstants.AdapterViewType.SUB_PLAN_CHANGE.ordinal
            else -> GlobalConstants.AdapterViewType.SUB_TEXT.ordinal
        }
    }
}
