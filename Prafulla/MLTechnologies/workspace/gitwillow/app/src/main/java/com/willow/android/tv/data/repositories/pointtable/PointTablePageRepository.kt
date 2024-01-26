package com.willow.android.tv.data.repositories.pointtable

import android.app.Application
import com.willow.android.tv.data.repositories.pointtable.remote.PointTablePageRemoteDataSource

class PointTablePageRepository {

    fun pointTableData(application: Application): IPointTablePageConfig {
        return PointTablePageRemoteDataSource(application)
    }
}