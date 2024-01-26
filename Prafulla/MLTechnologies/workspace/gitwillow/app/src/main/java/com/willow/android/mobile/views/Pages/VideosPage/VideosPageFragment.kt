package com.willow.android.mobile.views.pages.videosPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.willow.android.databinding.VideosPageFragmentBinding
import com.willow.android.mobile.MainActivity
import com.willow.android.mobile.services.analytics.AnalyticsService

class VideosPageFragment : Fragment() {

    companion object {
        fun newInstance() = VideosPageFragment()
    }

    private lateinit var viewModel: VideosPageViewModel
    private lateinit var binding: VideosPageFragmentBinding
    private lateinit var refreshContainer: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = VideosPageFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(VideosPageViewModel::class.java)

        refreshContainer = binding.refreshContainer
        refreshContainer.setOnRefreshListener {
            loadPageData()
            refreshContainer.isRefreshing = false
        }

        setPageTitle()
        loadPageData()
        sendAnalyticsEvent()
    }

    fun sendAnalyticsEvent() {
         AnalyticsService.trackFirebaseScreen("AN_VIDEOS_PAGE")
    }

    private fun setPageTitle() {
        (activity as? MainActivity)?.setPageTitle("Videos")
    }

    private fun loadPageData() {
        binding.spinner.visibility = View.VISIBLE
        viewModel.getVideosPageData(requireContext())
        viewModel.videosPageData.observe(viewLifecycleOwner, Observer {
            binding.spinner.visibility = View.GONE

            val categoryAdapter = VideosPageAdapter(requireContext(), it)
            val categoryLinearLayoutManager = LinearLayoutManager(requireContext())
            categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            binding.videosRecyclerView.layoutManager = categoryLinearLayoutManager
            binding.videosRecyclerView.adapter = categoryAdapter
        })
    }
}