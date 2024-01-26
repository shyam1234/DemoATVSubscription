package com.willow.android.mobile.views.pages.tVELoginPage

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import com.adobe.adobepass.accessenabler.api.AccessEnabler
import com.adobe.adobepass.accessenabler.api.utils.AccessEnablerConstants
import com.willow.android.WillowApplication
import com.willow.android.mobile.configs.Keys
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.utils.Utils
import com.willow.android.mobile.views.pages.PagesNavigator
import com.willow.android.mobile.views.pages.matchCenterPage.MatchCenterPageFragment
import com.willow.android.mobile.views.pages.videoDetailPage.VideoDetailPageFragment

interface MessageHandler {
    fun handle(bundle: Bundle?)
}

object TVELoginService {
    var TAG = "TVELoginService"
    private var accessEnabler: AccessEnabler? = null

    private var callingFragment: Fragment? = null
    private var isPlaybackRequest: Boolean = false
    private var contentId: String = ""


    private val messageHandlers = arrayOf(
        object : MessageHandler {
            override fun handle(bundle: Bundle?) {
                if (bundle != null) {
                    handleSetRequestor(bundle)
                }
            }
        },  //  0 SET_REQUESTOR_COMPLETE

        object : MessageHandler {
            override fun handle(bundle: Bundle?) {
                if (bundle != null) {
                    handleSetAuthnStatus(bundle)
                }
            }
        },  //  1 SET_AUTHN_STATUS

        object : MessageHandler {
            override fun handle(bundle: Bundle?) {
                if (bundle != null) {
                    handleSetToken(bundle)
                }
            }
        },  //  2 SET_TOKEN

        object : MessageHandler {
            override fun handle(bundle: Bundle?) {
                if (bundle != null) {
                    handleSetTokenRequestFailed(bundle)
                }
            }
        },  //  3 TOKEN_REQUEST_FAILED

        object : MessageHandler {
            override fun handle(bundle: Bundle?) {
                if (bundle != null) {
                    handleSelectedProvider(bundle)
                }
            }
        },  //  4 SELECTED_PROVIDER

        object : MessageHandler {
            override fun handle(bundle: Bundle?) {
                if (bundle != null) {
                    handleDisplayProviderDialog(bundle)
                }
            }
        },  //  5 DISPLAY_PROVIDER_DIALOG

        object : MessageHandler {
            override fun handle(bundle: Bundle?) {
                if (bundle != null) {
                    handleNavigateToUrl(bundle)
                }
            }
        },

        object : MessageHandler {
            override fun handle(bundle: Bundle?) {
                if (bundle != null) {
                    handleSendTrackingData(bundle)
                }
            }
        },  //  7 SEND_TRACKING_DATA

        object : MessageHandler {
            override fun handle(bundle: Bundle?) {
                if (bundle != null) {
                    handleSetMetadataStatus(bundle)
                }
            }
        },  //  8 SET_METADATA_STATUS

        object : MessageHandler {
            override fun handle(bundle: Bundle?) {
                if (bundle != null) {
                    handlePreauthorizedResources(bundle)
                }
            }
        },

        object : MessageHandler {
            override fun handle(bundle: Bundle?) {
                if (bundle != null) {
                    handleAdvancedStatus(bundle)
                }
            }
        })

    private val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            val bundle = msg.data
            val opCode = bundle.getInt("op_code")
            messageHandlers[opCode].handle(bundle)
        }
    }

    private val delegate = AccessEnablerDelegate(handler)

    fun initializeAccessEnabler(callingFragment: Fragment, context: Context, isPlaybackRequest: Boolean) {
        val swStatement = Keys.tveSoftwareStatement

        TVELoginService.callingFragment = callingFragment
        TVELoginService.isPlaybackRequest = isPlaybackRequest

        if (accessEnabler == null) {
            try {
                WillowApplication.setAccessEnabler(
                    AccessEnabler.Factory.getInstance(
                        context,
                        swStatement,
                        null
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize the AccessEnabler library. " + e.message)
            }

            // configure the AccessEnabler library
            accessEnabler = WillowApplication.getAccessEnablerInstance()
        }

        if (accessEnabler != null) {
            // set the delegate for the AccessEnabler
            AccessEnabler.setDelegate(delegate)
            // update the title bar with the client version
            //setTitle(getResources().getString(R.string.app_name) + " (v" + accessEnabler.getVersion() + ")");
            // Warning: this method should be invoked for testing/development purpose only.
            // The production app SHOULD use only HTTPS (this is the default value).
//            accessEnabler.useHttps(true)
            // request configuration data
            val spUrls = ArrayList<String>()
            spUrls.add(Keys.tveProductionUrl)
            accessEnabler!!.setRequestor(Keys.tveRequestorId, spUrls)
        } else {
            Log.d(TAG, "Failed to configure the AccessEnabler library. ")
            // finish();
        }
    }

    private fun handleAdvancedStatus(bundle: Bundle) {
        val id = bundle.getString("id")
        val level = bundle.getString("level")
        val message = bundle.getString("message")
        val resource = bundle.getString("resource")
        var alertMessage = "errorId: $id\nlevel: $level\nmessage: $message"
        if (resource != null) {
            alertMessage += "\n resource: $resource"
        }
        Log.i("Status", alertMessage)
    }

    private fun handleSetRequestor(bundle: Bundle) {
        // extract the status of the setRequestor() API call
        val status = bundle.getInt("status")
        when (status) {
            AccessEnablerConstants.ACCESS_ENABLER_STATUS_SUCCESS -> {

                // set requestor operation was successful - enable the authN/Z controls
                Log.d("Config phase", "SUCCESS")

                checkAuthenticationForPlayback()
            }
            AccessEnablerConstants.ACCESS_ENABLER_STATUS_ERROR -> {

                // set requestor operation failed - disable the authN/Z controls
                Log.d("Config phase", "FAILED")
            }
            else -> {
                Log.d("Config phase", "setRequestor(): Unknown status code.")
            }
        }
    }

    private fun handleSetAuthnStatus(bundle: Bundle) {
        // extract the status code
        val status = bundle.getInt("status")
        val errCode = bundle.getString("err_code")
        when (status) {
            AccessEnablerConstants.ACCESS_ENABLER_STATUS_SUCCESS -> {
                if (accessEnabler != null) {
                    if (callingFragment != null) {
                        if (callingFragment is TVELoginPageFragment) {
                            accessEnabler!!.getAuthorization(Keys.tveRequestorId)
                        } else {
                            if (UserModel.isTVEProviderSpectrum()) {
                                val playbackContentId = Utils.getTVEPlaybackEncryptedContentId(
                                    contentId
                                )
                                val contentIdFeed = "<rss version=\"2.0\"><channel><title>WILLOW</title><item><title>" + playbackContentId + "</title></item></channel></rss>"
                                accessEnabler!!.getAuthorization(contentIdFeed)
                            } else {
                                accessEnabler!!.getAuthorization(Keys.tveRequestorId)
                            }
                        }
                    }
                }
                Log.d("Authentication", "SUCCESS")
            }
            AccessEnablerConstants.ACCESS_ENABLER_STATUS_ERROR -> {
                if (errCode == AccessEnablerConstants.USER_NOT_AUTHENTICATED_ERROR) {
                    accessEnabler!!.getAuthentication()
                }
                Log.d("Authentication", "FAILED\n$errCode")
            }
            else -> {
                Log.d("Authentication", "setAuthnStatus(): Unknown status code.")
            }
        }
    }

    private fun handleSetToken(bundle: Bundle) {
        // extract the token and resource ID
        val resourceId = bundle.getString("resource_id")
        val token = bundle.getString("token")
        var error = ""
        if (token == null || token.trim { it <= ' ' }.length == 0) {
            error = "empty token"
        } else {
            try {
                Log.i("handleSetToken", token)
                if (callingFragment != null) {
                    if (callingFragment is TVELoginPageFragment) {
                        (callingFragment as TVELoginPageFragment).verifyTVEUserFromWillow(token)
                    } else if (callingFragment is VideoDetailPageFragment) {
                        (callingFragment as VideoDetailPageFragment).makeTVEStreamRequest(token)
                    } else if (callingFragment is MatchCenterPageFragment) {
                        (callingFragment as MatchCenterPageFragment).makeTVEStreamRequest(token)
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message!!)
                error = "token validation process interrupted"
            }
        }
        Log.d(TAG, resourceId!!)
        Log.d(TAG, "Token: $token")
    }

    private fun handleSetTokenRequestFailed(bundle: Bundle) {
        // extract the error details and resource ID
        val resourceId = bundle.getString("resource_id")
        val errorCode = bundle.getString("err_code")
        val errorDescription = bundle.getString("err_description")
//        ShowLoginFailedMessage(errorCode, errorDescription)
        if ((callingFragment != null) && (errorDescription != null) ) {
            callingFragment!!.activity?.let {
                PagesNavigator.showPopupMessage(it, errorDescription)
                logout()
            }
        }
    }

    private fun ShowLoginFailedMessage(errorCode: String?, errorDescription: String?) {
        Log.i("ShowLoginFailedMessage", errorDescription.toString())
    }

    private fun handleSelectedProvider(bundle: Bundle) {}
    private fun handleNavigateToUrl(bundle: Bundle) {}
    private fun handleDisplayProviderDialog(bundle: Bundle) {}
    private fun handleSendTrackingData(bundle: Bundle) {}
    private fun handleSetMetadataStatus(bundle: Bundle) {}
    private fun handlePreauthorizedResources(bundle: Bundle) {}


    fun initProviderLoginProcess(providerName: String ) {
        accessEnabler!!.setSelectedProvider(providerName)
        accessEnabler!!.getAuthentication()
    }

    // ********************* Playback Related methods *************************
    fun initPlayback(contentId: String, callingFragment: Fragment, context: Context) {
        isPlaybackRequest = true
        TVELoginService.contentId = contentId
        initializeAccessEnabler(callingFragment, context, isPlaybackRequest)
    }

    fun checkAuthenticationForPlayback() {
        if (accessEnabler != null) {
            accessEnabler!!.checkAuthentication()
        }
    }

    // *************** Logout *******************
    fun logout() {
        if (accessEnabler != null) {
            accessEnabler!!.logout()
        }
    }
}