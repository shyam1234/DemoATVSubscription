package com.willow.android.tv.common.navmenu

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.tv.common.Types
import com.willow.android.tv.data.repositories.tvconfig.datamodel.NavigationDataModel
import com.willow.android.tv.data.repositories.tvconfig.datamodel.NavigationTabsDataModel
import com.willow.android.tv.utils.events.NavMenuFocusAccountEvent
import org.greenrobot.eventbus.EventBus
import timber.log.Timber


class NavigationMenuAdaptor(
    private val fragment: NavigationMenuFragment,
    private val data: NavigationDataModel,
    private val fragmentChangeListener: FragmentChangeListener,
    private val navigationStateListener: NavigationStateListener
) : RecyclerView.Adapter<NavigationMenuAdaptor.NavigationMenuVH>() {

    var lastSelectedPosition: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationMenuVH {
        val context: Context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the layout
        val view = inflater.inflate(R.layout.row_dynamic_nav_menu_item, parent, false)
        return NavigationMenuVH(view)
    }

    override fun onBindViewHolder(holder: NavigationMenuVH, position: Int) {
        //this is for fixed profile
        holder.menuName.text = data.navigationTabs[position].name
        //need to write code to replace the hardcode icon with the remote icon
        //holder.menuName.textSize = fragment.context?.resources?.getDimension(R.dimen.nav_menu_item_text_norm_size)?:12.0f
        holder.menuIcon.setImageResource(getIcon(holder.menuName.text.toString(), false))
        handleLastSelection(holder, position, holder.menuName.text.toString())
        setListeners(holder, holder.menuName.text.toString(), position)
        handleEventBasedOnNavDrawerUIState(holder, position, holder.menuName.text.toString())
    }


    private fun setListeners(holder: NavigationMenuVH, name: String, currPosition: Int) {
        holder.menuIcon.setOnFocusChangeListener { v, hasFocus ->
            if (fragment.isNavigationOpen()) {
                if (hasFocus) {
                    //fragment.setFocusedView(holder.menuIcon as ImageButton, getIcon(name, true))
                    fragment.focusIn(holder.menuIcon)
                    fragment.setMenuNameFocusView(holder.menuName, true)
                }else {
                    if(!fragment.lastSelectedMenu.equals(name,true) /*&& lastSelectedPosition != currPosition*/) {
                        fragment.setOutOfFocusedView(holder.menuIcon as ImageButton, getIcon(name, false))
                        fragment.setMenuNameFocusView(holder.menuName, false)
                        fragment.focusOut(holder.menuIcon)
                    }
                }
            }

        }
        holder.menuIcon.setOnKeyListener { v, keyCode, event ->
            Timber.d("keycode menuIcon $keyCode")
            if (event.action == KeyEvent.ACTION_DOWN)
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        closeNav()
                        navigationStateListener.onStateChanged(false, fragment.lastSelectedMenu)
                    }
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        if (!holder.menuIcon.isFocusable)
                            holder.menuIcon.isFocusable = true
                    }
                    KeyEvent.KEYCODE_DPAD_DOWN ->{
                        if(currPosition+1== itemCount){
                            Timber.d("keycode menuIcon end of list")
                            EventBus.getDefault().post(NavMenuFocusAccountEvent(true))
                        }
                    }
                    KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                        fragment.lastSelectedMenu = holder.menuName.text.toString()
                        lastSelectedPosition = currPosition
                        fragmentChangeListener.switchFragment(fragment.lastSelectedMenu)
                        closeNav()
                    }
                }
            false
        }
    }

    private fun closeNav() {
        fragment.closeNav()
    }

    private fun handleEventBasedOnNavDrawerUIState(
        holder: NavigationMenuVH,
        currPosition: Int,
        name: String
    ) {
        //if nav menu open
        if (fragment.isNavigationOpen()) {
            enableNavMenuViews(View.VISIBLE, holder)
            if (fragment.lastSelectedMenu.equals(name,true)/* lastSelectedPosition == currPosition*/) {
                holder.menuIcon.requestFocus()
            }
        } else {
            //if nav menu close
            enableNavMenuViews(View.GONE, holder)
            if (fragment.lastSelectedMenu.equals(name,true) /*lastSelectedPosition == currPosition*/) {
                fragment.setFocusedView(holder.menuIcon as ImageButton, getIcon(name, true))
                //fragment.setMenuNameFocusView(holder.menuName, true)
            } else {
                fragment.setOutOfFocusedView(holder.menuIcon as ImageButton, getIcon(name, false))
                fragment.setMenuNameFocusView(holder.menuName, false)
            }
        }
    }

    private fun handleLastSelection(
        holder: NavigationMenuVH,
        currPosition: Int,
        name: String
    ) {
        if (fragment.lastSelectedMenu.equals(name,true)) {
            fragment.setOutOfFocusedView(holder.menuIcon as ImageButton, getIcon(name, true))
            fragment.setMenuNameFocusView(holder.menuName, true)
        }
    }

    private fun getIcon(name: String, isActive: Boolean): Int {
        if (isActive) {
            return when (name.uppercase()) {
                Types.ScreenName.EXPLORE.name -> {
                    R.drawable.nav_menu_home_active_icon
                }
                Types.ScreenName.VIDEOS.name -> {
                    R.drawable.nav_menu_video_active_icon
                }
                Types.ScreenName.FIXTURES.name -> {
                    R.drawable.nav_menu_fixture_active_icon
                }
                Types.ScreenName.RESULTS.name -> {
                    R.drawable.nav_menu_result_active_icon
                }
                else -> {
                    R.drawable.nav_menu_fixture_active_icon
                }
            }
        } else {
            return when (name.uppercase()) {
                Types.ScreenName.EXPLORE.name -> {
                    R.drawable.nav_menu_home_icon
                }
                Types.ScreenName.VIDEOS.name -> {
                    R.drawable.nav_menu_video_icon
                }
                Types.ScreenName.FIXTURES.name -> {
                    R.drawable.nav_menu_fixture_icon
                }
                Types.ScreenName.RESULTS.name -> {
                    R.drawable.nav_menu_result_icon
                }
                Types.ScreenName.PROFILE.name -> {
                    R.drawable.nav_menu_profile_icon
                }
                else -> {
                    R.drawable.nav_menu_fixture_icon
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.navigationTabs.size
    }

    inner class NavigationMenuVH(view: View) : RecyclerView.ViewHolder(view) {
        var menuIcon: ImageView = view.findViewById<AppCompatImageButton>(R.id.img_btn_menu_item)
        var menuName: TextView = view.findViewById(R.id.txtview_menu_item)
    }

    private fun getNavTabData(position: Int): NavigationTabsDataModel? {
        if (data.navigationTabsOrder.isNotEmpty()) {
            data.navigationTabsOrder[position].let { id ->
                data.navigationTabs.forEach {
                    if (it.id == id) return it
                }
            }
        } else {
            return data.navigationTabs[position]
        }
        return null
    }

    private fun enableNavMenuViews(visibility: Int, holder: NavigationMenuVH) {
        if (visibility == View.GONE) {
            holder.menuName.visibility = visibility
        } else {
            fragment.animateMenuNamesEntry(holder.menuName, visibility)
        }
    }
}


