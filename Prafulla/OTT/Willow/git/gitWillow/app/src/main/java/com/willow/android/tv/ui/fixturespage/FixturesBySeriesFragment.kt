package com.willow.android.tv.ui.fixturespage

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.willow.android.R
import com.willow.android.databinding.FragmentFixturesByDateBinding
import com.willow.android.databinding.FragmentFixturesBySeriesBinding
import com.willow.android.tv.common.base.BaseFragment
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.data.repositories.fixturespage.datamodel.FixturesBySeries
import com.willow.android.tv.ui.fixturespage.adapters.SeriesItemClickListener
import com.willow.android.tv.ui.fixturespage.adapters.SeriesListAdapter
import com.willow.android.tv.ui.fixturespage.viewmodel.FixturesViewModel
import com.willow.android.tv.ui.matchcenterpage.MatchCenterFragment
import com.willow.android.tv.utils.NavigationUtils
import com.willow.android.tv.utils.Resource


/**
 * A simple [Fragment] subclass.
 * Use the [FixturesBySeriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FixturesBySeriesFragment : BaseFragment() , SeriesItemClickListener,KeyListener {
    private var mViewModel: FixturesViewModel?=null

    private var mBinding: FragmentFixturesBySeriesBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = mBinding!!
    private var keyListener: KeyListener?=null
    private var itemClickPosition = 0


    var adapterSeriesRv : SeriesListAdapter?= null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding =  FragmentFixturesBySeriesBinding.inflate(inflater.cloneInContext(context), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel = ViewModelProviders.of(requireParentFragment())[FixturesViewModel::class.java]
        adapterSeriesRv = SeriesListAdapter(this)
        binding.seriesList.layoutManager = GridLayoutManager(requireContext(),
            resources.getInteger(R.integer.span_count))
        binding.seriesList.adapter = adapterSeriesRv

        mViewModel?.renderPage?.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> onSuccess(it.data?.result?.fixtures_by_series)
                else -> {}
            }
        }


    }

    private fun onSuccess(fixturesByDate: List<FixturesBySeries>?) {

        adapterSeriesRv?.setMatchesListToRV(fixturesByDate,this@FixturesBySeriesFragment)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment FixturesBySeriesFragment.
         */
        @JvmStatic
        fun newInstance(listener: KeyListener) =
            FixturesBySeriesFragment().apply {
                keyListener = listener
            }
    }

    override fun onSeriesItemClickListener(match: FixturesBySeries, position: Int) {
        //mViewModel._targetUrl.value = match.target_url
        itemClickPosition = position
        NavigationUtils.onAddToFragmentContainer(
            activity as AppCompatActivity?,
            R.id.fl_main_holder,
            MatchCenterFragment.newInstance(match.target_url, fragmentDestroyCallBack),
            addToBackStack = true
        )
    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    navigationMenuCallback?.navMenuToggle(true)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    keyListener?.onKey(view, keyCode, event)
                    return true
                }
            }
        }
        return false
    }

    fun focusFirstItem(){
        val view = binding.seriesList.layoutManager?.getChildAt(0)
        view?.let {
            it.requestFocus()
        }
    }

    private val fragmentDestroyCallBack = object : MatchCenterFragment.DestroyMethodCallback {
        override fun fragmentDestroyed() {
            val view = binding.seriesList.layoutManager?.getChildAt(itemClickPosition)
            view?.let {
                it.requestFocus()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
        adapterSeriesRv = null
        keyListener = null
        mViewModel = null
    }

}