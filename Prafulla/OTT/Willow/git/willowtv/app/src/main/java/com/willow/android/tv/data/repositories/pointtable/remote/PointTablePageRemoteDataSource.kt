package com.willow.android.tv.data.repositories.pointtable.remote

import android.app.Application
import com.willow.android.WillowApplication
import com.willow.android.tv.data.repositories.BaseRemoteDataSource
import com.willow.android.tv.data.repositories.pointtable.IPointTablePageConfig
import com.willow.android.tv.data.repositories.pointtable.datamodel.PointTableDataModelResponse
import com.willow.android.tv.utils.Resource
import javax.inject.Inject

class PointTablePageRemoteDataSource(application: Application) : BaseRemoteDataSource(), IPointTablePageConfig {

    init {
        (application as WillowApplication).applicationComponent.inject(this)
    }
    @Inject
    lateinit var pointTablePageApi: PointTablePageAPIs

    override suspend fun getPointTableData(url: String): Resource<PointTableDataModelResponse> {
        return safeApiCall { pointTablePageApi.getPointTableData(url) }
    }
}