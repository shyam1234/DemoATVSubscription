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
import com.willow.android.mobile.models.video.SuggestedVideosModel
import com.willow.android.mobile.models.video.VideoModel
import com.willow.android.mobile.utils.Utils
import com.willow.android.mobile.views.pages.PagesNavigator
import com.willow.android.tv.utils.ImageUtility.loadImageWithCallback

interface VideoDetailPageInterface {
    fun changeSelectedVideo(videoModel: VideoModel)
}

class VideoDetailPageAdapter(val context: Context, val selectedVideoModel: VideoModel, val suggestedVideosModel: SuggestedVideosModel, val videoDetailPageInterface: VideoDetailPageInterface): RecyclerView.Adapter<VideoDetailPageAdapter.CardVideoVerticalViewHolder>() {
    var allCardsBindings: MutableList<CardVideoVerticalBinding> = mutableListOf()

    inner class CardVideoVerticalViewHolder(val binding: CardVideoVerticalBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(data: VideoModel) {
            allCardsBindings.add(binding)

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

            binding.icShare.setOnClickListener { PagesNavigator.launchSharePopup(context, data) }
            loadImageWithCallback(context,data.imageUrl,200,50,object: CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                    Log.d("Image", "onLoadCleared")
                }
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    binding.videoWrapper.background = resource
                }
            })

            if (data.contentId.equals(selectedVideoModel.contentId, true)) {
                highlightSelectedCard(binding)
            }

            binding.root.setOnClickListener {
                videoDetailPageInterface.changeSelectedVideo(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardVideoVerticalViewHolder {
        return CardVideoVerticalViewHolder(CardVideoVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CardVideoVerticalViewHolder, position: Int) {
        val videoData = suggestedVideosModel.list[position]
        holder.setData(videoData)
    }

    override fun getItemCount(): Int {
        return suggestedVideosModel.list.size
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