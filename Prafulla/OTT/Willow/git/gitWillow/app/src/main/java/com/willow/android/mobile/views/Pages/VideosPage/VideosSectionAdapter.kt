package com.willow.android.mobile.views.pages.videosPage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.databinding.CardVideoBigBinding
import com.willow.android.databinding.CardVideoSmallBinding
import com.willow.android.databinding.CardVideoVerticalBinding
import com.willow.android.mobile.models.video.SuggestedVideosModel
import com.willow.android.mobile.models.video.VideoModel
import com.willow.android.mobile.models.video.VideosSectionModel
import com.willow.android.mobile.utils.Utils
import com.willow.android.mobile.views.pages.PagesNavigator
import com.willow.android.tv.utils.ImageUtility


class VideosSectionAdapter (val context: Context, val videosSectionModel: VideosSectionModel): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val BIG_CARD_VIEW = 1
    val SMALL_CARD_VIEW = 3
    val VERTICAL_CARD_VIEW = 4

    inner class CardVideoSmallViewHolder(val binding: CardVideoSmallBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(data: VideoModel, videosList: List<VideoModel>) {
            binding.cardVideoSmallTitle.text = data.title

            if (data.duration.isNotEmpty()) {
                binding.tvVideoDuration.text = data.duration
                binding.icVideoPlay.visibility = View.INVISIBLE
            } else {
                binding.tvVideoDuration.visibility = View.INVISIBLE
                binding.icVideoDurationBg.visibility = View.INVISIBLE
                binding.icVideoPlay.visibility = View.VISIBLE
            }

            if (Utils.shouldShowPremiumIcon(data.needSubscription)) {
                binding.icPremium.visibility = View.VISIBLE
            } else {
                binding.icPremium.visibility = View.GONE
            }

            ImageUtility.loadImageInto(data.imageUrl,binding.backgroundImageView)

            binding.icShare.setOnClickListener { PagesNavigator.launchSharePopup(context, data) }

            val suggestedVideos = SuggestedVideosModel()
            suggestedVideos.setData(videosList)
            binding.videoWrapper.setOnClickListener{
                PagesNavigator.chooseAuthPlayController(context, data, suggestedVideos, )
            }
        }
    }

    inner class CardVideoBigViewHolder(val binding: CardVideoBigBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(data: VideoModel, videosList: List<VideoModel>) {
            binding.cardVideoBigTitle.text = data.title

            if (data.duration.isNotEmpty()) {
                binding.tvVideoDuration.text = data.duration
                binding.icVideoPlay.visibility = View.INVISIBLE
            } else {
                binding.tvVideoDuration.visibility = View.INVISIBLE
                binding.icVideoDurationBg.visibility = View.INVISIBLE
                binding.icVideoPlay.visibility = View.VISIBLE
            }

            if (Utils.shouldShowPremiumIcon(data.needSubscription)) {
                binding.icPremium.visibility = View.VISIBLE
            } else {
                binding.icPremium.visibility = View.GONE
            }

            ImageUtility.loadImageInto(data.imageUrl,binding.backgroundImageView)

            binding.icShare.setOnClickListener { PagesNavigator.launchSharePopup(context, data) }

            val suggestedVideos = SuggestedVideosModel()
            suggestedVideos.setData(videosList)
            binding.videoWrapper.setOnClickListener{ PagesNavigator.chooseAuthPlayController(context, data, suggestedVideos) }
        }
    }

    inner class CardVideoVerticalViewHolder(val binding: CardVideoVerticalBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(data: VideoModel, videosList: List<VideoModel>) {
            binding.cardVideoVerticalTitle.text = data.title
            if (data.matchTitle.isNotEmpty()) {
                binding.cardVideoMatchName.visibility = View.VISIBLE
                binding.cardVideoMatchName.text = data.matchTitle
            } else {
                binding.cardVideoMatchName.visibility = View.GONE
            }

            if (data.duration.isNotEmpty()) {
                binding.tvVideoDuration.text = data.duration
                binding.icVideoPlay.visibility = View.INVISIBLE
            } else {
                binding.tvVideoDuration.visibility = View.INVISIBLE
                binding.icVideoDurationBg.visibility = View.INVISIBLE
                binding.icVideoPlay.visibility = View.VISIBLE
            }

            if (Utils.shouldShowPremiumIcon(data.needSubscription)) {
                binding.icPremium.visibility = View.VISIBLE
            } else {
                binding.icPremium.visibility = View.GONE
            }

            ImageUtility.loadImageInto(data.imageUrl,binding.backgroundImageView)

            val suggestedVideos = SuggestedVideosModel()
            suggestedVideos.setData(videosList)
            binding.root.setOnClickListener{ PagesNavigator.chooseAuthPlayController(context, data, suggestedVideos) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        if (viewType == BIG_CARD_VIEW) {
            return CardVideoBigViewHolder(CardVideoBigBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        if (viewType == SMALL_CARD_VIEW) {
            return CardVideoSmallViewHolder(CardVideoSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        if (viewType == VERTICAL_CARD_VIEW) {
            return CardVideoVerticalViewHolder(CardVideoVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        return CardVideoSmallViewHolder(CardVideoSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val videoData = videosSectionModel.videos[position]

        if (holder is CardVideoBigViewHolder) {
            holder.setData(videoData, videosSectionModel.videos)
        }

        if (holder is CardVideoSmallViewHolder) {
            holder.setData(videoData, videosSectionModel.videos)
        }

        if (holder is CardVideoVerticalViewHolder) {
            holder.setData(videoData, videosSectionModel.videos)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val view_type = videosSectionModel.mobile_view

        when (view_type) {
            "highlights_row" -> return BIG_CARD_VIEW
            "horizontal_max_grid" -> return BIG_CARD_VIEW
            "horizontal_min_grid" -> return SMALL_CARD_VIEW
            "vertical_grid" -> return VERTICAL_CARD_VIEW
        }

        return SMALL_CARD_VIEW
    }

    override fun getItemCount(): Int {
        return videosSectionModel.videos.size
    }
}