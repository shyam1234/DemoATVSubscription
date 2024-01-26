package com.willow.android.tv.common.carousel.models

import android.os.Parcelable
import com.willow.android.tv.common.Types
import com.willow.android.tv.data.repositories.commondatamodel.Card
import kotlinx.parcelize.Parcelize

@Parcelize
data class HeroBanner(
    var listItems: ArrayList<Card>?,
    var type: Types.HeroBanner,
    var isContinueWatching: Boolean = false
):Parcelable {

}
