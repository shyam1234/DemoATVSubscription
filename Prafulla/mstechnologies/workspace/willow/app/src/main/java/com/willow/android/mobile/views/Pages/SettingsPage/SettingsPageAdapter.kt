package com.willow.android.mobile.views.pages.settingsPage

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.databinding.CardSettingBinding
import com.willow.android.databinding.CardSettingProfileBinding
import com.willow.android.mobile.MainActivity
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.views.pages.WebViewPageActivity
import com.willow.android.mobile.views.pages.loginPage.LoginPageActivity
import com.willow.android.mobile.views.pages.othersPage.OthersPageActivity
import com.willow.android.mobile.views.pages.preferencesPage.PreferencesPageActivity
import com.willow.android.mobile.views.popup.decisionPopup.LogoutPopup
import tv.willow.Models.SettingsItemModel
import tv.willow.Models.SettingsPageModel

interface LogoutListenerInterface {
    fun logoutRefresh()
    fun launchProfileActivity(itemData: SettingsItemModel)
}

class SettingsPageAdapter(val context: Context, val settingsPageModel: SettingsPageModel, val logoutListenerInterface: LogoutListenerInterface): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val SETTINGS_ITEM_VIEW = 0
    val SETTINGS_USER_VIEW = 1

    inner class SettingsUserViewHolder(val binding: CardSettingProfileBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: SettingsItemModel) {
            createSettingsUserSection(binding, sectionData)

            itemView.setOnClickListener(View.OnClickListener {
                handleLoginLogout(sectionData)
            })
        }
    }

    inner class SettingsItemModelViewHolder(val binding: CardSettingBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: SettingsItemModel) {
            createSettingsSection(binding, sectionData)

            itemView.setOnClickListener(View.OnClickListener {
                when (sectionData.action) {
                    "profile" -> showProfileScreen(sectionData)
                    "preferences" -> showPreferencesScreen(itemData = sectionData)
                    "webview" -> showWebView(itemData = sectionData)
                    "subsection" -> showSubsections(itemData = sectionData)
                    "logout" -> handleLogout()
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == SETTINGS_USER_VIEW) {
            return SettingsUserViewHolder(CardSettingProfileBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }

        return SettingsItemModelViewHolder(CardSettingBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val sectionData = settingsPageModel.result[position]
        if (holder is SettingsUserViewHolder) {
            holder.setData(sectionData)
        } else if (holder is SettingsItemModelViewHolder) {
            holder.setData(sectionData)
        }
    }

    override fun getItemCount(): Int {
        return (settingsPageModel.result.size)
    }

    override fun getItemViewType(position: Int): Int {
        val sectionData = settingsPageModel.result[position]
        if (sectionData.action == "profile") {
            return SETTINGS_USER_VIEW
        }

        return SETTINGS_ITEM_VIEW
    }


    fun createSettingsUserSection(binding: CardSettingProfileBinding, sectionData: SettingsItemModel) {
        val resourceId = context.resources.getIdentifier(sectionData.icon, "drawable", context.packageName)

        binding.arrowIcon.visibility = View.VISIBLE
        binding.settingsUserIcon.setImageResource(resourceId)
        binding.settingsUserTitle.text = UserModel.displayTitle
        if (UserModel.isTVEUser) {
            binding.settingsUserSubtitle.text = ""
        } else {
            binding.settingsUserSubtitle.text = UserModel.displaySubtitle
        }
    }

    fun createSettingsSection(binding: CardSettingBinding, sectionData: SettingsItemModel) {
        val resourceId = context.resources.getIdentifier(sectionData.icon, "drawable", context.packageName)

        binding.settingsIcon.setImageResource(resourceId)
        binding.settingsTitle.text = sectionData.title

        if (sectionData.subtitle.isNotEmpty()) {
            binding.settingsSubtitle.visibility = View.VISIBLE
            binding.settingsSubtitle.text = sectionData.subtitle
        } else {
            binding.settingsSubtitle.visibility = View.GONE
        }
    }

    fun handleLoginLogout(sectionData: SettingsItemModel) {
        if (UserModel.isLoggedIn()) {
            if (!UserModel.isTVEUser) {
                showProfileScreen(sectionData)
            }
        } else {
            val intent = Intent(context, LoginPageActivity::class.java)
            context.startActivity(intent)
        }
    }

    fun handleLogout() {
        if (UserModel.isLoggedIn()) {
            val activity  = context as? MainActivity
            if (activity != null) {
                val newFragment = LogoutPopup()
                newFragment.onResult = {
                    logoutListenerInterface.logoutRefresh()
                }
                newFragment.show(activity.supportFragmentManager, "dialog")
            }
        }
    }

    fun showPreferencesScreen(itemData: SettingsItemModel) {
        val intent = Intent(context, PreferencesPageActivity::class.java).apply {}
        context.startActivity(intent)
    }

    fun showWebView(itemData: SettingsItemModel) {
        val intent = Intent(context, WebViewPageActivity::class.java).apply {}
        intent.putExtra("url", itemData.url)
        intent.putExtra("PAGE_TITLE", itemData.title)
        context.startActivity(intent)
    }

    fun showSubsections(itemData: SettingsItemModel) {
        val intent = Intent(context, OthersPageActivity::class.java).apply {}
        intent.putExtra("SETTINGS_ITEM", itemData)
        context.startActivity(intent)
    }

    fun showProfileScreen(itemData: SettingsItemModel) {
        logoutListenerInterface.launchProfileActivity(itemData)
    }
}