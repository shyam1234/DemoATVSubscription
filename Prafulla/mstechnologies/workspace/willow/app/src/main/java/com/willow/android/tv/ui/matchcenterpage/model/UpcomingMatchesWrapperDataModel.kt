package com.willow.android.tv.ui.matchcenterpage.model

import com.willow.android.tv.data.repositories.matchcenterpage.datamodel.Item

/**
 * Created by eldhosepaul on 22/03/23.
 */
data class UpcomingMatchesWrapperDataModel(
    var listAllMatches: Boolean? = false,
    val matchesList: List<Item>?
)
