package com.willow.android.tv.ui.login.model


data class LoginResultModel(
    val result: Boolean ,
    val message: String,
    val emailEmpty:Boolean?=false,
    val passWordEmpty:Boolean?=false
)