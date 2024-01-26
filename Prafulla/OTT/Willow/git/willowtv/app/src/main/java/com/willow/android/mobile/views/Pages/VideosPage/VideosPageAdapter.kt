package com.willow.android.mobile.views.pages.videosPage

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
import com.willow.android.mobile.models.video.VideosSectionModel
import tv.willow.Models.VideosPageModel

class VideosPageAdapter(val context: Context, val videosPageModel: VideosPageModel): RecyclerView.Adapter<VideosPageAdapter.VideosPageViewHolder>() {

    inner class VideosPageViewHolder(val binding: SectionVideosBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: VideosSectionModel) {
            val isDeviceTablet: Boolean = context.resources.getBoolean(R.bool.isTablet)
            if (sectionData.isVerticalScroll && isDeviceTablet) {
                setTabletSectionData(binding, sectionData)
            } else {
                setSectionData(binding, sectionData)
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
        val sectionData = videosPageModel.result.video_data[position]
        holder.setData(sectionData)
    }

    override fun getItemCount(): Int {
        return videosPageModel.result.video_data.size
    }

    private fun setSectionData(binding: SectionVideosBinding, sectionData: VideosSectionModel) {
        val categoryAdapter = VideosSectionAdapter(context, videosSectionModel = sectionData)
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
        if (sectionData.title.isNotEmpty()) {
            binding.header.root.visibility = View.VISIBLE
            binding.header.sectionHeaderTitle.text = sectionData.title
        }
        binding.recyclerView.layoutManager = categoryLinearLayoutManager
        binding.recyclerView.adapter = categoryAdapter
    }

    private fun setTabletSectionData(binding: SectionVideosBinding, sectionData: VideosSectionModel) {
        val categoryAdapter = GroupedVideosSectionAdapter(context, sectionData, null)
        val categoryLinearLayoutManager = LinearLayoutManager(context)
        categoryLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        if (sectionData.title.isNotEmpty()) {
            binding.header.root.visibility = View.VISIBLE
            binding.header.sectionHeaderTitle.text = sectionData.title
        }
        binding.recyclerView.layoutManager = categoryLinearLayoutManager
        binding.recyclerView.adapter = categoryAdapter
    }
}