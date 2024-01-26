package com.willow.android.tv.ui.fixturespage.model

import com.willow.android.tv.data.repositories.fixturespage.datamodel.FixturesByDate

/**
 * Created by eldhosepaul on 22/03/23.
 */
data class MatchesWrapperDataModel(
    var listAllMatches: Boolean? = false,
    val matchesList: List<FixturesByDate>?
)
