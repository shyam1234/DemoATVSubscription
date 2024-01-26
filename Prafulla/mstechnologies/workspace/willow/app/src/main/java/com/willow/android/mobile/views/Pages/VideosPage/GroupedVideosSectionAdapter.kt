package com.willow.android.mobile.views.pages.videosPage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.databinding.CardVideoVerticalBinding
import com.willow.android.databinding.CardVideoVerticalTabletBinding
import com.willow.android.mobile.models.video.SuggestedVideosModel
import com.willow.android.mobile.models.video.VideoModel
import com.willow.android.mobile.models.video.VideosSectionModel
import com.willow.android.mobile.utils.Utils
import com.willow.android.mobile.views.pages.PagesNavigator
import com.willow.android.mobile.views.pages.matchCenterPage.MatchCenterPageFragment
import com.willow.android.tv.utils.ImageUtility


class GroupedVideosSectionAdapter(val context: Context, val videosSectionModel: VideosSectionModel, val matchCenterPageFragment: MatchCenterPageFragment?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class CardVideoVerticalTabletViewHolder(val binding: CardVideoVerticalTabletBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(data: MutableList<VideoModel>, videosList: List<VideoModel>) {
            for (i in 0 until data.size) {
                when (i) {
                    0 -> {
                        binding.firstCard.root.visibility = View.VISIBLE
                        setCardData(binding.firstCard, data[i], videosList)
                    }
                    1 -> {
                        binding.secondCard.root.visibility = View.VISIBLE
                        setCardData(binding.secondCard, data[i], videosList)
                    }
                    2 -> {
                        binding.thirdCard.root.visibility = View.VISIBLE
                        setCardData(binding.thirdCard, data[i], videosList)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return CardVideoVerticalTabletViewHolder(CardVideoVerticalTabletBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val videoData = videosSectionModel.groupedVideos[position]

        if (holder is CardVideoVerticalTabletViewHolder) {
            holder.setData(videoData, videosSectionModel.videos)
        }
    }

    override fun getItemCount(): Int {
        return videosSectionModel.groupedVideos.size
    }

    private fun setCardData(cardBinding: CardVideoVerticalBinding, data: VideoModel, videosList: List<VideoModel>) {
        cardBinding.cardVideoVerticalTitle.text = data.title
        if (data.matchTitle.isNotEmpty()) {
            cardBinding.cardVideoMatchName.visibility = View.VISIBLE
            cardBinding.cardVideoMatchName.text = data.matchTitle
        } else {
            cardBinding.cardVideoMatchName.visibility = View.GONE
        }

        if (data.duration.isNotEmpty()) {
            cardBinding.tvVideoDuration.text = data.duration
            cardBinding.icVideoPlay.visibility = View.INVISIBLE
        } else {
            cardBinding.tvVideoDuration.visibility = View.INVISIBLE
            cardBinding.icVideoDurationBg.visibility = View.INVISIBLE
            cardBinding.icVideoPlay.visibility = View.VISIBLE
        }

        if (Utils.shouldShowPremiumIcon(data.needSubscription)) {
            cardBinding.icPremium.visibility = View.VISIBLE
        } else {
            cardBinding.icPremium.visibility = View.GONE
        }

        ImageUtility.loadImageInto(data.imageUrl,cardBinding.backgroundImageView)

        cardBinding.icShare.setOnClickListener { PagesNavigator.launchSharePopup(context, data) }

        cardBinding.root.setOnClickListener{
            if (matchCenterPageFragment != null) {
                matchCenterPageFragment.chooseAuthPlayController(data)
            } else {
                val suggestedVideos = SuggestedVideosModel()
                suggestedVideos.setData(videosList)
                PagesNavigator.chooseAuthPlayController(context, data, suggestedVideos)
            }
        }
    }
}