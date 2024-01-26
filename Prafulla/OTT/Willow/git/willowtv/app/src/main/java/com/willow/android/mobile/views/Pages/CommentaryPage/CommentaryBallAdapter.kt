package com.willow.android.mobile.views.pages.commentaryPage

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.willow.android.R
import com.willow.android.databinding.CommentaryBallBinding
import com.willow.android.mobile.models.pages.BallByBall
import com.willow.android.mobile.models.pages.CommOverModel
import com.willow.android.mobile.models.video.SuggestedVideosModel
import com.willow.android.mobile.models.video.VideoModel
import com.willow.android.mobile.views.pages.PagesNavigator


class CommentaryBallAdapter(val context: Context, val commentaryBallModel: CommOverModel, val latestVideos: MutableList<VideoModel>): RecyclerView.Adapter<CommentaryBallAdapter.CommentaryBallHolder>() {
    inner class CommentaryBallHolder(val binding: CommentaryBallBinding): RecyclerView.ViewHolder(binding.root) {

        fun setData(ballByBall: BallByBall) {
            binding.commentaryBallTitle.text = ballByBall.display
            binding.commentaryBallTitle.setTextColor(ballByBall.text_color)
            if (ballByBall.background_color == Color.WHITE) {
                binding.commentaryBallTitle.setBackgroundResource(R.drawable.white_ball_background)
            } else {
                binding.commentaryBallTitle.background.setTint(ballByBall.background_color)
            }
//            binding.commentaryBallTitle.background.setColorFilter(ballByBall.background_color, PorterDuff.Mode.SRC_ATOP)

            if (ballByBall.has_video) {
                binding.commentaryBallTitle.setOnClickListener {
                    val suggestedVideos = SuggestedVideosModel()
                    suggestedVideos.setData(latestVideos)
                    PagesNavigator.launchVideoDetailPage(context, ballByBall.videoModel, suggestedVideos, true)
                }
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentaryBallHolder {
        return CommentaryBallHolder(CommentaryBallBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CommentaryBallHolder, position: Int) {
        val sectionData = commentaryBallModel.ball_by_ball[position]
        holder.setData(sectionData)
    }

    override fun getItemCount(): Int {
        return commentaryBallModel.ball_by_ball.size
    }

}