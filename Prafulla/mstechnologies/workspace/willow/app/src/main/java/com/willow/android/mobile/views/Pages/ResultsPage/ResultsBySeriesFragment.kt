package com.willow.android.mobile.views.pages.resultsPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.willow.android.databinding.ResultsBySeriesBinding

class ResultsBySeriesFragment(): Fragment() {

    companion object {
        fun newInstance() = ResultsBySeriesFragment()
    }

    private lateinit var binding: ResultsBySeriesBinding
    private lateinit var viewModel: ResultsPageViewModel
    private var refreshContainer: SwipeRefreshLayout? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ResultsBySeriesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(ResultsPageViewModel::class.java)
        viewModel.resultsData.observe(viewLifecycleOwner, Observer {
            val categoryAdapter = ResultsBySeriesAdapter(requireContext(), it)
            val categoryLinearLayoutManager = LinearLayoutManager(requireContext())
            categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            binding.resultsBySeriesRecycler.layoutManager = categoryLinearLayoutManager
            binding.resultsBySeriesRecycler.adapter = categoryAdapter

            setRefreshAction()
        })
    }

    private fun setRefreshAction() {
        val parentFragment = this.parentFragment
        if (parentFragment is ResultsPageFragment) {
            refreshContainer = binding.refreshContainer
            refreshContainer?.setOnRefreshListener {
                parentFragment.loadPageData()
                refreshContainer?.isRefreshing = false
            }
        }
    }
}