package com.willow.android.tv.ui.resultspage.adapters

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.databinding.RowDatesListItemBinding
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.ui.resultspage.model.ResultsDateListModel
import timber.log.Timber

class ResultsDatesAdapter(private val onClick: ResultsItemClickListener): RecyclerView.Adapter<ResultsDatesAdapter.MainViewHolder>() {


    private var dateList = mutableListOf<ResultsDateListModel>()
    private var selectedItem: ResultsDateListModel? = null
    private var lastSelectedPosition: Int = -1
    private lateinit var keyListener: KeyListener

    private var mainViewHolder: ResultsDatesAdapter.MainViewHolder? = null
    private lateinit var context: Context
    fun setDateListToRV(movies: List<ResultsDateListModel>?, keyListener: KeyListener) {
        this.dateList = movies?.toMutableList()!!
        this.keyListener = keyListener
        lastSelectedPosition = -1
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
        Timber.d("DateRV:: "+date.pst_start_date)
        holder.binding.txtDateTime.text = date.pst_start_date

        if(date.selected){
//            holder.itemView.requestFocus()
            onClick.onResultsItemClickListener(date)
            setSelectedButton(holder)
        }

        holder.itemView.setOnClickListener {
            lastSelectedPosition = position
            onClick.onResultsItemClickListener(date)
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
                } else
                    keyListener.onKey(v, keyCode, event)
            }
            false
        }
    }
    override fun getItemCount(): Int {
        return dateList.size
    }

    inner class MainViewHolder(val binding: RowDatesListItemBinding) : RecyclerView.ViewHolder(binding.root){

    }

    private fun setSelectedButton(holder: ResultsDatesAdapter.MainViewHolder?) {
        defaultView(mainViewHolder)
        mainViewHolder = holder
        selectedView(mainViewHolder)
    }

    private fun selectedView(holder: ResultsDatesAdapter.MainViewHolder?) {
        holder?.binding?.itemView?.background =
            ContextCompat.getDrawable(context, R.drawable.help_faq_selected_button_background)
        holder?.binding?.txtDateTime?.setTextColor(ContextCompat.getColor(context, R.color.white))
    }

    private fun defaultView(holder: ResultsDatesAdapter.MainViewHolder?) {
        holder?.binding?.itemView?.background =
            ContextCompat.getDrawable(context, R.color.transparent)
        holder?.binding?.txtDateTime?.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.help_faq_default_text_color
            )
        )
    }

    private fun focusedView(holder: ResultsDatesAdapter.MainViewHolder?) {
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



interface ResultsItemClickListener {
    fun onResultsItemClickListener(date: ResultsDateListModel)
}
