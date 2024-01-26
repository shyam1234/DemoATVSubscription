package com.willow.android.tv.ui.scoreCardMatchInfo

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
import com.willow.android.databinding.FragmentScorecardBinding
import com.willow.android.tv.common.base.BaseFragment
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.data.repositories.fixturespage.datamodel.APIFixturesDataModel
import com.willow.android.tv.ui.matchcenterpage.MatchCenterVideosTabFragment
import com.willow.android.tv.ui.matchcenterpage.UpcomingMatchesByDateFragment
import com.willow.android.tv.ui.pointtable.PointTableGroupFragment
import com.willow.android.tv.ui.scoreCardMatchInfo.model.ScorecardTableData
import com.willow.android.tv.ui.scoreCardMatchInfo.viewmodel.ScorecardPageViewModel
import com.willow.android.tv.ui.scoreCardMatchInfo.viewmodel.ScorecardPageViewModelFactory
import com.willow.android.tv.utils.*
import com.willow.android.tv.utils.events.KeyPressedEvent
import com.willow.android.tv.utils.events.ScoreCardDpadUpEvent
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class ScoreCardFragment() : BaseFragment(), KeyListener {

    private var indexOfItem: Int = -1
    private lateinit var mViewModel: ScorecardPageViewModel
    private lateinit var mBinding: FragmentScorecardBinding
    private var radioCheckedId: Int? = null
    private var fragment: Fragment? = null

    init {
        Timber.d("called ScoreCardFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            FragmentScorecardBinding.inflate(inflater.cloneInContext(context), container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProvider(
            this,
            ScorecardPageViewModelFactory(WillowApplication.instance)
        )[(ScorecardPageViewModel::class.java)]
        arguments?.getString(GlobalConstants.Keys.TARGET_URL)
            ?.let { mViewModel.makeScorecardPageDataRequest(context, it) }
        mViewModel.scorecardData.observe(viewLifecycleOwner) {
            if(it?.isNotEmpty() == true) {
                setUpTabsnViewPager(it)
            }else{
                showError(mBinding.root, ErrorType.NO_DATA_FOUND)
            }
        }
    }

//    private fun setUpTabsnViewPager(scorecardTableData: List<ScorecardTableData>) {
//        val viewPager = mBinding.viewPager
//        val tabLayout = mBinding.tabLayout
//        val adapter = ScorecardViewPagerAdapter(childFragmentManager, lifecycle,scorecardTableData)
//        viewPager.adapter = adapter
//
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = scorecardTableData[position].tabName
//        }.attach()
//    }

    private fun onSuccess(data: APIFixturesDataModel?) {
        mBinding.loadingview.layoutLoading.hide()
    }

    private fun onLoading() {
        mBinding.loadingview.layoutLoading.show()
        val imageViewAnimator = ObjectAnimator.ofFloat(mBinding.loadingview.progressBar, View.ROTATION, 359f)
        imageViewAnimator.repeatCount = Animation.INFINITE
        imageViewAnimator.duration = 1000
        imageViewAnimator.start()
    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }
    companion object{
        fun getInstance(url: String?) = ScoreCardFragment().apply {
            arguments=Bundle().apply {
                putString(GlobalConstants.Keys.TARGET_URL,url)
            }
        }
    }

    override fun focusItem() {
        Timber.d("$$$$ Score card Focus Item ")
        mBinding.tabRadioGroup.getChildAt(0).requestFocus()
    }

    private fun setUpTabsnViewPager(tabsList: List<ScorecardTableData>) {
        val fragmentContainer = mBinding.fragmentContainer
        val tabRadioGroup = mBinding.tabRadioGroup

        tabRadioGroup.removeAllViews()
        for (i in tabsList.indices) {
            var radioButton = RadioButton(requireContext())
            radioButton.text = "  " + tabsList[i].tabName + "  "
            radioButton.tag = tabsList[i].tabName
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
                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            Timber.d("$$$$ KEYCODE_DPAD_UP")
                            makeFocusOnParentFragment()
                        }


                    }
                }
                onKey(view, keyCode, event)
            }
        }



        tabRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = tabRadioGroup.findViewById<RadioButton>(checkedId)
            radioCheckedId = checkedId
            val selectedIndex = tabRadioGroup.indexOfChild(
                tabRadioGroup.findViewById(checkedId)
            )
            fragment = ScoreCardInnerFragment.newInstance(tabsList[selectedIndex])
            navigateToFragment(fragment!!)

        }


        tabRadioGroup.check(tabRadioGroup.get(0).id)

    }

    private fun makeFocusOnParentFragment() {
        EventBus.getDefault().post(ScoreCardDpadUpEvent(true))
    }

    private fun navigateToFragment(fragment: Fragment) {
        NavigationUtils.onReplaceToFragmentContainer(
            activity as AppCompatActivity?,
            childFragmentManager,
            mBinding.fragmentContainer.id,
            fragment,
            true
        )
    }
}