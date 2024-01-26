package com.willow.android.mobile.views.pages.fixturesPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.willow.android.databinding.FixturesByDatePageBinding


class FixturesByDateFragment: Fragment() {
    companion object {
        fun newInstance() = FixturesByDateFragment()
    }

    private lateinit var binding: FixturesByDatePageBinding
    private lateinit var viewModel: FixturesPageViewModel
    private var refreshContainer: SwipeRefreshLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FixturesByDatePageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(FixturesPageViewModel::class.java)
        viewModel.fixturesData.observe(viewLifecycleOwner, Observer {
            val categoryAdapter = FixturesByDateAdapter(requireContext(), it)
            categoryAdapter.notifyDataSetChanged()
            val categoryLinearLayoutManager = LinearLayoutManager(requireContext())
            categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            binding.fixturesByDateRecycler.layoutManager = categoryLinearLayoutManager
            binding.fixturesByDateRecycler.adapter = categoryAdapter

            setRefreshAction()
        })
    }

    private fun setRefreshAction() {
        val parentFragment = this.parentFragment
        if (parentFragment is FixturesPageFragment) {
            refreshContainer = binding.refreshContainer
            refreshContainer?.setOnRefreshListener {
                parentFragment.loadPageData()
                refreshContainer?.isRefreshing = false
            }
        }
    }
}