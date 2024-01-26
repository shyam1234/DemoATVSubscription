package com.willow.android.tv.data.repositories.player.remote

import android.app.Application
import com.willow.android.WillowApplication
import com.willow.android.tv.data.repositories.BaseRemoteDataSource
import com.willow.android.tv.data.repositories.player.IPlayerConfig
import com.willow.android.tv.data.repositories.player.datamodel.APIPollerDataModel
import com.willow.android.tv.data.repositories.player.datamodel.APIStreamingURLDataModel
import com.willow.android.tv.ui.playback.model.PlayerRequestModel
import com.willow.android.tv.ui.playback.model.PollerModel
import com.willow.android.tv.utils.PrefRepository
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.config.GlobalTVConfig
import okhttp3.MultipartBody
import javax.inject.Inject

class PlayerRemoteDataSource(application: Application) : BaseRemoteDataSource(), IPlayerConfig {

    init {
        (application  as WillowApplication).applicationComponent.inject(this)
    }

    @Inject
    lateinit var playerAPIs: PlayerAPIs

    override suspend fun getPlayerConfig(reqModel:PlayerRequestModel): Resource<APIStreamingURLDataModel> {
        val prefRepository = PrefRepository(WillowApplication.instance)
        val requestBodyBuilder: MultipartBody.Builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("mid", reqModel.matchId.toString())
            .addFormDataPart("devType", reqModel.devType)
            .addFormDataPart("id", reqModel.contentId.toString())
            .addFormDataPart("type", reqModel.contentType.toString())
            .addFormDataPart("need_login", reqModel.needLogin.toString())
            .addFormDataPart("need_subscription", reqModel.needSubscription.toString())

        if(reqModel.contentType == "live"){
            requestBodyBuilder.addFormDataPart("pr", reqModel.priority.toString())
        }

        if (prefRepository.getTVELoggedIn()==true) {
            requestBodyBuilder.addFormDataPart("clientless",reqModel.clientless.toString())
            requestBodyBuilder.addFormDataPart("token",reqModel.mediaToken.toString())
            if (prefRepository.isTVEProviderSpectrum()&& reqModel.contentType!="live") {
                requestBodyBuilder.addFormDataPart("request_from_mvpd",reqModel.requestFromMVPD.toString())
                requestBodyBuilder.addFormDataPart("requestor_content_id",reqModel.requestorContentId.toString())
            }
        }else{
            requestBodyBuilder.addFormDataPart("wuid", reqModel.willowUserID.toString())
        }
        val requestBody = requestBodyBuilder.build()

        return safeApiCall { playerAPIs.getPlayerPayload(reqModel.url.toString(),requestBody) }
    }
    override suspend fun getPollerReq(reqModel: PollerModel): Resource<APIPollerDataModel> {
        val url : String
        if (reqModel.guid?.isEmpty() == true) {
            url = GlobalTVConfig.getPollerUrl()+"/plMobile?" + "UserId=" + reqModel.userId + "&matchId=" + reqModel.matchId
        } else {
            url = GlobalTVConfig.getPollerUrl()+"/plMobile?" + "UserId=" + reqModel.userId + "&matchId=" + reqModel.matchId + "&guid=" + reqModel.guid
        }
        return safeApiCall { playerAPIs.getPollerPayload(url) }
    }

}