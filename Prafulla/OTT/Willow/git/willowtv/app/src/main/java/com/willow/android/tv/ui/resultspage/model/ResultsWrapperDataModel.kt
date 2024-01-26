package com.willow.android.tv.ui.resultspage.model

import com.willow.android.tv.data.repositories.resultspage.datamodel.ResultsByDate

/**
 * Created by eldhosepaul on 22/03/23.
 */
data class ResultsWrapperDataModel(
    var listAllMatches: Boolean? = false,
    val matchesList: List<ResultsByDate>?
)
