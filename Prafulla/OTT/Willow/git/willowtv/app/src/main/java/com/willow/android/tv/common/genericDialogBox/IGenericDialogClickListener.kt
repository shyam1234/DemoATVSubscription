package com.willow.android.tv.common.genericDialogBox

import android.view.View

interface IGenericDialogClickListener{
    fun onPositiveClick(view :View)
    fun onNegativeClick(view :View)
}