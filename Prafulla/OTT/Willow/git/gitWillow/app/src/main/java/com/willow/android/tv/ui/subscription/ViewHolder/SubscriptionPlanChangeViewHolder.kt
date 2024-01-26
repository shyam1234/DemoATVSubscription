package willow.android.tv.ui.subscription.ViewHolder

import com.willow.android.databinding.ItemChangesSubscriptionPlanBinding
import com.willow.android.tv.common.base.BaseViewHolder
import com.willow.android.tv.ui.subscription.`interface`.ISubscriptionListener
import com.willow.android.tv.ui.subscription.model.SubscriptionChangePlan

class SubscriptionPlanChangeViewHolder(
    private val binding: ItemChangesSubscriptionPlanBinding,
    private val listner: ISubscriptionListener?
) : BaseViewHolder(binding.root) {
    override fun bind(model: Any) {
        if (model is SubscriptionChangePlan) {
            binding.model = model
            binding.planChangeLyt.setOnClickListener {
                listner?.onPlanChangeClick(model.planUd)
            }
            binding.planChangeLyt.setOnFocusChangeListener { v, hasFocus ->
                listner?.onFocuseSubScripe(listOf(),absoluteAdapterPosition)
            }
        }
    }

}
