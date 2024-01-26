package com.willow.android.mobile.views.pages.iAPPage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.Purchase
import com.willow.android.R
import com.willow.android.WillowApplication
import com.willow.android.databinding.IapPageFragmentBinding
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.services.ReloadService
import com.willow.android.mobile.services.WiAPIService
import com.willow.android.mobile.services.billing.WiBillingLifecycle
import com.willow.android.tv.utils.ImageUtility
import tv.willow.Views.Pages.HomePage.IAPPageViewModel

class IAPPageFragment : Fragment() {
    companion object {
        fun newInstance() = IAPPageFragment()
    }

    private lateinit var binding: IapPageFragmentBinding
    private lateinit var viewModel: IAPPageViewModel

    private lateinit var wiBillingLifecycle: WiBillingLifecycle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = IapPageFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.subscribe.setOnClickListener { subscribe() }
        binding.restore.setOnClickListener { restore() }

        drawWillowLogo()
        drawData()
        viewModel = ViewModelProvider(this).get(IAPPageViewModel::class.java)

        viewModel.getOffersData(requireContext())
        viewModel.offersData.observe(viewLifecycleOwner) {
            val categoryAdapter = IAPOffersAdapter(requireContext(), it.offers)
            val categoryLinearLayoutManager = LinearLayoutManager(requireContext())
            categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            binding.offersRecycler.layoutManager = categoryLinearLayoutManager
            binding.offersRecycler.adapter = categoryAdapter
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Billing APIs are all handled in the this lifecycle observer.
        wiBillingLifecycle = WillowApplication.wiBillingLifecycle
        lifecycle.addObserver(wiBillingLifecycle)

        wiBillingLifecycle.purchases.observe(this) {
            if (it.isNotEmpty()) {
                makeSyncAndroidReceiptCall(it[0])
            }
        }
    }

    /**
     * UI related Methods
     */
    private fun drawWillowLogo() {
        if (UserModel.cc.lowercase() == "ca") {
            binding.willowLogo.setImageResource(R.drawable.willow_ca)
        }
    }

    private fun drawData() {
        binding.priceBrandLayout.text = UserModel.iapProductPrice
        ImageUtility.loadImageInto(WiAPIService.iapImageUrl,binding.devicesImage)
        binding.descriptionOne.text = MessageConfig.iapDescription
        binding.descriptionTwo.text = MessageConfig.iapDescriptionDetail
    }


    private fun subscribe() {
        binding.spinner.visibility = View.VISIBLE
        activity?.let {
            wiBillingLifecycle.launchPurchaseFlow(requireActivity())
        }
    }

    private fun restore() {
        wiBillingLifecycle.queryPurchases()
    }

    private fun makeSyncAndroidReceiptCall(purchase: Purchase) {
        viewModel.makeSyncAndroidReceiptCall(requireContext(), UserModel.userId, purchase.originalJson)
        viewModel.receiptData.observe(viewLifecycleOwner) {
            binding.spinner.visibility = View.GONE
            if (it.accessValid != null) {
                UserModel.isSubscribed = it.accessValid
                ReloadService.reloadAllScreens()
                activity?.finish()
            } else {
                Log.d("ReceiptError", it.toString())
            }
        }
    }
}