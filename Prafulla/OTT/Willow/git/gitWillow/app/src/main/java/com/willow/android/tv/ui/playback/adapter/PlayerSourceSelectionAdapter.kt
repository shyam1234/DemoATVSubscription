package com.willow.android.tv.ui.playback.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.tv.data.repositories.commondatamodel.ContentDetail

class PlayerSourceSelectionAdapter(private val listOfSourceURL: List<ContentDetail>?, private val listener :View.OnClickListener) : RecyclerView.Adapter<PlayerSourceSelectionAdapter.ViewHolder>()    {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_player_source_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name?.tag = position
        holder.name?.text = listOfSourceURL?.get(position)?.name
        holder.name?.setOnClickListener(listener)
    }

    override fun getItemCount(): Int {
        return listOfSourceURL?.size ?:0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView?
        init {
            name = view.findViewById(R.id.textview_source_name)

        }
    }
}