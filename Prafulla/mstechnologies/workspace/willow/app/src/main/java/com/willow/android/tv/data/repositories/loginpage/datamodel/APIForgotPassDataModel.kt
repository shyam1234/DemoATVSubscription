package com.willow.android.tv.data.repositories.loginpage.datamodel

data class APIForgotPassDataModel(
    val result: ResultModel
)

data class ResultModel(
    val message: String,
    val status: String
)