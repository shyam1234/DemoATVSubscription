package com.willow.android.tv.ui.pointtable.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.willow.android.tv.common.base.BaseAndroidViewModel
import com.willow.android.tv.data.repositories.RepositoryFactory
import com.willow.android.tv.data.repositories.pointtable.datamodel.PointTableDataModelResponse
import com.willow.android.tv.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PointTableViewModel(application: Application) :
    BaseAndroidViewModel(application) {

    private val _renderPage = MutableLiveData<Resource<PointTableDataModelResponse>>()
    val renderPage: LiveData<Resource<PointTableDataModelResponse>> = _renderPage


    fun loadPointTableData(pointTableUrl: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            _renderPage.postValue(Resource.Loading())
            pointTableUrl?.let {
                fetchHelpPageData(it)
            }

        }
    }

    private suspend fun fetchHelpPageData(pointTableUrl: String) {
        val data =
            RepositoryFactory.getPointTablePageRepository()
                .pointTableData(application = getApplication())
        _renderPage.postValue(data.getPointTableData(pointTableUrl))
    }
}