package com.willow.android.mobile.views.pages.othersPage

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.databinding.OthersPageCardBinding
import com.willow.android.mobile.views.pages.WebViewPageActivity
import tv.willow.Models.SettingsItemModel
import tv.willow.Models.SettingsSubItem

class OthersPageAdapter(val context: Context, val settingsItemModel: SettingsItemModel): RecyclerView.Adapter<OthersPageAdapter.OthersViewHolder>() {

    inner class OthersViewHolder(val binding: OthersPageCardBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(settingsSubItem: SettingsSubItem) {
            binding.othersPageCardTitle.text = settingsSubItem.title

            itemView.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, WebViewPageActivity::class.java).apply {}
                intent.putExtra("PAGE_TITLE", settingsSubItem.title)
                intent.putExtra("url", settingsSubItem.url)
                context.startActivity(intent)
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OthersViewHolder {
        return OthersViewHolder(OthersPageCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: OthersViewHolder, position: Int) {
        val sectionData = settingsItemModel.subitems[position]
        holder.setData(sectionData)
    }

    override fun getItemCount(): Int {
        return settingsItemModel.subitems.size
    }
}