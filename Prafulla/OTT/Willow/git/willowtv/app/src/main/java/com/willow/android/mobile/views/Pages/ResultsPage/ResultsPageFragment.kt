package com.willow.android.mobile.views.pages.resultsPage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.willow.android.databinding.ResultsPageFragmentBinding
import com.willow.android.mobile.MainActivity
import com.willow.android.mobile.services.ReloadService
import com.willow.android.mobile.services.analytics.AnalyticsService
import com.willow.android.mobile.views.popup.filterPopup.FilterPopupActivity

class ResultsPageFragment : Fragment() {

    companion object {
        fun newInstance() = ResultsPageFragment()
    }

    private lateinit var viewModel: ResultsPageViewModel
    private lateinit var binding: ResultsPageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ResultsPageFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setPageTitle()
        loadPageData()
        binding.filterResults.setOnClickListener { launchFilterResultsPage() }
        sendAnalyticsEvent()
    }

    override fun onResume() {
        super.onResume()
        if (ReloadService.reloadResults) {
            loadPageData()
            ReloadService.reloadResults = false
        }
    }

    fun loadPageData() {
        binding.spinner.visibility = View.VISIBLE
        viewModel = ViewModelProvider(requireActivity()).get(ResultsPageViewModel::class.java)
        viewModel.makeResultsDataRequest(requireContext())
        viewModel.resultsData.observe(viewLifecycleOwner, Observer {
            binding.spinner.visibility = View.GONE

            val fragmentAdapter = ResultsPageAdapter(childFragmentManager)
            binding.tabViewPager.adapter = fragmentAdapter
            binding.tabLayout.setupWithViewPager(binding.tabViewPager)
        })
    }

    fun sendAnalyticsEvent() {
         AnalyticsService.trackFirebaseScreen("AN_RESULTS_PAGE")
    }

    private fun setPageTitle() {
        (activity as? MainActivity)?.setPageTitle("Results")
    }

    private fun launchFilterResultsPage() {
        val intent = Intent(context, FilterPopupActivity::class.java).apply {}
        intent.putExtra("IS_FIXTURES", false)
        startActivity(intent)
    }
}