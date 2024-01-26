package com.willow.android.mobile.views.pages.homePage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.databinding.CardLiveBinding
import com.willow.android.mobile.MainActivity
import com.willow.android.mobile.models.pages.LiveModel
import com.willow.android.mobile.models.video.VideoModel
import com.willow.android.mobile.utils.Utils
import com.willow.android.mobile.views.pages.PagesNavigator
import com.willow.android.tv.utils.ImageUtility


class LiveSectionAdapter(val context: Context, val liveModels: MutableList<LiveModel>): RecyclerView.Adapter<LiveSectionAdapter.LiveSectionViewHolder>() {

    inner class LiveSectionViewHolder(val binding: CardLiveBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(liveModel: LiveModel) {
            if (Utils.shouldShowPremiumIcon(liveModel.needSubscription)) {
                binding.icPremium.visibility = View.VISIBLE
            } else {
                binding.icPremium.visibility = View.GONE
            }

            ImageUtility.loadImageInto(liveModel.imageUrl,binding.backgroundImageView)

            val videoModel = VideoModel()
            videoModel.setLiveData(liveModel)
            binding.icShare.setOnClickListener { PagesNavigator.launchSharePopup(context, videoModel) }

            binding.videoWrapper.setOnClickListener {
                if (context is MainActivity) {
                    context.launchMatchCenterActivity(true, liveModel.matchId, liveModel.seriesId, liveModel.sources)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveSectionViewHolder {
        return LiveSectionViewHolder(CardLiveBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: LiveSectionViewHolder, position: Int) {
        val sectionData = liveModels[position]
        holder.setData(sectionData)
    }

    override fun getItemCount(): Int {
        return liveModels.size
    }
}