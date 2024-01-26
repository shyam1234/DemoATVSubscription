package com.willow.android.tv.ui.resultspage

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.willow.android.R
import com.willow.android.WillowApplication
import com.willow.android.databinding.FragmentFixturesBinding
import com.willow.android.databinding.FragmentFixturesBySeriesBinding
import com.willow.android.tv.common.Types
import com.willow.android.tv.common.base.BaseFragment
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.data.repositories.resultspage.datamodel.APIResultsDataModel
import com.willow.android.tv.data.repositories.tvconfig.datamodel.NavigationTabsDataModel
import com.willow.android.tv.ui.matchcenterpage.MatchCenterFragment
import com.willow.android.tv.ui.resultspage.viewmodel.ResultsViewModel
import com.willow.android.tv.ui.resultspage.viewmodel.ResultsViewModelFactory
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.NavigationUtils
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.Utils
import com.willow.android.tv.utils.config.GlobalTVConfig
import com.willow.android.tv.utils.hide
import com.willow.android.tv.utils.show
import timber.log.Timber

class ResultsFragment() : BaseFragment(), KeyListener {

    private var data: NavigationTabsDataModel? = null
    private var mViewModel: ResultsViewModel? =null
    private var mBinding: FragmentFixturesBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = mBinding!!
    private var checkedPosition = BY_DATE_POSITION
    private var dateFragment: ResultsByDateFragment? = null
    private var seriesFragment: ResultsBySeriesFragment? = null
    private val tabsArray = mutableListOf(
        "By Date",
        "By Series",
    )

    init {
        Timber.d("called ResultsFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            FragmentFixturesBinding.inflate(inflater.cloneInContext(context), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationMenuCallback?.navMenuScreenName(Types.ScreenName.RESULTS)
        mViewModel = ViewModelProvider(
            this,
            ResultsViewModelFactory(WillowApplication.instance, data)
        )[(ResultsViewModel::class.java)]
        mViewModel?.renderPage?.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> onSuccess(it.data)
                is Resource.Error -> {
                    showErrorPage(ErrorType.NO_DATA_FOUND)
                    navigationMenuCallback?.navMenuToggle(true)
                }
                is Resource.Loading -> onLoading()
            }
        }

        mViewModel?._targetUrl?.observe(viewLifecycleOwner) {
            GlobalTVConfig.currentPage = resources.getStringArray(R.array.arrayPages)[5]
            NavigationUtils.onAddToFragmentContainer(
                activity as AppCompatActivity?,
                R.id.fl_main_holder,
                MatchCenterFragment.newInstance(it),
                addToBackStack = true
            )
        }

        setUpTabsnViewPager(tabsArray)

    }

//    private fun setUpTabsnViewPager() {
//        val viewPager = binding.viewPager
//        val tabLayout = binding.tabLayout
//        val adapter = ResultsViewPagerAdapter(childFragmentManager, lifecycle, this)
//        viewPager.adapter = adapter
//
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = tabsArray[position]
//        }.attach()
//        binding.tabLayout.getTabAt(0)?.view?.setOnKeyListener { v, keyCode, event ->
//            if (event?.action == KeyEvent.ACTION_DOWN) {
//
//                when (keyCode) {
//                    KeyEvent.KEYCODE_DPAD_LEFT -> {
//                        navigationMenuCallback.navMenuToggle(true)
//                    }
//                }
//            }
//            false
//        }
//        binding.tabLayout.getTabAt(1)?.view?.setOnKeyListener { v, keyCode, event ->
//            if (event?.action == KeyEvent.ACTION_DOWN) {
//
//                Timber.d("Testettes")
//                when (keyCode) {
//                    KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT -> {
//                        EventBus.getDefault().post(FixturesTabKeyDownEvent(true))
//                    }
//                }
//            }
//            false
//        }
//
//        tabLayout.makeSpaceBetweenTab()
//    }

    private fun setUpTabsnViewPager(tabsList: MutableList<String>) {
        val fragmentContainer = binding.fragmentContainer
        val tabRadioGroup = binding.tabRadioGroup


        for(i in tabsArray.indices){
            val radioButton = RadioButton(requireContext())
            radioButton.text = "   "+tabsList[i]+"   "
            radioButton.tag = tabsList[i]
            val params: RadioGroup.LayoutParams = RadioGroup.LayoutParams(context, null)
            params.setMargins(15, 0, 15, 0)
            radioButton.layoutParams = params
            context?.resources?.getDimension(R.dimen.dimen_10sp)?.let {
                radioButton.textSize = it
            }
            radioButton.buttonDrawable =null
            val typeface = context?.let {
                ResourcesCompat.getFont(
                    it, R.font.roboto_medium
                )
            }
            radioButton.typeface = typeface
            radioButton.background = AppCompatResources.getDrawable(requireContext(),R.drawable.radio_button_selector)
            tabRadioGroup.addView(radioButton)
        }


        tabRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = tabRadioGroup.findViewById<RadioButton>(checkedId)
            when (radioButton.tag) {
                "By Date" ->{
                    dateFragment = ResultsByDateFragment.newInstance(this)
                    navigateToFragment(dateFragment!!)
                    checkedPosition = BY_DATE_POSITION
                }

                "By Series" ->{
                    seriesFragment = ResultsBySeriesFragment.newInstance(this)
                    seriesFragment?.setNavigationCallback(navigationMenuCallback)
                    navigateToFragment(seriesFragment!!)
                    checkedPosition = BY_SERIES_POSITION
                }

                else -> {
                    seriesFragment = ResultsBySeriesFragment.newInstance(this)
                    navigateToFragment(seriesFragment!!)
                }
            }
        }

        tabRadioGroup[0].setOnClickListener {
            dateFragment = ResultsByDateFragment.newInstance(this)
            navigateToFragment(dateFragment!!)
            checkedPosition = BY_DATE_POSITION
        }

        tabRadioGroup[1].setOnClickListener {
            seriesFragment = ResultsBySeriesFragment.newInstance(this)
            seriesFragment?.setNavigationCallback(navigationMenuCallback)
            navigateToFragment(seriesFragment!!)
            checkedPosition = BY_SERIES_POSITION
        }

        tabRadioGroup.check(tabRadioGroup[checkedPosition].id)
        tabRadioGroup[checkedPosition].requestFocus()


        tabRadioGroup[0].setOnKeyListener { view, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        navigationMenuCallback?.navMenuToggle(true)
                        return@setOnKeyListener true

                    }
                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        dateFragment?.selectMonthLayout(true)
                        return@setOnKeyListener true
                    }
                }
            }
            false
        }

        tabRadioGroup[1].setOnKeyListener { view, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        tabRadioGroup[0].requestFocus()
                        return@setOnKeyListener true

                    }
                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        seriesFragment?.focusFirstItem()
                        return@setOnKeyListener true
                    }

                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        return@setOnKeyListener true
                    }
                }
            }
            false
        }


    }

    private fun navigateToFragment(fragment: Fragment){
        NavigationUtils.onReplaceToFragmentContainer(
            activity as AppCompatActivity?,
            childFragmentManager,
            binding.fragmentContainer.id,
            fragment,
            true
        )
    }
    private fun onSuccess(data: APIResultsDataModel?) {
        binding.loadingview.layoutLoading.hide()
    }

    private fun onLoading() {

        binding.loadingview.layoutLoading.show()
        val imageViewAnimator =
            ObjectAnimator.ofFloat(binding.loadingview.progressBar, View.ROTATION, 359f)
        imageViewAnimator.repeatCount = Animation.INFINITE
        imageViewAnimator.duration = 1000
        imageViewAnimator.start()
    }


    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        Utils.memoryLogs()
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    navigationMenuCallback?.navMenuToggle(true)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    if(view?.id == R.id.btnPrevious || view?.id == R.id.btnNext) {
                        binding.tabRadioGroup[0].requestFocus()
                        return true

                    } else if (view?.id == R.id.itemView) {
                        binding.tabRadioGroup[1].requestFocus()
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun showErrorPage(errorType: ErrorType, errorMessage: String? = null) {
        binding.loadingview.layoutLoading.hide()
        showError(binding.root, errorType, errorMessage, backBtnListener = {activity?.onBackPressed()}, btnText = "Back")
    }

    override fun focusItem() {
        binding.tabRadioGroup.requestFocus()
        binding.tabRadioGroup.isSelected = true
    }

    override fun onResume() {
        super.onResume()
        GlobalTVConfig.currentPage = resources.getStringArray(R.array.arrayPages)[4]
    }

    companion object {
        @JvmStatic
        fun newInstance(data: NavigationTabsDataModel?) =
            ResultsFragment().apply {
                this.data = data
            }

        const val BY_DATE_POSITION = 0
        const val BY_SERIES_POSITION = 1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
        dateFragment = null
        seriesFragment = null
        mViewModel = null
    }
}