package com.willow.android.tv.ui.matchcenterpage.adapters

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.databinding.RowDatesListItemBinding
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.ui.fixturespage.model.DateListModel
import timber.log.Timber

class UpcomingDatesAdapter(private val onClick: ItemClickListener) :
    RecyclerView.Adapter<UpcomingDatesAdapter.MainViewHolder>() {


    private var dateList = mutableListOf<DateListModel>()
    private var lastSelectedPosition: Int = -1
    private var mainViewHolder: MainViewHolder ? = null
    private lateinit var context: Context
    private lateinit var keyListener: KeyListener

    fun setDateListToRV(movies: List<DateListModel>?, keyListener: KeyListener) {
        this.dateList = movies?.toMutableList()!!
        this.keyListener = keyListener
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowDatesListItemBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val date = dateList[position]
        Timber.d("DateRV:: "+date.pst_start_date+" :: "+date.selected)
        holder.binding.txtDateTime.text = date.pst_start_date

        if(date.selected){
//            holder.itemView.requestFocus()
            onClick.onItemClickListener(date)
            setSelectedButton(holder)
        }

        holder.itemView.setOnClickListener {
            lastSelectedPosition = position
            onClick.onItemClickListener(date)
            setSelectedButton(holder)
        }

        holder.binding.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                focusedView(holder)
            } else {
                if (lastSelectedPosition == position) selectedView(holder)
                else defaultView(holder)
            }
        }

        holder.binding.itemView.setOnKeyListener { v, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN) {
                if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
                    if(position == dateList.size - 1)
                        return@setOnKeyListener true
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
                    keyListener.onKey(v, keyCode, event)
                    return@setOnKeyListener true
                } else
                    keyListener.onKey(v, keyCode, event)
            }
            false
        }
    }

    override fun getItemCount(): Int {
        return dateList.size
    }

    inner class MainViewHolder(val binding: RowDatesListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    private fun setSelectedButton(holder: MainViewHolder?) {
        defaultView(mainViewHolder)
        mainViewHolder = holder
        selectedView(mainViewHolder)
    }

    private fun selectedView(holder: MainViewHolder?) {
        holder?.binding?.itemView?.background =
            ContextCompat.getDrawable(context, R.drawable.help_faq_selected_button_background)
        holder?.binding?.txtDateTime?.setTextColor(ContextCompat.getColor(context, R.color.white))
    }

    private fun defaultView(holder: MainViewHolder?) {
        holder?.binding?.itemView?.background =
            ContextCompat.getDrawable(context, R.color.transparent)
        holder?.binding?.txtDateTime?.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.help_faq_default_text_color
            )
        )
    }

    private fun focusedView(holder: MainViewHolder?) {
        holder?.binding?.itemView?.background = ContextCompat.getDrawable(
            context, R.drawable.help_faq_focused_button_background
        )
        holder?.binding?.txtDateTime?.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.help_faq_default_text_color
            )
        )
    }
}

interface ItemClickListener {
    fun onItemClickListener(date: DateListModel)
}
