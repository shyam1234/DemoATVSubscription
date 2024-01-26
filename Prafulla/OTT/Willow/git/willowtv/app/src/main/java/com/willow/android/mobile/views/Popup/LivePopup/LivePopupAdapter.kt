package com.willow.android.mobile.views.popup.livePopup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.databinding.LiveSourceCardBinding
import com.willow.android.mobile.models.pages.LiveSourceModel

class LivePopupAdapter(val liveSourcesDialog: LiveSourcesDialog, val context: Context, val sources: List<LiveSourceModel>): RecyclerView.Adapter<LivePopupAdapter.LiveSourceViewHolder>() {

    inner class LiveSourceViewHolder(val binding: LiveSourceCardBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(liveSourceModel: LiveSourceModel) {
            binding.othersPageCardTitle.text = liveSourceModel.title

            itemView.setOnClickListener(View.OnClickListener {
                liveSourcesDialog.onLiveSourceSelected(liveSourceModel)
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveSourceViewHolder {
        return LiveSourceViewHolder(LiveSourceCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: LiveSourceViewHolder, position: Int) {
        val sectionData = sources[position]
        holder.setData(sectionData)
    }

    override fun getItemCount(): Int {
        return sources.size
    }
}