package com.willow.android.tv.ui.videospage.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.willow.android.tv.data.repositories.RepositoryFactory
import com.willow.android.tv.data.repositories.commondatamodel.CommonCardRow
import com.willow.android.tv.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideosViewModel(application: Application) : AndroidViewModel(application) {


    private val _renderPage = MutableLiveData<Resource<CommonCardRow>>()
    val renderPage: LiveData<Resource<CommonCardRow>> = _renderPage



    fun loadVideosPageConfig(url: String?) {
        viewModelScope.launch (Dispatchers.IO){
            _renderPage.postValue(Resource.Loading())
            fetchExplorePageDetails(url)
        }
    }

    private suspend fun fetchExplorePageDetails(url :String?) {
        val data =  RepositoryFactory.getVideosPageRepository().getVideosPageData(getApplication(),true)
        _renderPage.postValue(data.getVideosPageConfig(url))
    }



}
