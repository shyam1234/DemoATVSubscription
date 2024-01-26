package willow.android.tv.ui.subscription.ViewHolder

import com.willow.android.databinding.ItemSubscriptionPlanButtonBinding
import com.willow.android.tv.common.base.BaseViewHolder
import com.willow.android.tv.ui.subscription.`interface`.ISubscriptionListener
import com.willow.android.tv.ui.subscription.model.SubscriptionPeriodButtonModel

class SubscriptionButtonViewHolder(
    private val binding: ItemSubscriptionPlanButtonBinding,
    private val listner: ISubscriptionListener?
) : BaseViewHolder(binding.root) {
    override fun bind(model: Any) {
        if (model is SubscriptionPeriodButtonModel) {
            binding.model = model
            binding.SubButton.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    listner?.onFocuseSubScripe(model.details, absoluteAdapterPosition)
                }
            }
            binding.SubButton.setOnClickListener {
                model.productId?.let { pID -> listner?.onClickSubScripe(pID) }
            }
            if (absoluteAdapterPosition == 0) {
                binding.SubButton.requestFocus()
            }

        }
    }

}
