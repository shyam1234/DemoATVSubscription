package com.willow.android.tv.ui.scoreCardMatchInfo.model

data class MatchInfoData(
    val title :String?,
    val rowData:List<MatchInfoRow>?,
    val paraData:List<MatchInfoParaData>?
)
data class MatchInfoRow(
    val title :String?,
    val value :String?
)

data class MatchInfoParaData(
    val heading :String?,
    val para :String?
)