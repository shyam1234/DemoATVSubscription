package com.willow.android.mobile.views.pages.homePage

import android.net.Uri

object FirebaseDeeplinkState {
    /**
     * Set the value whenever any new link is received from deep link
     * Reset it once the respective activity has been launched
     */
    var receivedUri: Uri? = null
}