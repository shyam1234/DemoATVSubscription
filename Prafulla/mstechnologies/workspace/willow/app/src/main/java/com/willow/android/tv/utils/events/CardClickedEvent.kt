package com.willow.android.tv.utils.events

import com.willow.android.tv.data.repositories.commondatamodel.Card
import com.willow.android.tv.data.repositories.commondatamodel.CardRow

/**
 * Created by eldhosepaul on 10/03/23.
 */
data class CardClickedEvent(
    val card: Card,
    val cardRow: CardRow?=null,
)
