package com.willow.android.mobile.views.pages.homePage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.databinding.SectionEmptyBinding
import com.willow.android.databinding.SectionFixtureByDateBinding
import com.willow.android.databinding.SectionResultByDateBinding
import com.willow.android.databinding.SectionVideosBinding
import com.willow.android.mobile.MainTabsSelectionInterface
import com.willow.android.mobile.models.pages.FixtureModel
import com.willow.android.mobile.models.pages.HomePageModel
import com.willow.android.mobile.models.pages.HomePageSectionModel
import com.willow.android.mobile.models.pages.LiveModel
import com.willow.android.mobile.models.pages.ResultModel
import com.willow.android.mobile.models.video.VideoModel
import com.willow.android.mobile.models.video.VideosSectionModel
import com.willow.android.mobile.views.pages.videosPage.VideosSectionAdapter

class HomePageAdapter(val fragment: HomePageFragment, val context: Context, val homePageModel: HomePageModel, val mainTabsSelectionInterface: MainTabsSelectionInterface): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val LIVE_VIEW = 0
    val HIGHLIGHTS_VIEW = 1
    val RESULTS_VIEW = 2
    val FIXTURES_VIEW = 3
    val DEFAULT_VIEW = 4

    val EMPTY_SECTION_VIEW = 5

    inner class LiveSectionViewHolder(val binding: SectionVideosBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: HomePageSectionModel) {
            if (sectionData.content.size > 0 ) {
                val finalData = sectionData.content as MutableList<LiveModel>
                createLiveSection(binding = binding, sectionData = finalData, "", viewType = sectionData.view_type)
            }
        }
    }

    inner class HighlightsSectionViewHolder(val binding: SectionVideosBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: HomePageSectionModel) {
            if (sectionData.content.size > 0 ) {
                val finalData = sectionData.content as MutableList<VideoModel>
                createHighlightsSection(binding = binding, sectionData = finalData, sectionTitle = sectionData.title, viewType = sectionData.view_type)
            }
        }
    }

    inner class FixturesSectionViewHolder(val binding: SectionFixtureByDateBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: HomePageSectionModel) {
            if (sectionData.content.size > 0 ) {
                val finalData = sectionData.content as MutableList<FixtureModel>
                createFixturesSection(binding = binding, sectionData = finalData, sectionTitle = sectionData.title, viewType = sectionData.view_type)
            }
        }
    }

    inner class ResultsSectionViewHolder(val binding: SectionResultByDateBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: HomePageSectionModel) {
            if (sectionData.content.size > 0 ) {
                val finalData = sectionData.content as MutableList<ResultModel>
                createResultsSection(binding = binding, sectionData = finalData, sectionTitle = sectionData.title, viewType = sectionData.view_type)
            }
        }
    }

    inner class EmptySectionViewHolder(val binding: SectionEmptyBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            LIVE_VIEW -> return LiveSectionViewHolder(SectionVideosBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            HIGHLIGHTS_VIEW -> return HighlightsSectionViewHolder(SectionVideosBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            RESULTS_VIEW -> return ResultsSectionViewHolder(SectionResultByDateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            FIXTURES_VIEW -> return FixturesSectionViewHolder(SectionFixtureByDateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            EMPTY_SECTION_VIEW -> return EmptySectionViewHolder(SectionEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        return HighlightsSectionViewHolder(SectionVideosBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position > (homePageModel.result.video_data.size - 1)) { return }

        val sectionData = homePageModel.result.video_data[position]
        if (holder is LiveSectionViewHolder) {
            holder.setData(sectionData)
        } else if (holder is HighlightsSectionViewHolder) {
            holder.setData(sectionData)
        } else if (holder is ResultsSectionViewHolder) {
            holder.setData(sectionData)
        } else if (holder is FixturesSectionViewHolder) {
            holder.setData(sectionData)
        }
    }

    override fun getItemCount(): Int {
        return homePageModel.result.video_data.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        if (position > (homePageModel.result.video_data.size - 1)) {
            return EMPTY_SECTION_VIEW
        }

        val view_type = homePageModel.result.video_data[position].view_type
        if (view_type == null || view_type !is String) {
            return DEFAULT_VIEW
        }

        when (view_type) {
            "live_row" -> return LIVE_VIEW
            "highlights_row" -> return HIGHLIGHTS_VIEW
            "results_row" -> return RESULTS_VIEW
            "fixtures_row" -> return FIXTURES_VIEW
        }

        return DEFAULT_VIEW
    }

    private fun createLiveSection(binding: SectionVideosBinding, sectionData: MutableList<LiveModel>, sectionTitle: String, viewType: String) {
        if (sectionData.size > 0) {
            val categoryAdapter = LiveSectionAdapter(context, sectionData)
            val categoryLinearLayoutManager = LinearLayoutManager(context)
            categoryLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

            binding.header.root.visibility = View.INVISIBLE
            binding.headerImage.root.visibility = View.VISIBLE
            binding.recyclerView.layoutManager = categoryLinearLayoutManager
            binding.recyclerView.adapter = categoryAdapter
        } else {
            binding.header.root.visibility = View.GONE
        }
    }

    private fun createHighlightsSection(binding: SectionVideosBinding, sectionData: MutableList<VideoModel>, sectionTitle: String, viewType: String) {
        if (sectionData.size > 0) {
            val sectionModel = VideosSectionModel()
            sectionModel.setBaseData(homePageModel.result.imageBaseUrl, homePageModel.result.videoBaseUrl, homePageModel.result.slugBaseUrl, homePageModel.result.slugDict, homePageModel.result.clipUrlDict, homePageModel.result.clipExt, viewType)
            sectionModel.setVideoModels(sectionData)

            val categoryAdapter = VideosSectionAdapter(context, videosSectionModel = sectionModel)
            val categoryLinearLayoutManager = LinearLayoutManager(context)
            categoryLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            if (sectionTitle.isNotEmpty()) {
                binding.header.root.visibility = View.VISIBLE
                binding.header.sectionHeaderTitle.text = sectionTitle
                binding.header.arrowIcon.visibility = View.VISIBLE
            }
            binding.recyclerView.layoutManager = categoryLinearLayoutManager
            binding.recyclerView.adapter = categoryAdapter
            binding.header.root.setOnClickListener {
                mainTabsSelectionInterface.setCurrentFragment(R.id.navigation_videos_page)
            }
        } else {
            binding.header.root.visibility = View.GONE
        }
    }

    private fun createResultsSection(binding: SectionResultByDateBinding, sectionData: MutableList<ResultModel>, sectionTitle: String, viewType: String) {
        if (sectionData.size > 0) {
//            val resultsList: MutableList<ResultModel> = mutableListOf()
//            for (sectionUnit in sectionData) {
//                val jsonObject: JSONObject = Gson().toJsonTree(sectionUnit).getAsJsonObject()
//                val resultModel = ResultModel()
//                resultModel.setData(homePageModel.result.imageBaseUrl, jsonObject)
//                if (Utils.isAllowedInCountry(resultModel.CC)) {
//                    resultModel.completeImageUrl(homePageModel.result.imageBaseUrl, homePageModel.result.videoBaseUrl, homePageModel.result.slugBaseUrl)
//                    resultsList.add(resultModel)
//                }
//            }

            val categoryAdapter = ResultsSectionAdapter(context, sectionData)
            val categoryLinearLayoutManager = LinearLayoutManager(context)
            categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            if (sectionTitle.isNotEmpty()) {
                binding.header.root.visibility = View.VISIBLE
                binding.header.sectionHeaderTitle.text = sectionTitle
                binding.header.arrowIcon.visibility = View.VISIBLE
                binding.header.root.setOnClickListener {
                    mainTabsSelectionInterface.setCurrentFragment(R.id.navigation_results_page)
                }
            }
            binding.sectionRecyclerView.layoutManager = categoryLinearLayoutManager
            binding.sectionRecyclerView.adapter = categoryAdapter
        } else {
            binding.header.root.visibility = View.GONE
        }
    }

    private fun createFixturesSection(binding: SectionFixtureByDateBinding, sectionData: MutableList<FixtureModel>, sectionTitle: String, viewType: String) {
        if (sectionData.size > 0) {
//            val fixturesList: MutableList<FixtureModel> = mutableListOf()
//            for (sectionUnit in sectionData) {
//                val jsonObject: JsonObject = Gson().toJsonTree(sectionUnit).getAsJsonObject()
//                val fixtureModel = Gson().fromJson(jsonObject, FixtureModel::class.java)
//                if (Utils.isAllowedInCountry(fixtureModel.CC)) {
//                    fixturesList.add(fixtureModel)
//                }
//            }

            val categoryAdapter = HomeFixturesSectionAdapter (context, sectionData)
            val categoryLinearLayoutManager = LinearLayoutManager(context)

            val isDeviceTablet: Boolean = context.resources.getBoolean(R.bool.isTablet)
            if (isDeviceTablet) {
                categoryLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            } else {
                categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            }

            if (sectionTitle.isNotEmpty()) {
                binding.header.root.visibility = View.VISIBLE
                binding.header.sectionHeaderTitle.text = sectionTitle
                binding.header.arrowIcon.visibility = View.VISIBLE
                binding.header.root.setOnClickListener {
                    mainTabsSelectionInterface.setCurrentFragment(R.id.navigation_fixtures_Page)
                }
            }
            binding.sectionRecyclerView.layoutManager = categoryLinearLayoutManager
            binding.sectionRecyclerView.adapter = categoryAdapter
        } else {
            binding.header.root.visibility = View.GONE
        }
    }
}