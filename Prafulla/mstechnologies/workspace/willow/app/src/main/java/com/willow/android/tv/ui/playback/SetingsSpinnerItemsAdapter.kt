package com.willow.android.tv.ui.playback

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.willow.android.R


class SetingsSpinnerItemsAdapter(val context: Context, var dataSource: ArrayList<String?>) :
    BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val itemHolder: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.player_settings_spinner_item, parent, false)
            itemHolder = ItemHolder(view)
            view?.tag = itemHolder
        } else {
            view = convertView
            itemHolder = view.tag as ItemHolder
        }
        //Following is done to make "HD" color red in label.
        if (dataSource[position]?.contains("HD") == true) {
            val spannableString = SpannableString(dataSource[position])
            val spannableStringBuilder = SpannableStringBuilder("HD")
            val red = ForegroundColorSpan(Color.RED)
            spannableString.setSpan(red,
                spannableString.length.minus(2), spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            itemHolder.textViewLabel.text = spannableString
        } else {
            itemHolder.textViewLabel.text = dataSource[position]
        }

        return view
    }

    override fun getItem(position: Int): Any? {
        return dataSource[position];
    }

    override fun getCount(): Int {
        return dataSource.size;
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    private class ItemHolder(row: View?) {
        val textViewLabel: TextView

        init {
            textViewLabel = row?.findViewById(R.id.tv_label) as TextView
        }
    }

}