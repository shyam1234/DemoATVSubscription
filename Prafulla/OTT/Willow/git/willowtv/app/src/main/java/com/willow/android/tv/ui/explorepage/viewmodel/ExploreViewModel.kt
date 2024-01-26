package com.willow.android.tv.ui.explorepage.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.willow.android.tv.data.repositories.RepositoryFactory
import com.willow.android.tv.data.repositories.commondatamodel.Card
import com.willow.android.tv.data.repositories.commondatamodel.CardRow
import com.willow.android.tv.data.repositories.commondatamodel.CommonCardRow
import com.willow.android.tv.data.repositories.tvconfig.datamodel.NavigationTabsDataModel
import com.willow.android.tv.data.room.db.VideoProgressDao
import com.willow.android.tv.utils.GlobalConstants.CONTINUE_WATCHING_POS
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.extension.toJsonHashUsingGson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ExploreViewModel(application: Application, private val navData : NavigationTabsDataModel?, private val videoProgressDao: VideoProgressDao) : AndroidViewModel(application) {



    private val _renderPage = MutableLiveData<Resource<CommonCardRow>>()
    val renderPage: LiveData<Resource<CommonCardRow>> = _renderPage


    private val _renderLiveRow = MutableLiveData<Resource<CommonCardRow>>()
    val renderLiveRow: LiveData<Resource<CommonCardRow>> = _renderLiveRow


    var configJsonData: String? = null

    init {
        loadExplorePageConfig()
    }

    private fun loadExplorePageConfig() {
        viewModelScope.launch (Dispatchers.IO){
            _renderPage.postValue(Resource.Loading())
            fetchExplorePageDetails()
        }
    }

    fun reloadExplorePageConfig() {
        viewModelScope.launch (Dispatchers.IO){
            fetchExplorePageDetails()
        }
    }
    private suspend fun fetchExplorePageDetails() {
        val data =  RepositoryFactory.getExplorePageRepository().getExplorePageData(getApplication(),true)

        val config = data.getExplorePageConfig()

        Timber.d("#### fetchExplorePageDetails 1 : "+configJsonData)

        if(configJsonData == null){
            configJsonData = config.data?.toJsonHashUsingGson()
            Timber.d("#### fetchExplorePageDetails 2 : "+configJsonData)

            var cardContinue= ArrayList<Card>()

            config.data?.result?.rows?.forEach { cardRow ->

                cardRow.items?.forEach { card ->
                    val videoProgress = card.content_id?.let { videoProgressDao.getVideoProgressByVideoId(it)}
                    if(videoProgress !=null){
                        card.progress = videoProgress.progress
                        cardContinue.add(card.copy())
                    }
                }
            }
            //LIFO
            cardContinue.reverse()
            val continueWatchRow = CardRow(title = "Continue Watching", sub_title = "", card_type = "medium_landscape", items_category = "video", items = cardContinue)
            config.data?.result?.rows?.add(CONTINUE_WATCHING_POS,continueWatchRow)

            _renderPage.postValue(config)
        }else{
            val newJsonData =  config.data?.toJsonHashUsingGson()
            Timber.d("#### fetchExplorePageDetails 3 : "+newJsonData)
            Timber.d("#### fetchExplorePageDetails 4 : "+configJsonData)


            if(newJsonData !==null && newJsonData != configJsonData){
                configJsonData = newJsonData

                _renderLiveRow.postValue(config)
            }
        }


    }


}
