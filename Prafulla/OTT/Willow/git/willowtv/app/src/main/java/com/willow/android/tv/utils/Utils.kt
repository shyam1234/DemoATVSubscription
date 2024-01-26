package com.willow.android.tv.utils

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.widget.LinearLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.willow.android.BuildConfig
import timber.log.Timber

object Utils {

    fun readJsonFromFile(application: Application, rawID: Int): String {
        val inputStream = application.resources.openRawResource(rawID)
        return inputStream.bufferedReader().use {
            it.readText()
        }
    }

    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

     fun updateIndicator(
        context: Context?,
        dots_scrollbar_holder: LinearLayout,
        mCurrentPage: Int,
        totalNumberOfPages: Int
    ) {
        dots_scrollbar_holder.removeAllViews()
        DotsScrollBar.createDotScrollBar(
            context,
            dots_scrollbar_holder,
            mCurrentPage,
            totalNumberOfPages
        )
    }

    fun memoryLogs() {
        System.gc()
        if (BuildConfig.DEBUG) {
            val runtime = Runtime.getRuntime()
            val maxMemory = runtime.maxMemory()
            val allocatedMemory = runtime.totalMemory()
            val freeMemory = runtime.freeMemory()

            val usedMemory = allocatedMemory - freeMemory

            Timber.d("MemoryLog Max Memory: ${maxMemory / (1024 * 1024)} MB")
            Timber.d("MemoryLog Allocated Memory: ${allocatedMemory / (1024 * 1024)} MB")
            Timber.d("MemoryLog Used Memory: ${usedMemory / (1024 * 1024)} MB")
            Timber.d("MemoryLog Free Memory: ${freeMemory / (1024 * 1024)} MB")
            Timber.d("MemoryLog ------------------------------------------------")
            FirebaseCrashlytics.getInstance().log("MemoryLog Max Memory: ${maxMemory / (1024 * 1024)} MB " +
                    "\nMemoryLog Allocated Memory: ${allocatedMemory / (1024 * 1024)} MB" +
                    "\nMemoryLog Used Memory: ${usedMemory / (1024 * 1024)} MB" +
                    "\nMemoryLog Free Memory: ${freeMemory / (1024 * 1024)} MB")
        }
    }

}