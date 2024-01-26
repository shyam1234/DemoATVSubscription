package com.willow.android.tv.ui.fixturespage.adapters

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.databinding.RowMatchesListItemBinding
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.data.repositories.fixturespage.datamodel.FixturesByDate
import com.willow.android.tv.ui.fixturespage.model.MatchesWrapperDataModel
import com.willow.android.tv.utils.ImageUtility
import com.willow.android.tv.utils.changeColorOnFocusChange
import com.willow.android.tv.utils.hide
import com.willow.android.tv.utils.show
import timber.log.Timber


class MatchesListAdapter(private val callBack: MatchesItemClickListener,
                         private val callBAckWatchLiveItemClickListener: FixturesWatchLiveItemClickListener,
private val keyListener: KeyListener?): RecyclerView.Adapter<MatchesListAdapter.MainViewHolder>(),
View.OnKeyListener{

    private var matchList = mutableListOf<FixturesByDate>()
    private var listAllMatches : Boolean ?= false
    private var selectedPosition = -1
    private lateinit var mRecyclerView: RecyclerView

    fun setMatchesListToRV(matches: MatchesWrapperDataModel) {
        this.matchList = matches.matchesList?.toMutableList()!!
        this.listAllMatches = matches.listAllMatches
        notifyDataSetChanged()
    }
    fun updateFixturesListItems(employees: List<FixturesByDate>?) {
        val diffCallback = MatchesDiffUtil(this.matchList, employees!!)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.matchList.clear()
        this.matchList.addAll(employees)
        diffResult.dispatchUpdatesTo(this)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowMatchesListItemBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val match = matchList[position]
        Timber.d("DateRV:: "+match.getLocalTimeFromGmtTs())

        holder.binding.layoutMatchInfo.apply {
            imgLiveTag.hide()
            buttonWatchLive.hide()
            txtMatchScore1.hide()
            txtMatchScore2.hide()
            txtTime.show()
            txtHighlightedDateTime.hide()
            txtMatchVenue.show()
            txtMatchTitle.text = match.title
            txtMatchSubTitle1.text = match.sub_title
            txtMatchSubTitle2.text = match.series_name
            txtMatchVenue.text = match.venue
//            txtMatchScore1.text = match.getScoreOne()
//            txtMatchScore1.text = match.getScoreTwo()

            if(match.display_live_link){
                imgLiveTag.show()
                buttonWatchLive.show()
            }else{
//                val localTime = convertPSTToLocalTime(match.pst_start_date.substringAfter("@"))
//                if(localTime.isNotEmpty())
//                {
//                txtHighlightedDateTime.show()
//                if(listAllMatches== true){
//                    txtHighlightedDateTime.text = match.getLocalTimeWithDayFromGmtTs()
//
//                }else{
//                    txtHighlightedDateTime.text = match.getLocalTimeFromGmtTs().substringAfter("@")
//
//                }
//                if(listAllMatches== true){
                    txtHighlightedDateTime.show()

                    txtTime.text = match.getLocalTimeWithZoneGmtTs()

                    txtHighlightedDateTime.text = match.getLocalTimeWithDayFromGmtTs()

//                }else{
                    txtTime.text = match.getLocalTimeFromGmtTs().substringAfter("@")

//                }
//                }
            }
//            holder.binding.layoutMatchInfo.buttonWatchLive.setOnFocusChangeListener { v, hasFocus ->
//                val colorInt: Int = Color.WHITE
//                val colorInt2: Int = Color.TRANSPARENT
//                val csl = ColorStateList.valueOf(colorInt)
//                val csl2 = ColorStateList.valueOf(colorInt2)
//                if (hasFocus) {
//                    holder.binding.layoutMatchInfo.buttonWatchLive.strokeColor = csl
//                    holder.binding.layoutMatchInfo.buttonWatchLive.strokeWidth = 3
//                }else{
//                    holder.binding.layoutMatchInfo.buttonWatchLive.strokeColor = csl2
//                    holder.binding.layoutMatchInfo.buttonWatchLive.strokeWidth = 0
//                }
//            }
            holder.binding.layoutMatchInfo.buttonWatchLive.changeColorOnFocusChange(backgroundColor = R.color.neutral_grey)
            holder.binding.layoutMatchInfo.buttonMatchCenter.changeColorOnFocusChange(backgroundColor = R.color.neutral_grey)

//            holder.binding.layoutMatchInfo.buttonMatchCenter.setOnFocusChangeListener { v, hasFocus ->
//                val colorInt: Int = Color.WHITE
//                val colorInt2: Int = Color.TRANSPARENT
//                val csl = ColorStateList.valueOf(colorInt)
//                val csl2 = ColorStateList.valueOf(colorInt2)
//                if (hasFocus) {
//                    callBack.onFocusPositionChangeListener(position)
//                    holder.binding.layoutMatchInfo.buttonMatchCenter.strokeColor = csl
//                    holder.binding.layoutMatchInfo.buttonMatchCenter.strokeWidth = 3
//                }else{
//                    holder.binding.layoutMatchInfo.buttonMatchCenter.strokeColor = csl2
//                    holder.binding.layoutMatchInfo.buttonMatchCenter.strokeWidth = 0
//                }
//            }

            holder.binding.itemView.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    if(position == 0 ){
                        scrollToPosition(0)
                    }
                    selectedPosition = position
                    callBack.onFocusPositionChangeListener(position)
                    if (holder.binding.layoutMatchInfo.buttonWatchLive.isVisible) {
                        holder.binding.layoutMatchInfo.buttonWatchLive.requestFocus()
                    }
                    else
                        holder.binding.layoutMatchInfo.buttonMatchCenter.requestFocus()
                }

            }
        }
        Timber.d("ImagePath:: "+match.getThumbnail())
        ImageUtility.loadImagewithRoundCornersTransform(match.getThumbnail(),
            R.drawable.default_match_holder,holder.binding.bgImage)

        holder.binding.layoutMatchInfo.buttonMatchCenter.setOnClickListener {

            callBack.onMatchesItemClickListener(match,position)
        }

        holder.binding.layoutMatchInfo.buttonWatchLive.setOnClickListener {

            callBAckWatchLiveItemClickListener.onFixturesWatchLiveItemClickListener(match)
        }


        holder.binding.layoutMatchInfo.buttonMatchCenter.setOnKeyListener { view, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN) {
                when(keyCode){
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        if(!holder.binding.layoutMatchInfo.buttonWatchLive.isVisible) {
                            keyListener?.onKey(view, keyCode, event)
                            return@setOnKeyListener true
                        }
                    }
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        if(selectedPosition == 0)
                            return@setOnKeyListener true
                    }

                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        if(position == matchList.size - 1) {
                            return@setOnKeyListener true
                        }else{
                            mRecyclerView.findViewHolderForAdapterPosition(position)?.itemView?.requestFocus()
                        }
                    }
                }
            }
            return@setOnKeyListener false
        }

        holder.binding.layoutMatchInfo.buttonWatchLive.setOnKeyListener(this)
    }
    override fun getItemCount(): Int {
        return matchList.size
    }

    inner class MainViewHolder(val binding: RowMatchesListItemBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when(keyCode){
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    keyListener?.onKey(view, keyCode, event)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    if(selectedPosition == 0)
                        return true
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    if(selectedPosition == matchList.size - 1)
                        return true
                }
            }
        }
        return false
    }
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }
    fun scrollToPosition(position: Int){
        mRecyclerView.scrollToPosition(position)
    }
}
interface MatchesItemClickListener {
    fun onMatchesItemClickListener(match: FixturesByDate, position: Int)
    fun onFocusPositionChangeListener(position: Int)
}


interface FixturesWatchLiveItemClickListener {
    fun onFixturesWatchLiveItemClickListener(match: FixturesByDate)
}