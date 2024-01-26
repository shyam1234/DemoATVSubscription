package com.willow.android.tv.data.repositories.pointtable.remote

import com.willow.android.tv.data.repositories.pointtable.datamodel.PointTableDataModelResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface PointTablePageAPIs {

    @GET
    suspend fun getPointTableData(@Url url: String) : Response<PointTableDataModelResponse>
}