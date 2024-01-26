package com.willow.android.tv.ui.pointtable.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.willow.android.R
import com.willow.android.databinding.RowPointTableItemBinding
import com.willow.android.databinding.RowPointTableItemHeaderBinding
import com.willow.android.tv.data.repositories.pointtable.datamodel.Group

class PointTableAdapter(private val group: Group) : RecyclerView.Adapter<ViewHolder>() {


    inner class PointTableChildVH(view: View) : ViewHolder(view) {
        val binding: RowPointTableItemBinding? = DataBindingUtil.bind(view)

    }

    inner class PointTableHeaderVH(view: View) : ViewHolder(view) {
        val binding: RowPointTableItemHeaderBinding? = DataBindingUtil.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == HEADER_ITEM)
            PointTableHeaderVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_point_table_item_header, parent, false)
            )
        else
            PointTableChildVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_point_table_item, parent, false)
            )

    }

    override fun getItemCount(): Int {
        return group.team_standings.size + CHILD_ITEM
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is PointTableChildVH) {
            val model = group.team_standings[position - 1]
            holder.binding?.data = model
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == HEADER_ITEM)
            HEADER_ITEM
        else
            CHILD_ITEM
    }


    companion object {
        const val HEADER_ITEM = 0
        const val CHILD_ITEM = 1
    }
}