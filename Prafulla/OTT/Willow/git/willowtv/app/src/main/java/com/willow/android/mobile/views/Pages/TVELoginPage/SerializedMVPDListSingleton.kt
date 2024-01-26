package com.willow.android.mobile.views.pages.tVELoginPage

import com.adobe.adobepass.accessenabler.models.Mvpd


class SerializedMVPDListSingleton private constructor() {
    var mvpdsList: ArrayList<Mvpd>? = null;

    companion object {
        private var instance: SerializedMVPDListSingleton? = null

        @JvmName("getInstance1")
        fun getInstance(): SerializedMVPDListSingleton? {
            if (instance == null) {
                instance = SerializedMVPDListSingleton()
            }
            return instance
        }
    }

    @JvmName("setMvpdsList1")
    fun setMvpdsList(mvpdsListSerialized: ArrayList<Mvpd>?) {
        this.mvpdsList = mvpdsListSerialized
    }

    @JvmName("getMvpdsList1")
    fun getMvpdsList(): ArrayList<Mvpd>? {
        return this.mvpdsList
    }
}