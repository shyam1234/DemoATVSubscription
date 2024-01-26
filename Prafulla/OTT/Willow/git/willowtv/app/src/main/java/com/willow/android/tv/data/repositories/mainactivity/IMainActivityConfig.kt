package com.willow.android.tv.data.repositories.mainactivity

import com.willow.android.tv.data.repositories.mainactivity.datamodel.APICheckSubDataModel
import com.willow.android.tv.ui.main.model.UserModel
import com.willow.android.tv.utils.Resource

interface IMainActivityConfig {
    suspend fun getCheckSubscription(loginUser: UserModel): Resource<APICheckSubDataModel>


}