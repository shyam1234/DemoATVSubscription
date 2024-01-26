package com.willow.android.tv.ui.matchcenterpage.model

import com.willow.android.tv.data.repositories.commondatamodel.CardRow

data class MatchCenterPageModel(
//    val heroBannerModel: HeroBanner?,
    val cardRowModel: ArrayList<CardRow>,
    val title:String?="",
    val date:String?=""
)

