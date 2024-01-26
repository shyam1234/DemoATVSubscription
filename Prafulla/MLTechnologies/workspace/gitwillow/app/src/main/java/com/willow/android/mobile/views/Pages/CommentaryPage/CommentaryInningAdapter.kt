package com.willow.android.mobile.views.pages.commentaryPage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.databinding.CommentaryCellBinding
import com.willow.android.mobile.models.pages.CommOverModel
import com.willow.android.mobile.models.pages.CommentaryInningModel
class CommentaryInningAdapter(val context: Context, val commentaryInningModel: CommentaryInningModel): RecyclerView.Adapter<CommentaryInningAdapter.CommentaryInningViewHolder>() {

    inner class CommentaryInningViewHolder(val binding: CommentaryCellBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: CommOverModel) {
            var batsmanName = ""
            for (i in 0 until sectionData.batsman.size) {
                if (i != 0) {
                    batsmanName = batsmanName + ", " + sectionData.batsman[i]
                } else {
                    batsmanName = batsmanName + sectionData.batsman[i]
                }
            }
            binding.battsmansName.text = batsmanName
            binding.bowlerName.text = sectionData.bowler
            binding.overCount.text = sectionData.over_number
            binding.score.text = sectionData.score
            binding.currentOverRun.text = sectionData.runs_in_over.toString() + " runs"


            val categoryAdapter = CommentaryBallAdapter(context, sectionData, commentaryInningModel.latestVideos)
            val categoryLinearLayoutManager = GridLayoutManager(context, 6)
            categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL

            binding.overBallsRecyclerView.layoutManager = categoryLinearLayoutManager
            binding.overBallsRecyclerView.adapter = categoryAdapter
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentaryInningViewHolder {
        return CommentaryInningViewHolder(CommentaryCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CommentaryInningViewHolder, position: Int) {
        val sectionData = commentaryInningModel.data[position]
        holder.setData(sectionData)
    }

    override fun getItemCount(): Int {
        return commentaryInningModel.data.size
    }
}