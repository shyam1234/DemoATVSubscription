package com.willow.android.mobile.views.pages.profilePage

import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.databinding.CardSettingBinding
import com.willow.android.databinding.CardSettingProfileBinding
import com.willow.android.databinding.CardSettingSubscriptionBinding
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.views.pages.forgotPasswordPage.ForgotPasswordPageActivity
import com.willow.android.mobile.views.popup.decisionPopup.DeleteAccountPopup
import tv.willow.Models.SettingsItemModel
import tv.willow.Models.SettingsSubItem

interface ProfileListenerInterface {
    fun requestPasswordListener()
    fun verifyEmailListener()
    fun deleteAccountListener()
}

class ProfilePageAdapter(val context: Context, val settingsItemModel: SettingsItemModel, val profileListenerInterface: ProfileListenerInterface): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val PROFILE_ITEM_VIEW = 0
    val PROFILE_USER_VIEW = 1
    val PROFILE_SUBSCRIPTION_VIEW = 2

    inner class ProfileUserViewHolder(val binding: CardSettingProfileBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: SettingsSubItem) {
            createProfileUserSection(binding, sectionData)

            itemView.setOnClickListener(View.OnClickListener {
//                handleLoginLogout()
            })
        }
    }

    inner class ProfileSubscriptionViewHolder(val binding: CardSettingSubscriptionBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: SettingsSubItem) {
            binding.profileUserTitle.text = MessageConfig.subscriptionTitle
            binding.profileUserSubtitle.text = MessageConfig.subscriptionSubtitle + UserModel.nextRenewalDate

            itemView.setOnClickListener(View.OnClickListener {
//                handleLoginLogout()
            })
        }
    }

    inner class ProfileItemModelViewHolder(val binding: CardSettingBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: SettingsSubItem) {
            createProfileSection(binding, sectionData)

            itemView.setOnClickListener(View.OnClickListener {
                when (sectionData.action) {
                    "request_password" -> launchRequestPassword()
                    "verify_email" -> launchVerifyEmail()
                    "delete_account" -> launchDeleteAccountPopup()
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == PROFILE_USER_VIEW) {
            return ProfileUserViewHolder(CardSettingProfileBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        } else if (viewType == PROFILE_SUBSCRIPTION_VIEW) {
            return ProfileSubscriptionViewHolder(CardSettingSubscriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        return ProfileItemModelViewHolder(CardSettingBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val sectionData = settingsItemModel.subitems[position]
        if (holder is ProfileUserViewHolder) {
            holder.setData(sectionData)
        } else if (holder is ProfileSubscriptionViewHolder) {
            holder.setData(sectionData)
        } else if (holder is ProfileItemModelViewHolder) {
            holder.setData(sectionData)
        }
    }

    override fun getItemCount(): Int {
        return (settingsItemModel.subitems.size)
    }

    override fun getItemViewType(position: Int): Int {
        val sectionData = settingsItemModel.subitems[position]
        if (sectionData.action == "account_details") {
            return PROFILE_USER_VIEW
        } else if (sectionData.action == "subscription_details") {
            return PROFILE_SUBSCRIPTION_VIEW
        }

        return PROFILE_ITEM_VIEW
    }


    fun createProfileUserSection(binding: CardSettingProfileBinding, sectionData: SettingsSubItem) {
        val resourceId = context.resources.getIdentifier(sectionData.icon, "drawable", context.packageName)

        binding.settingsUserIcon.setImageResource(resourceId)
        binding.settingsUserTitle.text = UserModel.displayTitle

        val title = getColoredSpanned("Subscription Status : ", context.resources.getColor(R.color.settingSubtitle))
        var subsStatus: String?
        if (UserModel.isSubscribed) {
            subsStatus = getColoredSpanned("Active", context.resources.getColor(R.color.active_green))
        } else {
            subsStatus = getColoredSpanned("Inactive", context.resources.getColor(R.color.inactive_red))
        }

        binding.settingsUserSubtitle.text = Html.fromHtml(title + subsStatus)
    }

    fun createProfileSection(binding: CardSettingBinding, sectionData: SettingsSubItem) {
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


    fun launchRequestPassword() {
        if (UserModel.isLoggedIn()) {
            profileListenerInterface.requestPasswordListener()
        } else {
            val intent = Intent(context, ForgotPasswordPageActivity::class.java).apply {}
            context.startActivity(intent)
        }
    }

    fun launchVerifyEmail() {
        profileListenerInterface.verifyEmailListener()
    }

    private fun launchDeleteAccountPopup() {
        val activity  = context as? ProfilePageActivity
        if (activity != null) {
            val newFragment = DeleteAccountPopup()
            newFragment.onResult = {
                profileListenerInterface.deleteAccountListener()
            }
            newFragment.show(activity.supportFragmentManager, "dialog")
        }
    }

    private fun getColoredSpanned(text: String, color: Int): String? {
        return "<font color=$color>$text</font>"
    }
}