
package com.willow.android.mobile.views.pages.fixturesPage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.willow.android.databinding.FixturesPageFragmentBinding
import com.willow.android.mobile.MainActivity
import com.willow.android.mobile.services.ReloadService
import com.willow.android.mobile.services.analytics.AnalyticsService
import com.willow.android.mobile.views.popup.filterPopup.FilterPopupActivity


class FixturesPageFragment : Fragment() {

    companion object {
        fun newInstance() = FixturesPageFragment()
    }

    private lateinit var viewModel: FixturesPageViewModel
    private lateinit var binding: FixturesPageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FixturesPageFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setPageTitle()
        loadPageData()
        sendAnalyticsEvent()
    }

    override fun onResume() {
        super.onResume()
        if (ReloadService.reloadFixtures) {
            loadPageData()
            ReloadService.reloadFixtures = false
        }
    }

    fun loadPageData() {
        binding.spinner.visibility = View.VISIBLE
        viewModel = ViewModelProvider(requireActivity()).get(FixturesPageViewModel::class.java)
        viewModel.makeFixturesDataRequest(requireContext())
        viewModel.fixturesData.observe(viewLifecycleOwner, Observer {
            binding.spinner.visibility = View.GONE

            val fragmentAdapter = FixturesPageAdapter(childFragmentManager)
            binding.fixturesTabViewPager.adapter = fragmentAdapter
            fragmentAdapter.notifyDataSetChanged()
            binding.tabLayout.setupWithViewPager(binding.fixturesTabViewPager)

            binding.filterFixtures.setOnClickListener { launchFilterFixturesPage() }
        })
    }

    fun sendAnalyticsEvent() {
         AnalyticsService.trackFirebaseScreen("AN_HOME_PAGE")
    }

    private fun setPageTitle() {
        (activity as? MainActivity)?.setPageTitle("Fixtures")
    }

    private fun launchFilterFixturesPage() {
        val intent = Intent(context, FilterPopupActivity::class.java).apply {}
        intent.putExtra("IS_FIXTURES", true)
        startActivity(intent)
    }
}