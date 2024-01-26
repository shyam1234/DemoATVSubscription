package com.willow.android.mobile.views.pages.settingsPage


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.willow.android.R
import com.willow.android.databinding.SettingsPageFragmentBinding
import com.willow.android.mobile.MainActivity
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.configs.ResultCodes
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.services.ReloadService
import com.willow.android.mobile.services.analytics.AnalyticsService
import com.willow.android.mobile.views.pages.profilePage.ProfilePageActivity
import com.willow.android.mobile.views.popup.messagePopup.MessagePopupActivity
import tv.willow.Models.SettingsItemModel


class SettingsPageFragment : Fragment(), LogoutListenerInterface {

    companion object {
        fun newInstance() = SettingsPageFragment()
    }

    private lateinit var viewModel: SettingsPageViewModel
    private lateinit var binding: SettingsPageFragmentBinding
    private lateinit var refreshContainer: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingsPageFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingsPageViewModel::class.java)
        refreshContainer = binding.refreshContainer
        refreshContainer.setOnRefreshListener {
            loadPageData()
            refreshContainer.isRefreshing = false
        }

        addDecoratorToRecycler()
        setPageTitle()
        setBrandLogoAndCopyright()
        loadPageData()
        sendAnalyticsEvent()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == ResultCodes.DELETE_ACCOUNT_RESULT) {
            val deleteAccMsg = data?.getStringExtra("DELETE_ACC_MEG")
            if (deleteAccMsg != null) {
                showMessage(deleteAccMsg)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (ReloadService.reloadSettings) {
            loadPageData()
            ReloadService.reloadSettings = false
        }
    }

    fun loadPageData() {
        binding.spinner.visibility = View.VISIBLE
        viewModel.getSettingsPageData(requireContext())
        viewModel.settingsData.observe(viewLifecycleOwner, Observer {
            binding.spinner.visibility = View.GONE

            val categoryAdapter = SettingsPageAdapter(requireContext(), it, this)
            val categoryLinearLayoutManager = LinearLayoutManager(requireContext())
            categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            binding.settingsRecycler.layoutManager = categoryLinearLayoutManager
            binding.settingsRecycler.adapter = categoryAdapter
        })
    }

    fun addDecoratorToRecycler() {
        // Add divider decorator
        val itemDecor = DividerItemDecoration(context, RecyclerView.VERTICAL)
        val dividerDrawable = context?.let { it1 -> ContextCompat.getDrawable(it1, R.drawable.vertical_divider) }
        if (dividerDrawable != null) {
            itemDecor.setDrawable(dividerDrawable)
        }
        binding.settingsRecycler.addItemDecoration(itemDecor)
    }

    override fun logoutRefresh() {
        loadPageData()
    }

    override fun launchProfileActivity(itemData: SettingsItemModel) {
        val intent = Intent(context, ProfilePageActivity::class.java).apply {}
        intent.putExtra("SETTINGS_ITEM", itemData)
        startActivityForResult(intent, ResultCodes.DELETE_ACCOUNT_RESULT)
    }

    private fun sendAnalyticsEvent() {
         AnalyticsService.trackFirebaseScreen("AN_SETTINGS_PAGE")
    }

    private fun setBrandLogoAndCopyright() {
        if (UserModel.cc.lowercase() == "ca") {
            binding.settingsFooter.settingsFooterLogo.setImageResource(R.drawable.willow_ca)
        }

        try {
            val pInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            binding.settingsFooter.appVersionText.text = "Version " + pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        binding.settingsFooter.copyrightMessage.text = "\u00a9 " + MessageConfig.copyrightMessage
    }

    private fun setPageTitle() {
        (activity as? MainActivity)?.setPageTitle("Settings")
    }

    fun showMessage(message: String) {
        if (message.isEmpty()) { return }

        val intent = Intent(context, MessagePopupActivity::class.java).apply {}
        intent.putExtra("MESSAGE", message)
        startActivity(intent)
    }
}