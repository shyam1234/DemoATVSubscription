package com.willow.android.tv.data.repositories.signuppage

import com.willow.android.tv.data.repositories.signuppage.datamodel.APISIgnupDataModel
import com.willow.android.tv.ui.login.model.SignUpUserModel
import com.willow.android.tv.utils.Resource

interface ISignUpPageRepository {
    suspend fun getSignupUser(signUpUser: SignUpUserModel): Resource<APISIgnupDataModel>
}