package willow.android.tv.ui.subscription

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.willow.android.R
import com.willow.android.databinding.FragmentSubscripitionPlanBinding
import com.willow.android.tv.ui.subscription.adapter.SubscriptionAdapter
import com.willow.android.tv.ui.subscription.`interface`.ISubscriptionListener
import com.willow.android.tv.ui.subscription.viewmodel.SubscriptionViewModel
import com.willow.android.tv.utils.NavigationUtils
import com.willow.android.tv.utils.Utils
import com.willow.android.tv.utils.extension.loadImageUrl

class SubscriptionPlanFragment() : Fragment() {

    private lateinit var mBinding: FragmentSubscripitionPlanBinding
    private lateinit var viewModel: SubscriptionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            FragmentSubscripitionPlanBinding.inflate(
                inflater.cloneInContext(context),
                container,
                false
            )
        return mBinding.root
    }

    fun onPageSelected(position: Int, size: Int) {
        Utils.updateIndicator(context,mBinding.linIndicatorHolder, position, size)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SubscriptionViewModel::class.java)
        initView()
    }

    private fun initView() {
        mBinding.imageView3.loadImageUrl(viewModel.getImageUrl(),R.drawable.subscription_player_img)
        initRecycleView()
        mBinding.textSubscriptionDetails.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                foregroundGravity = Gravity.START
            }
            layoutManager = LinearLayoutManager(context)
            hasFixedSize()
        }
    }

    private fun initRecycleView() {
        val dataSet = viewModel.getInAppProductDetails()
        val size = dataSet.size
        if (size > 0) {
            setPlanDetailsText(dataSet.get(0).details)
        }
        onPageSelected(0, size)
        mBinding.rvSubButton.apply {
            hasFixedSize()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                foregroundGravity = Gravity.START
            }
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    foregroundGravity = Gravity.START
                }
            }
            adapter = SubscriptionAdapter(
                dataSet,
                iSubscriptionListener(size)
            )
        }
    }

    private fun iSubscriptionListener(size: Int) = object : ISubscriptionListener {
        override fun onFocuseSubScripe(
            discriptionText: List<String>,
            absoluteAdapterPosition: Int
        ) {
            setPlanDetailsText(discriptionText)
            onPageSelected(absoluteAdapterPosition, size)
        }

        override fun onClickSubScripe(productId: String) {
            subscribe(productId)
        }
    }

    private fun setPlanDetailsText(discriptionText: List<String>) {
        mBinding.textSubscriptionDetails.apply {
            adapter = SubscriptionAdapter(
                dataSet = discriptionText,
                listner = null
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SubscriptionPlanFragment()
    }


    private fun subscribe(productId: String) {
        NavigationUtils.onAddToFragmentContainer(
            activity as AppCompatActivity?, R.id.fragment_container_view_holder,
            SubscriptionInAppBillingProgressFragment.getInstance(
                productId,
                true
            )
        )
    }
}