package com.willow.android.tv.data.repositories.mainactivity.datamodel

import com.willow.android.tv.utils.GlobalConstants

data class APICheckSubDataModel(
    val EnableDfpForLive: Boolean,
    val EnableDfpForVOD: Boolean,
    val LogoutUser: Int,
    val ads_category: String,
    val email: String,
    val emailVerified: Int,
    val game_on_request: GameOnRequest,
    val highlightsFromYT: Int,
    val iTunesUser: Int,
    val iplShowPopup: Int,
    val iplUserCategory: Int,
    val iplUserDevice: String,
    val nextRenewalDate: String,
    val pricePaid: Double,
    val status: String,
    val subscriptionStatus: Int,
    val tveUser: Int,
    val tveUserId: String,
    val userId: Int,
    val wuid: String
){
    fun isSubscribed()= subscriptionStatus== GlobalConstants.ApiConstant.USER_IS_SUBSCRIBED
}

data class GameOnRequest(
    val post_params: PostParams
)

data class PostParams(
    val auth_token: String,
    val domain: String,
    val first_name: String,
    val last_name: String,
    val ts: Int,
    val user_id: String
)