package com.willow.android.tv.ui.pointtable

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.willow.android.WillowApplication
import com.willow.android.databinding.FragmentFixturesBySeriesBinding
import com.willow.android.databinding.FragmentPointTableGroupBinding
import com.willow.android.tv.common.base.BaseFragment
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.data.repositories.pointtable.datamodel.Group
import com.willow.android.tv.data.repositories.pointtable.datamodel.PointTableDataModelResponse
import com.willow.android.tv.ui.pointtable.adapter.PointTableGroupPagerAdapter
import com.willow.android.tv.ui.pointtable.adapter.TabGroupAdapter
import com.willow.android.tv.ui.pointtable.viewmodel.PointTableViewModel
import com.willow.android.tv.ui.pointtable.viewmodel.PointTableViewModelFactory
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.hide
import com.willow.android.tv.utils.show


class PointTableGroupFragment : BaseFragment(), KeyListener {
    private var mBinding: FragmentPointTableGroupBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = mBinding!!
    private var mViewModel: PointTableViewModel?=null
    private var pointTableUrl: String? = null
    private var groupList: List<Group>? = null
    private var keyListener: KeyListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentPointTableGroupBinding.inflate(
            inflater.cloneInContext(context),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProvider(
            this,
            PointTableViewModelFactory(WillowApplication.instance)
        )[(PointTableViewModel::class.java)]
        mViewModel?.loadPointTableData(pointTableUrl)
        initObserver()
    }

    override fun focusItem() {
        if (!groupList.isNullOrEmpty())
            binding.tabLayout.getChildAt(0).requestFocus()
    }

    private fun initObserver() {
        mViewModel?.apply {
            renderPage.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> onSuccess(it.data)
                    is Resource.Error -> {
                        binding.loadingview.layoutLoading.hide()
                        showErrorPage(ErrorType.NO_DATA_FOUND)
                    }
                    is Resource.Loading -> onLoading()
                }
            }
        }
    }


    private fun onSuccess(pointTableData: PointTableDataModelResponse?) {
        binding.loadingview.layoutLoading.hide()
        groupList = pointTableData?.result?.standings?.get(0)?.groups ?: emptyList()
        groupList?.let {
            setGroupViewPager(it)
        }
    }


    private fun setGroupViewPager(groupList: List<Group>) {
        val adapter = PointTableGroupPagerAdapter(requireActivity(), groupList)
        binding.viewPager.adapter = adapter

        binding.tabLayout.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.tabLayout.adapter = TabGroupAdapter(groupList, menuItemClickListener, this)
    }

    private fun onLoading() {
        binding.loadingview.layoutLoading.show()
        val imageViewAnimator =
            ObjectAnimator.ofFloat(binding.loadingview.progressBar, View.ROTATION, 359f)
        imageViewAnimator.repeatCount = Animation.INFINITE
        imageViewAnimator.duration = 1000
        imageViewAnimator.start()
    }

    companion object {
        @JvmStatic
        fun newInstance(/*listener: NavigationMenuCallback?,*/ url: String?, keyListener: KeyListener) =
            PointTableGroupFragment().apply {
                pointTableUrl = url
                this.keyListener = keyListener
            }
    }

    private fun showErrorPage(errorType: ErrorType, errorMessage: String? = null) {
        showError(binding.root, errorType, errorMessage)
    }


    private val menuItemClickListener = object : TabGroupAdapter.ItemClickListener {
        override fun itemClicked(position: Int) {
            binding.viewPager.currentItem = position
        }

    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN -> {

                }

                KeyEvent.KEYCODE_DPAD_UP -> {
                    keyListener?.onKey(view, keyCode, event)
                    return true
                }

                KeyEvent.KEYCODE_DPAD_RIGHT -> {

                }

            }
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
        mViewModel = null
    }
}