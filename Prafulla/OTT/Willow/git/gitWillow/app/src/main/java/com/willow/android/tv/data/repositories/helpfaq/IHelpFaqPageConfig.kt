package com.willow.android.tv.data.repositories.helpfaq

import com.willow.android.tv.data.repositories.helpfaq.datamodel.ApiHelpDataModel
import com.willow.android.tv.utils.Resource

interface IHelpFaqPageConfig {

    suspend fun getHelpPageData(url: String): Resource<ApiHelpDataModel>
}