package com.willow.android.tv.data.repositories.player

import com.willow.android.tv.data.repositories.player.datamodel.APIPollerDataModel
import com.willow.android.tv.data.repositories.player.datamodel.APIStreamingURLDataModel
import com.willow.android.tv.ui.playback.model.PlayerRequestModel
import com.willow.android.tv.ui.playback.model.PollerModel
import com.willow.android.tv.utils.Resource

interface IPlayerConfig {
    suspend fun getPlayerConfig(reqModel: PlayerRequestModel): Resource<APIStreamingURLDataModel>
    suspend fun getPollerReq(reqModel: PollerModel): Resource<APIPollerDataModel>
}