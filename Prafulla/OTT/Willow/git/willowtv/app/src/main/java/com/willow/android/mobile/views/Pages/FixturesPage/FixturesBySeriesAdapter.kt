package com.willow.android.mobile.views.pages.fixturesPage;


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.databinding.CardFixtureBinding
import com.willow.android.databinding.CompTeamScoreBinding
import com.willow.android.databinding.HeaderExpandableBinding
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.models.pages.FixturesPageModel
import com.willow.android.mobile.models.pages.UIFixtureModel
import com.willow.android.mobile.services.LocalNotificationService
import com.willow.android.tv.utils.ImageUtility
import com.willow.android.tv.utils.ImageUtility.loadImageDontTransform


class FixturesBySeriesAdapter(val context:Context, val fixturesPageModel: FixturesPageModel): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class FixturesBySeriesHeaderViewHolder(val binding: HeaderExpandableBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(uiFixture: UIFixtureModel, position: Int) {
            binding.eHeaderTitle.text = uiFixture.fixture.series_name
            binding.eHeaderSubtitle.text = uiFixture.fixture.seriesSubtitle

            binding.root.setOnClickListener {
                uiFixture.isExpanded = !uiFixture.isExpanded
                if (uiFixture.isExpanded) {
                    expandSeries(position)
                } else {
                    collapseSeries(position)
                }
            }

            if (uiFixture.isExpanded) {
                binding.eHeaderIcon.setImageResource(R.drawable.minus)
            } else {
                binding.eHeaderIcon.setImageResource(R.drawable.plus)
            }

        }
    }

    inner class FixturesBySeriesItemsViewHolder(val binding: CardFixtureBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: UIFixtureModel, position: Int) {
            binding.matchName.text = sectionData.fixture.subtitle
            binding.seriesName.text = sectionData.fixture.series_name
            loadImageDontTransform(
                imageUrl = sectionData.fixture.stadium_image,
                placeHolder = R.drawable.ground_ph,
                view = binding.stadiumImage
            )

            binding.stadiumName?.let {
                binding.stadiumName.text = sectionData.fixture.venue
            }

            setTeamData(
                binding.teamOne,
                sectionData.fixture.team_one_logo,
                sectionData.fixture.team_one_fname
            )
            setTeamData(
                binding.teamTwo,
                sectionData.fixture.team_two_logo,
                sectionData.fixture.team_two_fname
            )

            if (sectionData.fixture.tve_only_series) {
                binding.tveEverywhereText.text = MessageConfig.tveOnlyMessage
                binding.tveEverywhereText.visibility = View.VISIBLE
            } else {
                binding.tveEverywhereText.visibility = View.GONE
            }
            setNotificationIconImage(sectionData, binding)

            binding.notificationIcon.setOnClickListener {
                sectionData.fixture.toggleNotificationSelection()
                if (sectionData.fixture.isNotificationEnabled) {
                    LocalNotificationService.scheduleLocalNotificationAlarm(context, sectionData.fixture)
                } else {
                    LocalNotificationService.removeLocalNotificationAlarm(context, sectionData.fixture)
                }
                setNotificationIconImage(sectionData, binding)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            UIFixtureModel.TYPE_FIXTURE_HEADER -> {FixturesBySeriesHeaderViewHolder(HeaderExpandableBinding.inflate(LayoutInflater.from(parent.context), parent, false))}

            UIFixtureModel.TYPE_FIXTURE_ITEM -> { FixturesBySeriesItemsViewHolder(
                CardFixtureBinding.inflate(LayoutInflater.from(parent.context), parent, false))  }

            else -> {FixturesBySeriesHeaderViewHolder(HeaderExpandableBinding.inflate(LayoutInflater.from(parent.context), parent, false))}
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val uiFixtureData = fixturesPageModel.uiFixtures[position]
        
        when(uiFixtureData.type){
            UIFixtureModel.TYPE_FIXTURE_HEADER -> {
                if(holder is FixturesBySeriesHeaderViewHolder) {
                    holder.setData(uiFixtureData, position)
                }
            }

            UIFixtureModel.TYPE_FIXTURE_ITEM -> {
                if(holder is FixturesBySeriesItemsViewHolder) {
                    holder.setData(uiFixtureData, position)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return fixturesPageModel.uiFixtures[position].type
    }
    override fun getItemCount(): Int {
        return fixturesPageModel.uiFixtures.size
    }

    private fun setTeamData(binding: CompTeamScoreBinding, logo: String, name: String) {
        ImageUtility.loadImageInto(logo,binding.icon)
        binding.name.text = name
    }

    private fun expandSeries(position: Int){
        val row = fixturesPageModel.uiFixtures[position]
        row.isExpanded = true

        var selectedSeriesFixtures = mutableListOf<UIFixtureModel>()
        for (series in fixturesPageModel.fixtures_by_series) {
            if (series.series_id == row.fixture.series_id) {
                selectedSeriesFixtures = series.fixtures
                break
            }
        }

        var nextPosition = position
        for (uiFixture in selectedSeriesFixtures) {
            if (uiFixture.type == UIFixtureModel.TYPE_FIXTURE_ITEM) {
                fixturesPageModel.uiFixtures.add(++nextPosition, uiFixture)
            }
        }
        notifyDataSetChanged()
    }

    private fun collapseSeries(position: Int) {
        val row = fixturesPageModel.uiFixtures[position]

        var selectedSeriesFixtures = mutableListOf<UIFixtureModel>()
        for (series in fixturesPageModel.fixtures_by_series) {
            if (series.series_id == row.fixture.series_id) {
                selectedSeriesFixtures = series.fixtures
                break
            }
        }

        val startIndex = position + 1
        val endIndex = position + selectedSeriesFixtures.size
        for (i in startIndex until endIndex) {
            if (fixturesPageModel.uiFixtures[startIndex].type == UIFixtureModel.TYPE_FIXTURE_ITEM) {
                fixturesPageModel.uiFixtures.removeAt(startIndex)
            }
        }

        notifyDataSetChanged()
    }

    private fun setNotificationIconImage(sectionData: UIFixtureModel, binding: CardFixtureBinding) {
        if (sectionData.fixture.isNotificationEnabled) {
            binding.notificationIcon.setImageResource(R.drawable.notification_sel)
        } else {
            binding.notificationIcon.setImageResource(R.drawable.notification)
        }
    }
}