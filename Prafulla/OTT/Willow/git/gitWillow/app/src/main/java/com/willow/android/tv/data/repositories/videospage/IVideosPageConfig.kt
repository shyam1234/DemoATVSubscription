package com.willow.android.tv.data.repositories.videospage

import com.willow.android.tv.data.repositories.commondatamodel.CommonCardRow
import com.willow.android.tv.utils.Resource

interface IVideosPageConfig {
    suspend fun getVideosPageConfig(url: String? = null): Resource<CommonCardRow>
}