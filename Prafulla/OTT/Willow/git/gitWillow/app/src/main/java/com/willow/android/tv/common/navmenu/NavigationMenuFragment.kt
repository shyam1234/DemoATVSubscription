package com.willow.android.tv.common.navmenu

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.willow.android.R
import com.willow.android.databinding.FragmentNavigationMenuBinding
import com.willow.android.tv.common.Types
import com.willow.android.tv.data.repositories.tvconfig.datamodel.APITVConfigDataModel
import com.willow.android.tv.utils.GlobalConstants.DEFAULT_PAGE
import com.willow.android.tv.utils.events.NavMenuFocusAccountEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class NavigationMenuFragment(): Fragment() {

    private var isOpenNav: Boolean  = false
    private lateinit var mBinding: FragmentNavigationMenuBinding
    private lateinit var fragmentChangeListener: FragmentChangeListener
    private lateinit var navigationStateListener: NavigationStateListener
    private var apiTVConfigDataModel: APITVConfigDataModel? = null
    private var menuTextAnimationDelay = 200
    var lastSelectedMenu: String? = DEFAULT_PAGE

    companion object{
        @JvmStatic
        fun newInstance(apiTVConfigDataModel: APITVConfigDataModel): NavigationMenuFragment {
            val args = Bundle()
            args.putParcelable("data", apiTVConfigDataModel)
            val fragment = NavigationMenuFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Retrieve the object from the arguments bundle
        apiTVConfigDataModel = arguments?.getParcelable<APITVConfigDataModel>("data")

        mBinding = FragmentNavigationMenuBinding.inflate(
            inflater.cloneInContext(context),
            container,
            false
        )
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        settingListener()
        profileListener()
    }


    private fun initRecyclerView() {
        if(this::fragmentChangeListener.isInitialized) {
            mBinding.rvNavMenuDynamicItemsHolder.layoutManager = LinearLayoutManager(context)
            apiTVConfigDataModel?.let {
                mBinding.rvNavMenuDynamicItemsHolder.adapter = NavigationMenuAdaptor(
                    this,
                    it.navigation,
                    fragmentChangeListener,
                    navigationStateListener
                )
            }
        }
    }

    private fun settingListener() {
        mBinding.ibNavMenuSetting.setOnFocusChangeListener { v, hasFocus ->
            if (isNavigationOpen()) {
                if (hasFocus) {
                    //setFocusedView(mBinding.ibNavMenuSetting, R.drawable.nav_menu_help_active_icon)
                    setMenuNameFocusView(mBinding.textviewNavMenuSetting, true)
                    focusIn(mBinding.ibNavMenuSetting)
                } else {
                    if(lastSelectedMenu?.equals(Types.ScreenName.HELP.name,true) == false) {
                        setOutOfFocusedView(
                            mBinding.ibNavMenuSetting,
                            R.drawable.nav_menu_help_icon
                        )
                        setMenuNameFocusView(mBinding.textviewNavMenuSetting, false)
                        focusOut(mBinding.ibNavMenuSetting)
                    }
                }
            }
        }
        mBinding.ibNavMenuSetting.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) when (keyCode) {
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    closeNav()
                    navigationStateListener.onStateChanged(false, lastSelectedMenu)
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    if (!mBinding.ibNavMenuSetting.isFocusable)
                        mBinding.ibNavMenuSetting.isFocusable = true
                }
                KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                    if(this::fragmentChangeListener.isInitialized) {
                        lastSelectedMenu = mBinding.textviewNavMenuSetting.text.toString()
                        fragmentChangeListener.switchFragment(lastSelectedMenu)
                        resetDynamicSelection()
                        closeNav()
                    }
                }
            }
            false
        }
    }

    private fun profileListener() {
        mBinding.ibNavMenuProfile.setOnFocusChangeListener { v, hasFocus ->
            if (isNavigationOpen()) {
                if (hasFocus) {
                   // setFocusedView(mBinding.ibNavMenuProfile, R.drawable.nav_menu_profile_active_icon)
                    setMenuNameFocusView(mBinding.textviewNavMenuProfile, true)
                    focusIn(mBinding.ibNavMenuProfile)
                } else {
                    if(lastSelectedMenu?.equals(Types.ScreenName.PROFILE.name,true) == false) {
                        setOutOfFocusedView(
                            mBinding.ibNavMenuProfile,
                            R.drawable.nav_menu_profile_icon
                        )
                        setMenuNameFocusView(mBinding.textviewNavMenuProfile, false)
                        focusOut(mBinding.ibNavMenuProfile)
                    }
                }
            }
        }
        mBinding.ibNavMenuProfile.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) when (keyCode) {
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    closeNav()
                    navigationStateListener.onStateChanged(false, lastSelectedMenu)
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    if (!mBinding.ibNavMenuProfile.isFocusable)
                        mBinding.ibNavMenuProfile.isFocusable = true
                }
                KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                    if(this::fragmentChangeListener.isInitialized) {
                        lastSelectedMenu = mBinding.textviewNavMenuProfile.text.toString()
                        fragmentChangeListener.switchFragment(lastSelectedMenu)
                        resetDynamicSelection()
                        closeNav()
                    }
                }
            }
            false
        }
    }

    private fun resetDynamicSelection() {
        (mBinding.rvNavMenuDynamicItemsHolder.adapter as NavigationMenuAdaptor).lastSelectedPosition = -1
    }

    private fun unHighlightMenuSelections(lastSelectedMenu: String?) {
        if (!lastSelectedMenu.equals( Types.ScreenName.HELP.name, true)) {
            setOutOfFocusedView(mBinding.ibNavMenuSetting, R.drawable.nav_menu_help_icon)
            setMenuNameFocusView(mBinding.textviewNavMenuSetting, false)
        }
        if (!lastSelectedMenu.equals( Types.ScreenName.PROFILE.name, true)) {
            setOutOfFocusedView(mBinding.ibNavMenuProfile, R.drawable.nav_menu_profile_icon)
            setMenuNameFocusView(mBinding.textviewNavMenuProfile, false)
        }
        mBinding.ibNavMenuSetting.clearFocus()
        mBinding.ibNavMenuProfile.clearFocus()
    }

    private fun highlightMenuSelection(lastSelectedMenu: String?) {
        when (lastSelectedMenu?.uppercase()) {
            Types.ScreenName.HELP.name-> {
                setFocusedView(mBinding.ibNavMenuSetting, R.drawable.nav_menu_help_active_icon)
                setMenuNameFocusView(mBinding.textviewNavMenuSetting, false)
            }
            Types.ScreenName.PROFILE.name-> {
                setFocusedView(mBinding.ibNavMenuProfile, R.drawable.nav_menu_profile_active_icon)
                setMenuNameFocusView(mBinding.textviewNavMenuProfile, false)
            }
        }
    }

    private fun enableNavMenuViews(visibility: Int) {
        if (visibility == View.GONE) {
            mBinding.textviewNavMenuSetting.visibility = visibility
            mBinding.textviewNavMenuProfile.visibility = visibility
        } else {
            animateMenuNamesEntry(mBinding.textviewNavMenuProfile, visibility)
            animateMenuNamesEntry(mBinding.textviewNavMenuSetting, visibility)
        }
    }

    fun isNavigationOpen(): Boolean {
        return isOpenNav //pp mBinding.textviewNavMenuSetting.visibility == View.VISIBLE
    }

    fun setOutOfFocusedView(view: ImageButton, resource: Int) {
        setMenuIconFocusView(resource, view, false)
    }

    fun setFocusedView(view: ImageButton, resource: Int) {
        setMenuIconFocusView(resource, view, true)
    }


    private fun setMenuIconFocusView(resource: Int, view: ImageButton, inFocus: Boolean) {
        view.setImageResource(resource)
    }

    fun setMenuNameFocusView(view: TextView, inFocus: Boolean) {
        if (inFocus) {
            view.setTextColor(ContextCompat.getColor(requireContext(), R.color.nav_menu_item_active))
            view.textSize = resources.getDimension(R.dimen.nav_menu_item_text_active_size)
        } else {
            view.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.nav_menu_item_unactive
                )
            )
            view.textSize = resources.getDimension(R.dimen.nav_menu_item_text_norm_size)
        }
    }

    /**
     * Setting animation when focus is lost
     */
    fun focusOut(v: View) {
        val scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1.2f, 1f)
        val set = AnimatorSet()
        set.play(scaleX).with(scaleY)
        //pp set.start()
    }

    /**
     * Setting the animation when getting focus
     */
    fun focusIn(v: View) {
        val scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 1.2f)
        val scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.2f)
        val set = AnimatorSet()
        set.play(scaleX).with(scaleY)
       //pp set.start()
    }

    fun animateMenuNamesEntry(view: View, visibility: Int) {
        view.postDelayed({
            view.visibility = visibility
            val animate = AnimationUtils.loadAnimation(context, R.anim.slide_in_left_menu_name)
            view.startAnimation(animate)
        }, menuTextAnimationDelay.toLong())
    }

    fun setFragmentChangeListener(callback: FragmentChangeListener) {
        this.fragmentChangeListener = callback
    }

    fun setNavigationStateListener(callback: NavigationStateListener) {
        this.navigationStateListener = callback
    }

    fun openNav() {
        isOpenNav = true
        mBinding.ivNavMenuLogo.setImageResource(R.drawable.willow_logo_in_open_state)
        mBinding.ivNavMenuLogo.setPadding(resources.getDimension(R.dimen.dimen_10dp).toInt(),0,0,0)
        mBinding.textviewNavMenuSetting.textSize = resources.getDimension(R.dimen.nav_menu_item_text_norm_size)
        mBinding.textviewNavMenuProfile.textSize = resources.getDimension(R.dimen.nav_menu_item_text_norm_size)
        enableNavMenuViews(View.VISIBLE)
        enableNavMenuViews(View.GONE)
        val lp = activity?.resources?.getDimensionPixelSize(R.dimen.nav_menu_open_vertical_tab_w)
            ?.let { FrameLayout.LayoutParams(it, ViewGroup.LayoutParams.MATCH_PARENT) }
        mBinding.clNavMenuOpen.layoutParams = lp
        navigationStateListener.onStateChanged(true, lastSelectedMenu)

        when (lastSelectedMenu?.uppercase()) {
           Types.ScreenName.HELP.name-> {
                mBinding.ibNavMenuSetting.requestFocus()
                setMenuNameFocusView(mBinding.textviewNavMenuSetting, true)
            }
            Types.ScreenName.PROFILE.name-> {
                mBinding.ibNavMenuProfile.requestFocus()
                setMenuNameFocusView(mBinding.textviewNavMenuProfile, true)
            }else -> {
               // mBinding.rvNavMenuDynamicItemsHolder.requestFocus()
            }
        }
         mBinding.rvNavMenuDynamicItemsHolder.adapter?.notifyDataSetChanged()
    }

    fun closeNav() {
        isOpenNav = false
        mBinding.ivNavMenuLogo.setImageResource(R.drawable.willow_logo_in_closed_state)
        mBinding.ivNavMenuLogo.setPadding(0,0,0,0)
        enableNavMenuViews(View.GONE)
        val lp = activity?.resources?.getDimensionPixelSize(R.dimen.nav_menu_closed_vertical_tab_w)
            ?.let { FrameLayout.LayoutParams(it, ViewGroup.LayoutParams.MATCH_PARENT) }
        mBinding.clNavMenuOpen.layoutParams = lp

        //highlighting last selected menu icon
        Timber.d("closeNav $lastSelectedMenu")
        highlightMenuSelection(lastSelectedMenu)

        //Setting out of focus views for menu icons, names
        unHighlightMenuSelections(lastSelectedMenu)
        mBinding.rvNavMenuDynamicItemsHolder.adapter?.notifyDataSetChanged()
    }

    fun restoreSelection() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: NavMenuFocusAccountEvent) {
        mBinding.ibNavMenuDummy.requestFocus()

        Timber.d("onMessageEvent NavMenuFocusAccountEvent:: "+view?.findFocus())
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
}
