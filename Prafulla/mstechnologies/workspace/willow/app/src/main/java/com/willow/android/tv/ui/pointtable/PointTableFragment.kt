package com.willow.android.tv.ui.pointtable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.willow.android.databinding.FragmentFixturesBySeriesBinding
import com.willow.android.databinding.FragmentPointTableBinding
import com.willow.android.tv.common.base.BaseFragment
import com.willow.android.tv.data.repositories.pointtable.datamodel.Group
import com.willow.android.tv.ui.pointtable.adapter.PointTableAdapter

class PointTableFragment : BaseFragment() {
    private var mBinding: FragmentPointTableBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = mBinding!!
    private lateinit var group: Group


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding =
            FragmentPointTableBinding.inflate(inflater.cloneInContext(context), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPointRecyclerView()
    }

    private fun initPointRecyclerView() {
        binding.recyclerView.adapter = PointTableAdapter(group)
    }

    companion object {
        @JvmStatic
        fun newInstance(pointTableGroup: Group) =
            PointTableFragment().apply {
                this.group = pointTableGroup
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding=null
    }
}