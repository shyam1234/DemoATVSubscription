package com.willow.android.tv.ui.matchcenterpage

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
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.willow.android.R
import com.willow.android.WillowApplication
import com.willow.android.databinding.FragmentFixturesBySeriesBinding
import com.willow.android.databinding.FragmentMatchCenterBinding
import com.willow.android.tv.common.base.BaseFragment
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.common.cards.presenters.DefaultCardViewPresenter
import com.willow.android.tv.data.repositories.matchcenterpage.datamodel.APINewMatchCenterDataModel
import com.willow.android.tv.ui.matchcenterpage.model.TabsDataModel
import com.willow.android.tv.ui.matchcenterpage.viewmodel.MatchCenterViewModel
import com.willow.android.tv.ui.matchcenterpage.viewmodel.MatchCenterViewModelFactory
import com.willow.android.tv.ui.playback.PlayerManager
import com.willow.android.tv.ui.pointtable.PointTableGroupFragment
import com.willow.android.tv.ui.scoreCardMatchInfo.MatchInfoFragment
import com.willow.android.tv.ui.scoreCardMatchInfo.ScoreCardFragment
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.GlobalConstants
import com.willow.android.tv.utils.NavigationUtils
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.events.KeyPressedEvent
import com.willow.android.tv.utils.events.MatchCenterDestroyed
import com.willow.android.tv.utils.events.RefreshActivityEvent
import com.willow.android.tv.utils.events.ScoreCardDpadUpEvent
import com.willow.android.tv.utils.hide
import com.willow.android.tv.utils.show
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber


/**
 * A [Fragment] for showing videos list inside fixtures/results.
 * Use the [MatchCenterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val TARGET_URL = "targetUrl"


class MatchCenterFragment : BaseFragment(), KeyListener {

    private var mBinding: FragmentMatchCenterBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = mBinding!!
    private var mViewModel: MatchCenterViewModel?=null
    private var target_url: String? = null
    private var bySeries: Boolean? = false
    private var fragment: Fragment? = null
    private var radioCheckedId: Int? = null
    private var destroyCallback: DestroyMethodCallback? = null


    init {
        Timber.d("called MatchCenterFragment")
        PlayerManager.shouldPlayContent = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            target_url = it.getString(TARGET_URL)
            Timber.d("MatchCenterFragment targetURL :: $target_url")

        }

        if (target_url?.contains("series") == true) {
            bySeries = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            FragmentMatchCenterBinding.inflate(inflater.cloneInContext(context), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("targetURL :: $target_url")

        mViewModel = ViewModelProvider(
            this,
            MatchCenterViewModelFactory(WillowApplication.instance)
        )[(MatchCenterViewModel::class.java)]

        mViewModel?.apply {

            _urlToBeCalled.value = target_url

            renderPage.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> onSuccess(it.data)
                    is Resource.Error -> {
                        showErrorPage(ErrorType.NO_DATA_FOUND)
                    }
                    is Resource.Loading -> onLoading()
                }
            }

            urlToBeCalled.observe(viewLifecycleOwner) {
                this.loadMatchCenterPageConfig()
            }

            _targetUrl.observe(viewLifecycleOwner) {
                NavigationUtils.onAddToFragmentContainer(
                    activity as AppCompatActivity?,
                    R.id.fl_main_holder,
                    MatchCenterFragment.newInstance(it),
                    addToBackStack = true
                )
            }
        }
    }

    private fun onSuccess(data: APINewMatchCenterDataModel?) {

        binding.apply {
            loadingview.layoutLoading.hide()

        }

        val tabsList = mViewModel?.getTabNames()
        if (tabsList?.isNotEmpty()==true) {
            setUpTabsnViewPager(tabsList)
        } else {
            showErrorPage(ErrorType.NO_MATCH_FOUND)
        }
    }

    private fun onLoading() {

        binding.loadingview.layoutLoading.show()
        val imageViewAnimator =
            ObjectAnimator.ofFloat(binding.loadingview.progressBar, View.ROTATION, 359f)
        imageViewAnimator.repeatCount = Animation.INFINITE
        imageViewAnimator.duration = 1000
        imageViewAnimator.start()
    }


    private fun setUpTabsnViewPager(tabsList: ArrayList<TabsDataModel>) {
        val fragmentContainer = binding.fragmentContainer
        val tabRadioGroup = binding.tabRadioGroup

        tabRadioGroup.removeAllViews()
        for (i in tabsList.indices) {
            var radioButton = RadioButton(requireContext())
            radioButton.text = "  " + tabsList[i].title + "  "
            radioButton.tag = tabsList[i].type
            val params: RadioGroup.LayoutParams = RadioGroup.LayoutParams(context, null)
            params.setMargins(15, 0, 15, 0)
            radioButton.layoutParams = params
            context?.resources?.getDimension(R.dimen.dimen_10sp)?.let {
                radioButton.textSize = it
            }
            radioButton.buttonDrawable = null
            val typeface = context?.let {
                ResourcesCompat.getFont(
                    it, R.font.roboto_medium
                )
            }
            radioButton.typeface = typeface
            radioButton.background =
                AppCompatResources.getDrawable(requireContext(), R.drawable.radio_button_selector)
            tabRadioGroup.addView(radioButton)

            radioButton.setOnKeyListener { view, keyCode, event ->
                if (event?.action == KeyEvent.ACTION_DOWN) {
                    Timber.d("MatchCenter clicked down1")
                    when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            Timber.d("#### tabsList Size: :"+tabsList.size)
                            Timber.d("#### tabsList i: :"+i)
                            if (i == tabsList.size - 1)

                                return@setOnKeyListener true
                            else {
                                Timber.d("#### tabRadioGroup child at i: :"+ tabRadioGroup.getChildAt(i))
                                tabRadioGroup.getChildAt(i+1).requestFocus()
                                return@setOnKeyListener true

                            }
                        }

                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            Timber.d("#### KEYCODE_DPAD_LEFT i: :" + i)
                            if (i == 0) {
                                return@setOnKeyListener true
                            } else {
                                tabRadioGroup.getChildAt(i - 1).requestFocus()
                                return@setOnKeyListener true

                            }
                        }

                    }
                }
                onKey(view, keyCode, event)
            }
        }



        tabRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = tabRadioGroup.findViewById<RadioButton>(checkedId)
            radioCheckedId = checkedId
            when (radioButton.tag) {
                GlobalConstants.MatchCenterTab.VIDEOS -> {
                    fragment = MatchCenterVideosTabFragment.newInstance(this)
                    navigateToFragment(fragment!!)
                }

                GlobalConstants.MatchCenterTab.SCORECARD -> {
                    fragment = ScoreCardFragment.getInstance(
                        tabsList.filter { it.type == radioButton.tag }.first().url
                    )
                    navigateToFragment(fragment!!)
                }

                GlobalConstants.MatchCenterTab.MATCH_INFO -> {
                    fragment = MatchInfoFragment.newInstance(
                        tabsList.filter { it.type == radioButton.tag }.first().url
                    )
                    navigateToFragment(fragment!!)
                }

                GlobalConstants.MatchCenterTab.FIXTURES -> {
                    fragment = UpcomingMatchesByDateFragment.newInstance(this)
                    navigateToFragment(fragment!!)
                }

                GlobalConstants.MatchCenterTab.STANDINGS -> {
                    fragment = PointTableGroupFragment.newInstance(
                        tabsList.filter { it.type == radioButton.tag }.first().url,
                        this
                    )
                    navigateToFragment(fragment!!)
                }

                else -> {
                    fragment = MatchCenterVideosTabFragment.newInstance(this)
                    navigateToFragment(fragment!!)
                }
            }
        }


        tabRadioGroup.check(tabRadioGroup.get(0).id)
        tabRadioGroup.get(0).requestFocus()


    }

    private fun navigateToFragment(fragment: Fragment) {
        NavigationUtils.onReplaceToFragmentContainer(
            activity as AppCompatActivity?,
            childFragmentManager,
            binding.fragmentContainer.id,
            fragment,
            true
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(
            targetUrl: String,
            destroyMethodCallback: DestroyMethodCallback? = null
        ) =
            MatchCenterFragment().apply {
                arguments = Bundle().apply {
                    putString(TARGET_URL, targetUrl)
                }
                this.destroyCallback = destroyMethodCallback
            }
    }


    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            Timber.d("MatchCenter clicked down")
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    when (view?.tag) {
                        GlobalConstants.MatchCenterTab.FIXTURES,
                        GlobalConstants.MatchCenterTab.STANDINGS,
                        GlobalConstants.MatchCenterTab.VIDEOS,
                        GlobalConstants.MatchCenterTab.UPCOMING,
                        GlobalConstants.MatchCenterTab.SCORECARD,
                        GlobalConstants.MatchCenterTab.MATCH_INFO,
                        -> {
                            makeFocusOnChildFragment()
                            return true
                        }
                    }
                }

                KeyEvent.KEYCODE_DPAD_UP -> {
                    if (view?.id == R.id.btnNext || view?.id == R.id.btnPrevious ||
                        view?.id == R.id.buttonMatchCenter || view?.id == R.id.buttonWatchLive
                        || view?.id == R.id.btn_group_item || view?.tag is DefaultCardViewPresenter.CardInfo
                    ) {
                        focusItem()
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun showErrorPage(errorType: ErrorType, errorMessage: String? = null) {
        showError(binding.root, errorType, errorMessage, backBtnListener = {activity?.onBackPressed()}, btnText = "Back")
    }

    private fun makeFocusOnChildFragment() {
        if (fragment is BaseFragment) {
            (fragment as BaseFragment).focusItem()
        }
    }

    override fun focusItem() {
        radioCheckedId?.let {
            val radioButton = binding.tabRadioGroup.findViewById<RadioButton>(it)
            radioButton?.requestFocus()
        }
    }

    override fun onResume() {
        super.onResume()
        //  navigationMenuCallback?.navMenuVisibility(View.GONE)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // navigationMenuCallback?.navMenuVisibility(View.VISIBLE)
        EventBus.getDefault().post(MatchCenterDestroyed(true))
        if (mViewModel?.getTabNames()?.isNotEmpty()==true)
            destroyCallback?.fragmentDestroyed()

        destroyCallback=null

    }

    interface DestroyMethodCallback {
        fun fragmentDestroyed()
    }

    override fun onDetach() {
        super.onDetach()
        Timber.d("***** onDetach ")
        PlayerManager.shouldPlayContent = true
        EventBus.getDefault().post(KeyPressedEvent(-1,null))
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ScoreCardDpadUpEvent) {
        binding.mainHolder.requestFocus()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
        mViewModel = null
        radioCheckedId = null
        bySeries = null
        fragment = null

    }
}