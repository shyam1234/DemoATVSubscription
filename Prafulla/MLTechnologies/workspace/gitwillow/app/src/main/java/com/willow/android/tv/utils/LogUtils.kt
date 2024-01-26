package com.willow.android.tv.utils

import android.content.Context
import android.widget.Toast
import com.willow.android.BuildConfig
import timber.log.Timber

object LogUtils {
    fun d(tag: String?=null, messages: String) {
        if (BuildConfig.DEBUG)
            tag?.let {
                Timber.tag(tag).d(messages)
            }.run {
                Timber.d(messages)
            }
    }

    fun i(tag: String?=null, messages: String) {
        if (BuildConfig.DEBUG)
            tag?.let {
                Timber.tag(tag).i(messages)
            }.run {
                Timber.i(messages)
            }
    }

    fun e(tag: String?=null, messages: String?) {
        if (BuildConfig.DEBUG)
            tag?.let {
                Timber.tag(tag).e(messages)
            }.run {
                Timber.e(messages)
            }
    }

    fun toastText(context: Context, toastText: String) {
        Toast.makeText(context,toastText,Toast.LENGTH_LONG).show()
    }
}