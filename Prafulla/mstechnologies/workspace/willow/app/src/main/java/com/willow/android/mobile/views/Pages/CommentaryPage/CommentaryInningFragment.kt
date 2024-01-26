package com.willow.android.mobile.views.pages.commentaryPage

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
import com.willow.android.databinding.CommentaryInningFragmentBinding

private const val INNING_INDEX = "INNING_INDEX_KEY"

class CommentaryInningFragment : Fragment() {
    private var inningIndex: Int? = null

    private lateinit var viewModel: CommentaryPageViewModel
    private lateinit var binding: CommentaryInningFragmentBinding
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
        binding = CommentaryInningFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(CommentaryPageViewModel::class.java)

        viewModel.commentaryData.observe(viewLifecycleOwner, Observer {
            if (inningIndex != null) {
                val inningData = it.Innings[inningIndex!!]
                val categoryAdapter =
                    CommentaryInningAdapter(requireContext(), commentaryInningModel = inningData)
                val categoryLinearLayoutManager = LinearLayoutManager(context)
                categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                binding.commentaryInningRecycler.layoutManager = categoryLinearLayoutManager
                binding.commentaryInningRecycler.adapter = categoryAdapter

                // Add divider decorator
                val itemDecor = DividerItemDecoration(context, RecyclerView.VERTICAL)
                val dividerDrawable =
                    context?.let { it1 ->
                        ContextCompat.getDrawable(
                            it1,
                            R.drawable.vertical_divider
                        )
                    }
                if (dividerDrawable != null) {
                    itemDecor.setDrawable(dividerDrawable)
                }
                binding.commentaryInningRecycler.addItemDecoration(itemDecor)
            }
        })

        setRefreshAction()
    }

    private fun setRefreshAction() {
        val parentFragment = this.parentFragment
        if (parentFragment is CommentaryPageFragment) {
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
            CommentaryInningFragment().apply {
                arguments = Bundle().apply {
                    putInt(INNING_INDEX, inningIndex)
                }
            }
    }
}