package com.willow.android.tv.ui.login.model

import android.util.Patterns


class LoginUserModel(
    val action: String ,
                val email: String,
                val password: String?,
                val authToken: String
) {

    val isEmailValid: Boolean
        get() = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordLengthGreaterThan5: Boolean
        get() = password?.length?:0 > 3


}