package com.willow.android.tv.ui.pointtable.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.databinding.RowDynamicPointGroupItemBinding
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.data.repositories.pointtable.datamodel.Group
import com.willow.android.tv.utils.hide
import com.willow.android.tv.utils.show

class TabGroupAdapter(
    private val groupList: List<Group>,
    private val listener: ItemClickListener,
    private val keyListener: KeyListener
) : RecyclerView.Adapter<TabGroupAdapter.TabGroupVH>() {

    private lateinit var context: Context
    private var selectedButton: AppCompatButton? = null
    private var lastSelectedPosition = 0

    class TabGroupVH(view: View) : RecyclerView.ViewHolder(view) {
        val binding: RowDynamicPointGroupItemBinding? = DataBindingUtil.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabGroupVH {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_dynamic_point_group_item, parent, false)
        return TabGroupVH(view)
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TabGroupVH, @SuppressLint("RecyclerView") position: Int) {
        if(groupList[position].name == null){
            holder.binding?.btnGroupItem?.hide()
        }else{
            holder.binding?.btnGroupItem?.show()
        }
        holder.binding?.btnGroupItem?.text =
            "${context.getString(R.string.group)} ${groupList[position].name}"

        if (holder.absoluteAdapterPosition == 0) {
            setSelectedButton(holder.binding?.btnGroupItem)
            listener.itemClicked(0)
        }

        holder.binding?.btnGroupItem?.setOnClickListener {
            lastSelectedPosition = position
            listener.itemClicked(holder.absoluteAdapterPosition)
            setSelectedButton(it)
        }

        holder.binding?.btnGroupItem?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                focusedView(holder.binding.btnGroupItem)
            } else {
                if (lastSelectedPosition == position) selectedView(holder.binding.btnGroupItem)
                else defaultView(holder.binding.btnGroupItem)
            }
        }

        holder.binding?.btnGroupItem?.setOnKeyListener { v, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        return@setOnKeyListener true
                    }

                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        if(position == 0){
                            keyListener.onKey(v, keyCode, event)
                            return@setOnKeyListener true
                        }
                    }
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        keyListener.onKey(v, keyCode, event)
                        return@setOnKeyListener true
                    }

                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        if (position == groupList.size - 1)
                            return@setOnKeyListener true
                    }

                }
            }
            false
        }
    }

    private fun setSelectedButton(view: View?) {
        defaultView(selectedButton)
        selectedButton = view as AppCompatButton?
        selectedView(selectedButton)
    }

    private fun selectedView(view: AppCompatButton?) {
        view?.background =
            ContextCompat.getDrawable(context, R.drawable.group_tab_selected_bg)
        view?.setTextColor(ContextCompat.getColor(context, R.color.white))
    }

    private fun defaultView(view: AppCompatButton?) {
        view?.background = ContextCompat.getDrawable(context, R.drawable.group_tab_default_bg)
        view?.setTextColor(ContextCompat.getColor(context, R.color.help_faq_default_text_color))
    }

    private fun focusedView(view: AppCompatButton?) {
        view?.background = ContextCompat.getDrawable(
            context, R.drawable.group_tab_focused_bg
        )
    }

    interface ItemClickListener {
        fun itemClicked(position: Int)
    }
}