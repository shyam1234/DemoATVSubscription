package com.willow.android.mobile.views.pages.fixturesPage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.databinding.CardFixtureBinding
import com.willow.android.databinding.CompTeamScoreBinding
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.models.pages.FixtureModel
import com.willow.android.mobile.services.LocalNotificationService
import com.willow.android.tv.utils.ImageUtility


class FixturesByDateSectionAdapter(val context: Context, val fixturesModel: MutableList<FixtureModel>): RecyclerView.Adapter<FixturesByDateSectionAdapter.FixturesByDateViewHolder>() {

    inner class FixturesByDateViewHolder(val binding: CardFixtureBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: FixtureModel) {
            binding.matchName.text = sectionData.subtitle
            binding.seriesName.text = sectionData.series_name
                ImageUtility.loadImageDontTransform(
                    imageUrl = sectionData.stadium_image,
                    placeHolder = R.drawable.ground_ph,
                    view =  binding.stadiumImage
                )

            binding.stadiumName?.let  {
                binding.stadiumName.text = sectionData.venue
            }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FixturesByDateViewHolder {
        return FixturesByDateViewHolder(CardFixtureBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FixturesByDateViewHolder, position: Int) {
        val sectionData = fixturesModel[position]
        holder.setData(sectionData)
    }

    override fun getItemCount(): Int {
        return fixturesModel.size
    }

    private fun setTeamData(binding: CompTeamScoreBinding, logo: String, name: String) {
        ImageUtility.loadImageInto(logo,binding.icon)
        binding.name.text = name
    }

    private fun setNotificationIconImage(sectionData: FixtureModel, binding: CardFixtureBinding) {
        if (sectionData.isNotificationEnabled) {
            binding.notificationIcon.setImageResource(R.drawable.notification_sel)
        } else {
            binding.notificationIcon.setImageResource(R.drawable.notification)
        }
    }
}