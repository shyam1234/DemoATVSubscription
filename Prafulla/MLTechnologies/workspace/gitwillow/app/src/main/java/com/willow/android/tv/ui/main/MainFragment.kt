package com.willow.android.tv.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.willow.android.R
import com.willow.android.databinding.FragmentMainLeftMenuBinding
import com.willow.android.tv.common.Types
import com.willow.android.tv.common.base.BaseFragment
import com.willow.android.tv.common.navmenu.FragmentChangeListener
import com.willow.android.tv.common.navmenu.NavigationMenuCallback
import com.willow.android.tv.common.navmenu.NavigationMenuFragment
import com.willow.android.tv.common.navmenu.NavigationStateListener
import com.willow.android.tv.data.repositories.tvconfig.datamodel.APITVConfigDataModel
import com.willow.android.tv.data.repositories.tvconfig.datamodel.NavigationTabsDataModel
import com.willow.android.tv.ui.accountDetails.AccountDetailsFragment
import com.willow.android.tv.ui.explorepage.ExploreFragment
import com.willow.android.tv.ui.fixturespage.FixturesFragment
import com.willow.android.tv.ui.helpfaq.HelpFaqFragment
import com.willow.android.tv.ui.login.LoginActivity
import com.willow.android.tv.ui.main.viewmodel.MainViewModel
import com.willow.android.tv.ui.matchcenterpage.MatchCenterFragment
import com.willow.android.tv.ui.playback.PlaybackActivity
import com.willow.android.tv.ui.resultspage.ResultsFragment
import com.willow.android.tv.ui.subscription.SubscriptionActivity
import com.willow.android.tv.ui.videospage.FavTeamPageActivity
import com.willow.android.tv.ui.videospage.VideosFragment
import com.willow.android.tv.utils.CommonFunctions.whereToGo
import com.willow.android.tv.utils.GlobalConstants
import com.willow.android.tv.utils.GlobalConstants.DEFAULT_PAGE
import com.willow.android.tv.utils.GoTo
import com.willow.android.tv.utils.NavigationUtils
import com.willow.android.tv.utils.PrefRepository
import com.willow.android.tv.utils.config.GlobalTVConfig
import com.willow.android.tv.utils.events.CardClickedEvent
import com.willow.android.tv.utils.events.NavMenuToggleEvent
import com.willow.android.tv.utils.extension.startActivityWithData
import com.willow.android.tv.utils.extension.startActivityWithOutData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class MainFragment : Fragment(), FragmentChangeListener, NavigationStateListener,
    NavigationMenuCallback {

    //Default selection of page, later this will fetch from API

    private lateinit var mData: APITVConfigDataModel
    private lateinit var mNavigationMenu: NavigationMenuFragment
    private lateinit var mViewModel: MainViewModel
    private lateinit var mBinding: FragmentMainLeftMenuBinding
    private lateinit var prefRepository: PrefRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        prefRepository = PrefRepository(requireContext())

        mBinding =
            FragmentMainLeftMenuBinding.inflate(inflater.cloneInContext(context), container, false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        setObservers()

    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }


    private fun setObservers() {
        mViewModel.fetchTVConfig.observe(viewLifecycleOwner) {
            if (it != null) {
                initPage(it)
            }
        }
    }

    private fun initPage(data: APITVConfigDataModel) {
        mData = data
        mNavigationMenu = NavigationMenuFragment.newInstance(data)
        mNavigationMenu.setFragmentChangeListener(this)
        mNavigationMenu.setNavigationStateListener(this)
        NavigationUtils.onReplaceToFragmentContainer(
            activity as AppCompatActivity?,
            mBinding.flMainNavMenu.id,
            mNavigationMenu
        )
        switchFragment(DEFAULT_PAGE)
    }

    private fun getFragmentPage(name: String): Fragment {
        return when (getScreenType(name)) {
            Types.ScreenType.EXPLORE.name -> {
                ExploreFragment.newInstance(getNavigationTabDataModel(Types.ScreenType.EXPLORE.name))
            }
            Types.ScreenType.VIDEOS.name -> {
                VideosFragment.newInstance(data = getNavigationTabDataModel(Types.ScreenType.VIDEOS.name))
            }
            Types.ScreenType.FIXTURES.name -> {
                FixturesFragment.newInstance(getNavigationTabDataModel(Types.ScreenType.FIXTURES.name))
            }
            Types.ScreenType.RESULTS.name -> {
                ResultsFragment.newInstance(getNavigationTabDataModel(Types.ScreenType.RESULTS.name))
            }
            Types.ScreenType.PROFILE.name -> {
                AccountDetailsFragment.newInstance()
            }
            Types.ScreenType.HELP.name -> {
                HelpFaqFragment.newInstance(getNavigationTabDataModel(Types.ScreenType.HELP.name))
            }
            else -> {
                DefaultFragment()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


    private fun getScreenType(screenName: String): String? {
        mData.navigation.navigationTabs.forEach {
            if (screenName.equals(it.name, true)) {
                return it.getScreenType()?.name?.uppercase()
            }
        }
        /*
        * Here we are checking for help and profile as hardcoded
        * Because these menus is not dynamic which comes in json list
        * Both are static menu, so we are checking it as static
        * */
        if (screenName.equals(Types.ScreenName.HELP.name, true))
            return Types.ScreenType.HELP.name
        else if (screenName.equals(Types.ScreenType.PROFILE.name, true))
            return Types.ScreenType.PROFILE.name
        return null
    }

    /**
     * From navigation menu, fragment changes
     */
    override fun switchFragment(fragmentName: String?) {
        mBinding.flMainNavMenu.setBackgroundResource(R.drawable.ic_nav_bg_closed)
        when (fragmentName?.uppercase()) {
            Types.ScreenName.PROFILE.name -> {
                if(prefRepository.getLoggedIn() == true){
                    mViewModel.checkSubscription()
                }
                val fragment = AccountDetailsFragment.newInstance()
                fragment.setNavigationCallback(this)
                NavigationUtils.onReplaceToFragmentContainer(
                    activity as AppCompatActivity?,
                    mBinding.flMainHolder.id,
                    fragment,
                    true
                )
            }

            Types.ScreenName.EXPLORE.name -> {
                val page = getFragmentPage(fragmentName) as ExploreFragment
                page.setNavigationCallback(this)
                NavigationUtils.onReplaceToFragmentContainer(
                    activity as AppCompatActivity?,
                    mBinding.flMainHolder.id,
                    page, false
                )
                mNavigationMenu.restoreSelection()
            }
            Types.ScreenName.VIDEOS.name -> {
                val page = getFragmentPage(fragmentName) as VideosFragment
                page.setNavigationCallback(this)
                NavigationUtils.onReplaceToFragmentContainer(
                    activity as AppCompatActivity?,
                    mBinding.flMainHolder.id,
                    page, true
                )
                mNavigationMenu.restoreSelection()
            }
            Types.ScreenName.FIXTURES.name -> {
                val page = getFragmentPage(fragmentName) as FixturesFragment
                page.setNavigationCallback(this)
                NavigationUtils.onReplaceToFragmentContainer(
                    activity as AppCompatActivity?,
                    mBinding.flMainHolder.id,
                    page,
                    true
                )
                mNavigationMenu.restoreSelection()
            }
            Types.ScreenName.RESULTS.name -> {
                val page = getFragmentPage(fragmentName) as ResultsFragment
                page.setNavigationCallback(this)
                NavigationUtils.onReplaceToFragmentContainer(
                    activity as AppCompatActivity?,
                    mBinding.flMainHolder.id,
                    page,
                    true
                )
                mNavigationMenu.restoreSelection()
            }
            Types.ScreenName.HELP.name -> {
                val page = getFragmentPage(fragmentName) as HelpFaqFragment
                page.setNavigationCallback(this)
                NavigationUtils.onReplaceToFragmentContainer(
                    activity as AppCompatActivity?,
                    mBinding.flMainHolder.id,
                    page,
                    true
                )
                mNavigationMenu.restoreSelection()
            }
        }
    }

    /**
     * listener navigation menu state changes
     */
    override fun onStateChanged(expanded: Boolean, lastSelected: String?) {
        if (!expanded) {
            mBinding.flMainNavMenu.setBackgroundResource(R.drawable.ic_nav_bg_closed)
            mBinding.flMainNavMenu.clearFocus()

            lifecycleScope.launch {
                delay(100)
                val currentVisibleFragment = getVisibleFragment()
                currentVisibleFragment?.let {
                    when (it) {
                        is BaseFragment -> it.focusItem()
                    }
                }
            }
        }
    }

    override fun navMenuToggle(toShow: Boolean) {
        if (toShow) {
            mBinding.flMainNavMenu.setBackgroundResource(R.drawable.ic_nav_bg_open)
            mBinding.flMainHolder.clearFocus()
            mBinding.flMainNavMenu.requestFocus()
            navEnterAnimation()
            mNavigationMenu.openNav()
        } else {
            mBinding.flMainNavMenu.setBackgroundResource(R.drawable.ic_nav_bg_closed)
            mBinding.flMainNavMenu.clearFocus()
            mBinding.flMainHolder.requestFocus()
            mNavigationMenu.closeNav()
        }
    }


    private fun navEnterAnimation() {
        val animate = AnimationUtils.loadAnimation(activity, R.anim.anim_nav_menu_enter)
        mBinding.flMainNavMenu.startAnimation(animate)
    }

    override fun onAttachFragment(childFragment: Fragment) {
        when (childFragment) {
            is ExploreFragment -> {
                childFragment.setNavigationCallback(this)
            }
            is NavigationMenuFragment -> {
                childFragment.setFragmentChangeListener(this)
                childFragment.setNavigationStateListener(this)
            }
        }
    }

    private fun getNavigationTabDataModel(name: String): NavigationTabsDataModel? {
        mData.navigation.navigationTabs.forEach {
            if (it.name.equals(name, true)) {
                return it
            }
        }
        return null
    }

    fun setToHomePage() {
        switchFragment(DEFAULT_PAGE)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: CardClickedEvent) {

        Timber.d("onMessageEvent :: $event")
        redirectToTarget(event)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: NavMenuToggleEvent) {
        Timber.d("onMessageEvent NavMenuToggleEvent:: " + view?.findFocus())
        navMenuToggle(event.toShow)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun navMenuVisibility(visibility: Int) {
        mBinding.flMainNavMenu.visibility = visibility
    }

    override fun navMenuScreenName(screenName: Types.ScreenName) {
        makeExplorerDefaultSelection(screenName.name)
    }

    /**
     * this method helps to navigate control to the target page
     */
    private fun redirectToTarget(event: CardClickedEvent) {
        Timber.d("redirectToTarget target_type :: "+event.card.target_type +" > target_action:: "+event.card.target_action)

        if (event.card.target_type?.uppercase() == Types.TargetType.TEAM.name
            && event.card.target_action?.uppercase() == Types.TargetAction.DETAIL.name
        ) {
            GlobalTVConfig.currentPage = resources.getStringArray(R.array.arrayPages)[7]
            requireActivity().startActivityWithData<FavTeamPageActivity>(
                GlobalConstants.ActivityType.DETAILS_PAGE,
                GlobalTVConfig.getBaseUrl(event.card.base_url_type) + event.card.target_url
            )

        } else if (event.card.target_type?.uppercase() == Types.TargetType.MATCH.name
            && event.card.target_action?.uppercase() == Types.TargetAction.DETAIL.name
        ) {

            redirectToMatchCenter(event)

        } else if (event.card.target_type?.uppercase() == Types.TargetType.SERIES.name
            && event.card.target_action?.uppercase() == Types.TargetAction.DETAIL.name
        ) {

            redirectToMatchCenter(event)

        } else if (event.card.target_type?.uppercase() == Types.TargetType.PLAYER.name
            && event.card.target_action?.uppercase() == Types.TargetAction.VIDEO.name
        ) {

            redirectToPlayer(event)
        }

    }

    fun redirectToMatchCenter(event: CardClickedEvent) {
        GlobalTVConfig.currentPage = resources.getStringArray(R.array.arrayPages)[6]
        NavigationUtils.onAddToFragmentContainer(
            activity as AppCompatActivity?,
            mBinding.flMainHolder.id,
            MatchCenterFragment.newInstance(event.card.target_url.toString()),
            addToBackStack = true
        )

//        NavigationUtils.onReplaceToFragmentContainer(
//            activity as AppCompatActivity?,
//            mBinding.flMainHolder.id,
//            MatchCenterFragment.newInstance(event.card.target_url.toString(), this),
//            true
//        )

    }


    fun redirectToPlayer(event: CardClickedEvent) {
        val whereTo = whereToGo(event.card, requireContext())
        when (whereTo) {
            GoTo.LOGIN -> {
                requireContext().startActivityWithOutData<LoginActivity>()
            }
            GoTo.PLAY_VIDEO -> {
                requireContext().startActivityWithData<PlaybackActivity>(
                    event.card,
                    event.cardRow
                )
            }
            GoTo.SUBSCRIPTION -> {
                requireContext().startActivityWithData<SubscriptionActivity>(event.card)
            }
        }
    }

    private fun getVisibleFragment(): Fragment? {
        return fragmentManager?.findFragmentById(R.id.fl_main_holder)
    }

    private fun makeExplorerDefaultSelection(menuName: String) {
        mNavigationMenu.lastSelectedMenu = menuName
        navMenuToggle(false)
    }

}