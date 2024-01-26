package com.willow.android.tv.common.datamodels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Created by eldhosepaul on 24/02/23.
 */
@Parcelize
data class PlayerBundleDataModel(val type: String, val data: @RawValue Any? ):Parcelable
