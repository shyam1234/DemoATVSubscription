package com.willow.android.tv.utils.events

import com.willow.android.tv.data.repositories.commondatamodel.CardRow

/**
 * Created by eldhosepaul on 02/03/23.
 */
data class LiveRefreshEvent(
    val cardRow: ArrayList<CardRow>
)