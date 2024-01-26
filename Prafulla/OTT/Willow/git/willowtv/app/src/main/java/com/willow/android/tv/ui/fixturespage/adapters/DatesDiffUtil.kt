package com.willow.android.tv.ui.fixturespage.adapters

import androidx.recyclerview.widget.DiffUtil
import com.willow.android.tv.ui.fixturespage.model.DateListModel
import org.jetbrains.annotations.Nullable


/**
 * Created by eldhosepaul on 17/05/23.
 */
class DatesDiffUtil(oldDatesList: List<DateListModel>, newDatesList: List<DateListModel>) :
    DiffUtil.Callback() {
    private val mOldDatesList: List<DateListModel>
    private val mNewDatesList: List<DateListModel>

    init {
        mOldDatesList = oldDatesList
        mNewDatesList = newDatesList
    }

    override fun getOldListSize(): Int {
        return mOldDatesList.size
    }

    override fun getNewListSize(): Int {
        return mNewDatesList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldDatesList[oldItemPosition].gmt_start_date == mNewDatesList[newItemPosition].gmt_start_date
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldDates: DateListModel = mOldDatesList[oldItemPosition]
        val newDates: DateListModel = mNewDatesList[newItemPosition]
        return oldDates.gmt_start_date.equals(newDates.gmt_start_date)
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}