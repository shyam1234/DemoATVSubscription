package com.willow.android.tv.data.repositories.signuppage.datamodel

data class APISIgnupDataModel(
    val game_on_request: GameOnRequest,
    val result: Result
)

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


data class Result(
    val EnableDfpForLive: Boolean,
    val EnableDfpForVOD: Boolean,
    val LogoutUser: Int,
    val ads_category: String,
    val email: String,
    val emailVarified: Int,
    val nextRenewalDate: String,
    val pricePaid: Double,
    val status: String,
    val subscriptionStatus: Int,
    val userId: Int,
    val message: String?
)
