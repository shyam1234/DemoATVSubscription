package com.willow.android.tv.data.repositories.pointtable

import com.willow.android.tv.data.repositories.pointtable.datamodel.PointTableDataModelResponse
import com.willow.android.tv.utils.Resource

interface IPointTablePageConfig {

    suspend fun getPointTableData(url: String): Resource<PointTableDataModelResponse>
}