package com.willow.android.tv.data.repositories.explorepage

import com.willow.android.tv.data.repositories.commondatamodel.CommonCardRow
import com.willow.android.tv.utils.Resource

interface IExplorePageConfig {
    suspend fun getExplorePageConfig() : Resource<CommonCardRow>
}