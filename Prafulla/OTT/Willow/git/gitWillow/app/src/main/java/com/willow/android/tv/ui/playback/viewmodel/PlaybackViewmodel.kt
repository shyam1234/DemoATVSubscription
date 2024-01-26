package com.willow.android.tv.ui.playback.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.willow.android.WillowApplication
import com.willow.android.tv.data.repositories.RepositoryFactory
import com.willow.android.tv.data.repositories.player.datamodel.APIPollerDataModel
import com.willow.android.tv.data.repositories.player.datamodel.APIStreamingURLDataModel
import com.willow.android.tv.data.room.db.VideoProgressDao
import com.willow.android.tv.data.room.entities.VideoProgress
import com.willow.android.tv.ui.playback.model.PlayerRequestModel
import com.willow.android.tv.ui.playback.model.PollerModel
import com.willow.android.tv.utils.CommonFunctions.shouldSaveProgressToDb
import com.willow.android.tv.utils.GlobalConstants
import com.willow.android.tv.utils.PrefRepository
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.events.ContinueWatchingEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.Date

/**
 * Created by eldhosepaul on 14/02/23.
 */

class PlaybackViewmodel(application: Application, private val videoProgressDao: VideoProgressDao) : AndroidViewModel(application) {
    private val _videoProgress = MutableLiveData<VideoProgress?>()
    val videoProgress: MutableLiveData<VideoProgress?>
        get() = _videoProgress


    private val _renderPage = MutableLiveData<Resource<APIStreamingURLDataModel>>()
    val renderPage: LiveData<Resource<APIStreamingURLDataModel>> = _renderPage

    private val _pollerData = MutableLiveData<APIPollerDataModel?>()
    val pollerData: LiveData<APIPollerDataModel?> = _pollerData


    fun getPlayerStreamingURL(reqModel : PlayerRequestModel){
        _renderPage.postValue(Resource.Loading())
        viewModelScope.launch {
            val data =  RepositoryFactory.getPlayerRepository().getPlayerData(getApplication())
            viewModelScope.launch {
                _renderPage.postValue(data.getPlayerConfig(reqModel))
            }
        }
    }
    fun getPollerRequest(match_id: String?, guid: String?){
        viewModelScope.launch {
           startPollerAPI(match_id,guid)
        }
    }
    fun loadVideoProgress(videoId: Int) {
        viewModelScope.launch {
            val latestProgress = videoProgressDao.getVideoProgressByVideoId(videoId)
            Timber.d("Progress:: $latestProgress")
            _videoProgress.postValue(latestProgress)
        }
    }

    fun saveVideoProgress(videoId: Int, progress: Double,totalDuration:Double) {
        GlobalScope.launch {
            insertOrUpdateVideoProgress(videoId, progress,totalDuration)
        }
    }

    fun callDeleteVideoFromDb(videoId: Int){
        GlobalScope.launch {
            deleteVideoFromDb(videoId)
        }
    }




//    fun storeVideoProgress(videoId: Int, progress: Int, totalDuration:Int) {
//        if(shouldSaveProgressToDb(progress, totalDuration)) {
//            viewModelScope.launch {
//                val timestamp = Date()
//                val videoProgress = VideoProgress(videoId, progress, timestamp)
//                videoProgressDao.insert(videoProgress)
//            }
//        }
//    }

    suspend fun insertOrUpdateVideoProgress(videoId: Int, progress: Double,totalDuration:Double) {
        if(shouldSaveProgressToDb(progress, totalDuration)) {

            val existingVideoProgress = videoProgressDao.getVideoProgressByVideoId(videoId)

            Timber.d("Test1 existingVideoProgress:: "+existingVideoProgress)
            val timestamp = Date()
            if (existingVideoProgress == null) {
                val newVideoProgress = VideoProgress(videoId, progress, timestamp)
                videoProgressDao.insertOrUpdateVideoProgress(newVideoProgress)
            } else {
                existingVideoProgress.progress = progress
                existingVideoProgress.timestamp = timestamp
                videoProgressDao.insertOrUpdateVideoProgress(existingVideoProgress)
            }

            EventBus.getDefault().post(ContinueWatchingEvent(true))

        }else{
            val percent = (progress/totalDuration)*100
            if(percent> GlobalConstants.MAX_CONTENT_PROGRESS){
                deleteVideoFromDb(videoId)
            }
        }
    }

    suspend fun deleteVideoFromDb(videoId: Int){
        val existingVideoProgress = videoProgressDao.getVideoProgressByVideoId(videoId)

        Timber.d("deleteVideoFromDb existingVideoProgress:: "+existingVideoProgress)

        if (existingVideoProgress != null) {
            videoProgressDao.deleteByVideoId(videoId)
            Timber.d("deleteVideoFromDb existingVideoProgress11:: "+existingVideoProgress)

            EventBus.getDefault().postSticky(ContinueWatchingEvent(false))
        }
    }
    private suspend fun startPollerAPI(match_id: String?, guid: String?) {
        val playerRepo =  RepositoryFactory.getPlayerRepository().getPlayerData(WillowApplication.instance)

        val prefRepository =PrefRepository(WillowApplication.instance)

        lateinit var userId: String
        if(prefRepository.getLoggedIn()==true){
            userId = prefRepository.getUserID()
        }else if(prefRepository.getTVELoggedIn()==true) {
            userId = prefRepository.getTVEUserID()
        }

        val user = PollerModel(
            match_id,
            guid,
            userId
        )

        Timber.d("WORKER:: "+user)

        _pollerData.postValue(playerRepo.getPollerReq(user).data)

    }
}