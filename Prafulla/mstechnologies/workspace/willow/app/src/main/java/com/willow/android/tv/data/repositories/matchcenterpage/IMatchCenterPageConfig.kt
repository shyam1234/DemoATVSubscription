package com.willow.android.tv.data.repositories.matchcenterpage


import com.willow.android.tv.data.repositories.matchcenterpage.datamodel.APIMatchCenterDataModel
import com.willow.android.tv.data.repositories.matchcenterpage.datamodel.APINewMatchCenterDataModel
import com.willow.android.tv.utils.Resource

interface IMatchCenterPageConfig {
    suspend fun getMatchCenterPageConfig(url:String) : Resource<APIMatchCenterDataModel>
    suspend fun getMatchCenterPageConfigNew(url:String) : Resource<APINewMatchCenterDataModel>
}