package com.willow.android.mobile.views.pages.videoDetailPage

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.willow.android.R
import com.willow.android.databinding.CardVideoVerticalBinding
import com.willow.android.databinding.CardVideoVerticalTabletBinding
import com.willow.android.mobile.models.video.SuggestedVideosModel
import com.willow.android.mobile.models.video.VideoModel
import com.willow.android.mobile.utils.Utils
import com.willow.android.mobile.views.pages.PagesNavigator
import com.willow.android.tv.utils.ImageUtility


class VideoDetailPageTabletAdapter(val context: Context, val selectedVideoModel: VideoModel, val suggestedVideosModel: SuggestedVideosModel, val videoDetailPageInterface: VideoDetailPageInterface): RecyclerView.Adapter<VideoDetailPageTabletAdapter.CardVideoVerticalTabletViewHolder>()  {
    var allCardsBindings: MutableList<CardVideoVerticalBinding> = mutableListOf()

    inner class CardVideoVerticalTabletViewHolder(val binding: CardVideoVerticalTabletBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(data: MutableList<VideoModel>) {
            binding.firstCard.root.visibility = View.INVISIBLE
            binding.secondCard.root.visibility = View.INVISIBLE
            binding.thirdCard.root.visibility = View.INVISIBLE
            deHighlightBinding(binding.firstCard)
            deHighlightBinding(binding.secondCard)
            deHighlightBinding(binding.thirdCard)

            for (i in 0 until data.size) {
                when (i) {
                    0 -> {
                        binding.firstCard.root.visibility = View.VISIBLE
                        setCardData(binding.firstCard, data[i])
                    }
                    1 -> {
                        binding.secondCard.root.visibility = View.VISIBLE
                        setCardData(binding.secondCard, data[i])
                    }
                    2 -> {
                        binding.thirdCard.root.visibility = View.VISIBLE
                        setCardData(binding.thirdCard, data[i])
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardVideoVerticalTabletViewHolder {
        return CardVideoVerticalTabletViewHolder(CardVideoVerticalTabletBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CardVideoVerticalTabletViewHolder, position: Int) {
        val videoData = suggestedVideosModel.groupedVideos[position]
        holder.setData(videoData)
    }

    override fun getItemCount(): Int {
        return suggestedVideosModel.groupedVideos.size
    }

    private fun setCardData(cardBinding: CardVideoVerticalBinding, data: VideoModel) {
        allCardsBindings.add(cardBinding)

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
        ImageUtility.loadImageWithCallback(
            context,
            data.imageUrl,
            180, 101,
            object: CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                    Log.d("Image", "onLoadCleared")
                }
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    cardBinding.videoWrapper.background = resource
                }
            })

        if (data.contentId.equals(selectedVideoModel.contentId, true)) {
            highlightSelectedCard(cardBinding)
        }

        cardBinding.icShare.setOnClickListener { PagesNavigator.launchSharePopup(context, data) }

        cardBinding.root.setOnClickListener {
            videoDetailPageInterface.changeSelectedVideo(data)
        }
    }

    private fun highlightSelectedCard(binding: CardVideoVerticalBinding) {
        deselectAllCards()
        highlightBinding(binding)
    }

    private fun deselectAllCards() {
        for (cardBinding in allCardsBindings) {
            deHighlightBinding(cardBinding)
        }
    }

    fun highlightSelectedVideo(videoModel: VideoModel) {
        deselectAllCards()
        for (i in 0 until allCardsBindings.size) {
            if (allCardsBindings[i].cardVideoVerticalTitle.text.equals(videoModel.title)) {
                highlightBinding(allCardsBindings[i])
            }
        }
    }

    fun highlightNextVideo() {
        var selectedIndex = 0
        for (i in 0 until allCardsBindings.size) {
            if (allCardsBindings[i].selectionStatus.isSelected) {
                selectedIndex = allCardsBindings.indexOf(allCardsBindings[i])
                if (selectedIndex < (allCardsBindings.size - 1)) {
                    deHighlightBinding(allCardsBindings[selectedIndex])
                    selectedIndex += 1
                    highlightBinding(allCardsBindings[selectedIndex])
                    break
                }
            }
        }
    }

    private fun highlightBinding(binding: CardVideoVerticalBinding) {
        binding.root.setBackgroundResource(R.color.video_sel_background)
        binding.selectionStatus.isSelected = true
    }

    private fun deHighlightBinding(binding: CardVideoVerticalBinding) {
        binding.root.setBackgroundColor(Color.WHITE)
        binding.selectionStatus.isSelected = false
    }
}