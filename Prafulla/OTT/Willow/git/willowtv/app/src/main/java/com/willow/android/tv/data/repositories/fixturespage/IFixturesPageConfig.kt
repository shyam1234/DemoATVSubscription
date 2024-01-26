package com.willow.android.tv.data.repositories.fixturespage


import com.willow.android.tv.data.repositories.fixturespage.datamodel.APIFixturesDataModel
import com.willow.android.tv.utils.Resource

interface IFixturesPageConfig {
    suspend fun getFixturesPageConfig() : Resource<APIFixturesDataModel>
}