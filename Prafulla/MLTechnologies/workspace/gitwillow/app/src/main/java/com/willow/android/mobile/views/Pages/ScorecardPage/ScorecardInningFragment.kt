package com.willow.android.mobile.views.pages.scorecardPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.willow.android.databinding.ScorecardInningFragmentBinding

private const val INNING_INDEX = "INNING_INDEX_KEY"

class ScorecardInningFragment : Fragment() {
    private var inningIndex: Int? = null

    private lateinit var viewModel: ScorecardPageViewModel
    private lateinit var binding: ScorecardInningFragmentBinding
    private lateinit var refreshContainer: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            inningIndex = it.getInt(INNING_INDEX)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ScorecardInningFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ScorecardPageViewModel::class.java)

        viewModel.scorecardData.observe(viewLifecycleOwner, Observer {
            if (inningIndex != null) {
                val inningData = it.result.Innings[inningIndex!!]
                val categoryAdapter = ScorecardBattingAdapter(requireContext(), scorecardInningModel = inningData!!)
                val categoryLinearLayoutManager = LinearLayoutManager(context)
                categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                binding.batsmanRecycler.layoutManager = categoryLinearLayoutManager
                binding.batsmanRecycler.adapter = categoryAdapter
                // Add divider decorator
                val batsmanDecor = DividerItemDecoration(context, RecyclerView.VERTICAL)
                binding.batsmanRecycler.addItemDecoration(batsmanDecor)

                binding.extras.text = inningData?.Extras?.completeString
                binding.didNotBat.text = inningData?.didNotBatPlayers
                binding.total.text = inningData?.totalRunsWickets
                binding.fallOfWickets.text = inningData?.fowPlayers
                binding.overs.text = inningData?.overs + inningData.runRate

                val bowlerAdapter = ScorecardBowlingAdapter(requireContext(), scorecardInningModel = inningData!!)
                val bowlerLinearLayoutManager = LinearLayoutManager(context)
                bowlerLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                binding.bowlerRecycler.layoutManager = bowlerLinearLayoutManager
                binding.bowlerRecycler.adapter = bowlerAdapter

                // Add divider decorator
                val bowlerDecor = DividerItemDecoration(context, RecyclerView.VERTICAL)
                binding.bowlerRecycler.addItemDecoration(bowlerDecor)
            }
        })

        setRefreshAction()
    }

    private fun setRefreshAction() {
        val parentFragment = this.parentFragment
        if (parentFragment is ScorecardPageFragment) {
            refreshContainer = binding.refreshContainer
            refreshContainer.setOnRefreshListener {
                parentFragment.makeDataAPIRequest()
                refreshContainer.isRefreshing = false
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(inningIndex: Int) =
            ScorecardInningFragment().apply {
                arguments = Bundle().apply {
                    putInt(INNING_INDEX, inningIndex)
                }
            }
    }
}