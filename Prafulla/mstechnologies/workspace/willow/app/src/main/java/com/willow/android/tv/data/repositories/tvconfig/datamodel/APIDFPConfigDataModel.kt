package com.willow.android.tv.data.repositories.tvconfig.datamodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class APIDFPConfigDataModel(val androidtv: Androidtv) : Parcelable {}

@Parcelize
data class Androidtv(
    val main_config: MainConfig,
    val user_config: UserConfig
): Parcelable{}

@Parcelize
data class MainConfig(
    val enable_ads_for_vod: Boolean,
    val enable_ads_for_live: Boolean
): Parcelable{}

@Parcelize
data class UserConfig(
    val paid: Paid,
    val free: Free
): Parcelable{}

@Parcelize
data class Paid(
    val enable_ads_for_vod: Boolean,
    val enable_ads_for_live: Boolean
): Parcelable{}

@Parcelize
data class Free(
    val enable_ads_for_vod: Boolean,
    val enable_ads_for_live: Boolean
): Parcelable{}
