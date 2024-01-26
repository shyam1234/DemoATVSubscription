package com.willow.android.mobile.views.pages.homePage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.databinding.CardHomeFixtureBinding
import com.willow.android.databinding.CompTeamScoreBinding
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.models.pages.FixtureModel
import com.willow.android.mobile.services.LocalNotificationService
import com.willow.android.tv.utils.ImageUtility


class HomeFixturesSectionAdapter(val context: Context, val fixturesList: MutableList<FixtureModel>): RecyclerView.Adapter<HomeFixturesSectionAdapter.FixturesSectionViewHolder>() {

    inner class FixturesSectionViewHolder(val binding: CardHomeFixtureBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: FixtureModel) {
            binding.matchName.text = sectionData.subtitle
            binding.seriesName.text = sectionData.series_name

            setTeamData(binding.teamOne, sectionData.team_one_logo, sectionData.team_one_fname)
            setTeamData(binding.teamTwo, sectionData.team_two_logo, sectionData.team_two_fname)

            if (sectionData.tve_only_series) {
                binding.tveEverywhereText.text = MessageConfig.tveOnlyMessage
                binding.tveEverywhereText.visibility = View.VISIBLE
            } else {
                binding.tveEverywhereText.visibility = View.GONE
            }
            setNotificationIconImage(sectionData, binding)

            binding.notificationIcon.setOnClickListener {
                sectionData.toggleNotificationSelection()
                if (sectionData.isNotificationEnabled) {
                    LocalNotificationService.scheduleLocalNotificationAlarm(context, sectionData)
                } else {
                    LocalNotificationService.removeLocalNotificationAlarm(context, sectionData)
                }
                setNotificationIconImage(sectionData, binding)
            }
        }
    }

    private fun setTeamData(binding: CompTeamScoreBinding, logo: String, name: String) {
        ImageUtility.loadImageInto(logo,binding.icon)
        binding.name.text = name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FixturesSectionViewHolder {
        return FixturesSectionViewHolder(CardHomeFixtureBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FixturesSectionViewHolder, position: Int) {
        val sectionData = fixturesList[position]
        holder.setData(sectionData)
    }

    override fun getItemCount(): Int {
        return fixturesList.size
    }

    private fun setNotificationIconImage(sectionData: FixtureModel, binding: CardHomeFixtureBinding) {
        if (sectionData.isNotificationEnabled) {
            binding.notificationIcon.setImageResource(R.drawable.notification_sel)
        } else {
            binding.notificationIcon.setImageResource(R.drawable.notification)
        }
    }
}