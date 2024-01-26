package com.willow.android.tv.ui.playback.model

import com.willow.android.tv.utils.GlobalConstants.DEV_TYPE

/**
 * Created by eldhosepaul on 17/02/23.
 */
data class PlayerRequestModel(
    val matchId: Int?,
    var willowUserID: String?=null,
    val contentId: Int?,
    val contentType: String?,
    var url: String?,
    val devType: String= DEV_TYPE,
    var clientless: String? = null,
    var requestFromMVPD:String? = null,
    var requestorContentId:String? = null,
    var mediaToken:String? = null,
    var priority:String?=null,
    var needLogin:Boolean? = null,
    var needSubscription:Boolean? = null
)
