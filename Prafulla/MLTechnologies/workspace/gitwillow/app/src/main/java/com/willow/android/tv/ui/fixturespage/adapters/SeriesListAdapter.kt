package com.willow.android.tv.ui.fixturespage.adapters

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.databinding.RowSeriesListItemBinding
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.data.repositories.fixturespage.datamodel.FixturesBySeries
import com.willow.android.tv.utils.ImageUtility
import timber.log.Timber

class SeriesListAdapter(private val callBack: SeriesItemClickListener) :
    RecyclerView.Adapter<SeriesListAdapter.MainViewHolder>() {

    private var seriesList = mutableListOf<FixturesBySeries>()
    private lateinit var keyListener: KeyListener
    private lateinit var context: Context

    fun setMatchesListToRV(movies: List<FixturesBySeries>?, keyListener: KeyListener) {
        this.seriesList = movies?.toMutableList()!!
        this.keyListener = keyListener
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowSeriesListItemBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val series = seriesList[position]
        Timber.d("ImagePath:: " + series.getThumbnailLarge())

        ImageUtility.loadImagewithRoundCornersTransform(
            series.getThumbnailLarge(),
            R.drawable.default_large_holder, holder.binding.imageView
        )

        holder.binding.itemView.setOnClickListener {

            callBack.onSeriesItemClickListener(series, position)
        }

        if (position == 0)
            holder.binding.itemView.requestFocus()

        holder.binding.itemView.setOnKeyListener { v, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        if ((position % (context.resources.getInteger(R.integer.span_count))) == 0) {
                            keyListener.onKey(v, keyCode, event)
                            return@setOnKeyListener true
                        }
                    }
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        if (isPositionIsFirstRow(position)) {
                            keyListener.onKey(v, keyCode, event)
                            return@setOnKeyListener true
                        }
                    }

                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        if(isSelectedPositionInLasRow(position))
                            return@setOnKeyListener true
                    }
                }
            }
            return@setOnKeyListener false
        }
    }

    override fun getItemCount(): Int {
        return seriesList.size
    }

    inner class MainViewHolder(val binding: RowSeriesListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    private fun isSelectedPositionInLasRow(position: Int): Boolean {
        val fullRowCount = seriesList.size / context.resources.getInteger(R.integer.span_count)
        val lastPositionOfFullRow = fullRowCount * context.resources.getInteger(R.integer.span_count) -1
        return position > lastPositionOfFullRow
    }

    private fun isPositionIsFirstRow(position: Int): Boolean {
        return position < (context.resources.getInteger(R.integer.span_count))
    }
}

interface SeriesItemClickListener {
    fun onSeriesItemClickListener(match: FixturesBySeries, position: Int)
}
