package com.willow.android.mobile.views.pages.matchCenterPage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.databinding.SectionVideosBinding
import com.willow.android.mobile.models.pages.MatchCenterPageModel
import com.willow.android.mobile.models.video.VideosSectionModel
import com.willow.android.mobile.views.pages.videosPage.GroupedVideosSectionAdapter
import tv.willow.Views.Pages.VideosPage.MatchCenterVideosAdapter

class MatchCenterPageAdapter(val context: Context, val matchCenterPageModel: MatchCenterPageModel, val matchCenterPageFragment: MatchCenterPageFragment): RecyclerView.Adapter<MatchCenterPageAdapter.VideosPageViewHolder>() {
    inner class VideosPageViewHolder(val binding: SectionVideosBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: VideosSectionModel, matchCenterPageFragment: MatchCenterPageFragment) {
            if (sectionData.title.isNotEmpty()) {
                binding.header.root.visibility = View.VISIBLE
                binding.header.sectionHeaderTitle.text = sectionData.title
            }
            val isDeviceTablet: Boolean = context.resources.getBoolean(R.bool.isTablet)
            if (isDeviceTablet && (sectionData.mobile_view == "vertical_grid")) {

                val categoryAdapter = GroupedVideosSectionAdapter(context, sectionData, matchCenterPageFragment)
                val categoryLinearLayoutManager = LinearLayoutManager(context)
                categoryLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                binding.recyclerView.layoutManager = categoryLinearLayoutManager
                binding.recyclerView.adapter = categoryAdapter

            } else {

                val categoryAdapter = MatchCenterVideosAdapter(context, sectionData, matchCenterPageFragment)
                val categoryLinearLayoutManager = LinearLayoutManager(context)
                if (sectionData.isVerticalScroll) {
                    categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL

                    // Add divider decorator
                    val itemDecor = DividerItemDecoration(context, RecyclerView.VERTICAL)
                    val dividerDrawable = this?.let { it1 -> ContextCompat.getDrawable(context, R.drawable.vertical_divider) }
                    if (dividerDrawable != null) {
                        itemDecor.setDrawable(dividerDrawable)
                    }
                    binding.recyclerView.addItemDecoration(itemDecor)

                } else {
                    categoryLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                }
                binding.recyclerView.layoutManager = categoryLinearLayoutManager
                binding.recyclerView.adapter = categoryAdapter
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideosPageViewHolder {
        return VideosPageViewHolder(SectionVideosBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VideosPageViewHolder, position: Int) {
        val sectionData = matchCenterPageModel.result.videoSections[position]
        holder.setData(sectionData, matchCenterPageFragment)
    }

    override fun getItemCount(): Int {
        return matchCenterPageModel.result.videoSections.size
    }
}