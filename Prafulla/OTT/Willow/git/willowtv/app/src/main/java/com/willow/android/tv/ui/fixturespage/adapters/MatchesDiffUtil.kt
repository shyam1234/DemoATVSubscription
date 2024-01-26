package com.willow.android.tv.ui.fixturespage.adapters

import androidx.recyclerview.widget.DiffUtil
import com.willow.android.tv.data.repositories.fixturespage.datamodel.FixturesByDate
import org.jetbrains.annotations.Nullable


/**
 * Created by eldhosepaul on 17/05/23.
 */
class MatchesDiffUtil(oldFixtureList: List<FixturesByDate>, newFixtureList: List<FixturesByDate>) :
    DiffUtil.Callback() {
    private val mOldFixtureList: List<FixturesByDate>
    private val mNewFixtureList: List<FixturesByDate>

    init {
        mOldFixtureList = oldFixtureList
        mNewFixtureList = newFixtureList
    }

    override fun getOldListSize(): Int {
        return mOldFixtureList.size
    }

    override fun getNewListSize(): Int {
        return mNewFixtureList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldFixtureList[oldItemPosition].event_id == mNewFixtureList[newItemPosition].event_id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldFixture: FixturesByDate = mOldFixtureList[oldItemPosition]
        val newFixture: FixturesByDate = mNewFixtureList[newItemPosition]
        return oldFixture.title.equals(newFixture.title)
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}