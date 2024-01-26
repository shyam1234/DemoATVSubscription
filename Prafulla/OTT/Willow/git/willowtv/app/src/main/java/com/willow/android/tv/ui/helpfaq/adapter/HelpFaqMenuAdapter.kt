package com.willow.android.tv.ui.helpfaq.adapter

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
import com.willow.android.databinding.RowDynamicHelpFaqMenuItemBinding
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.data.repositories.helpfaq.datamodel.Setting

class HelpFaqMenuAdapter(
    private val data: List<Setting>,
    private val listener: ItemClickListener,
    private val keyListener: KeyListener
) : RecyclerView.Adapter<HelpFaqMenuAdapter.HelpFaqMenuVH>() {

    private lateinit var context: Context
    private var lastSelectedPosition: Int = 0
    private var selectedButton: AppCompatButton? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpFaqMenuVH {
        context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the layout
        val view = inflater.inflate(R.layout.row_dynamic_help_faq_menu_item, parent, false)
        return HelpFaqMenuVH(view)
    }

    inner class HelpFaqMenuVH(view: View) : RecyclerView.ViewHolder(view) {
        val binding: RowDynamicHelpFaqMenuItemBinding? = DataBindingUtil.bind(view)
    }

    override fun onBindViewHolder(holder: HelpFaqMenuVH, position: Int) {
        val item = data[holder.absoluteAdapterPosition]
        holder.binding?.model = item

        if (holder.absoluteAdapterPosition == 0) {
            val model = data[lastSelectedPosition]
            listener.itemClicked(lastSelectedPosition, model)
            setSelectedButton(holder.binding?.btnMenuItem)
        }

        holder.binding?.btnMenuItem?.setOnClickListener {
            lastSelectedPosition = position
            listener.itemClicked(holder.absoluteAdapterPosition, item)
            setSelectedButton(it)
            //notifyDataSetChanged()
        }

        if (lastSelectedPosition == position) {
            holder.binding?.btnMenuItem?.requestFocus()
            holder.binding?.btnMenuItem?.isSelected = true
        }

        holder.binding?.btnMenuItem?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                focusedView(holder.binding.btnMenuItem)
            } else {
                if (lastSelectedPosition == position) selectedView(holder.binding.btnMenuItem)
                else defaultView(holder.binding.btnMenuItem)
            }
        }

        holder.binding?.btnMenuItem?.setOnKeyListener { v, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        if (position == data.size - 1) return@setOnKeyListener true
                    }

                    KeyEvent.KEYCODE_DPAD_UP -> {
                        if (position == 0) return@setOnKeyListener true
                    }
                }
                keyListener.onKey(v, keyCode, event)
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
            ContextCompat.getDrawable(context, R.drawable.help_faq_selected_button_background)
        view?.setTextColor(ContextCompat.getColor(context, R.color.white))
    }

    private fun defaultView(view: AppCompatButton?) {
        view?.background = ContextCompat.getDrawable(context, R.color.transparent)
        view?.setTextColor(ContextCompat.getColor(context, R.color.help_faq_default_text_color))
    }

    private fun focusedView(view: AppCompatButton?) {
        view?.background = ContextCompat.getDrawable(
            context, R.drawable.help_faq_focused_button_background
        )
        view?.setTextColor(ContextCompat.getColor(context, R.color.help_faq_default_text_color))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface ItemClickListener {
        fun itemClicked(position: Int, model: Setting)
    }

    fun getSelectedButton(): AppCompatButton ? = selectedButton


}