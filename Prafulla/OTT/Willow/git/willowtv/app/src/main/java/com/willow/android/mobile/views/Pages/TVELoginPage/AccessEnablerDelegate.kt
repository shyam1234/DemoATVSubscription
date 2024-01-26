package com.willow.android.mobile.views.pages.tVELoginPage

import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.adobe.adobepass.accessenabler.api.IAccessEnablerDelegate
import com.adobe.adobepass.accessenabler.api.callback.model.AdvancedStatus
import com.adobe.adobepass.accessenabler.models.Event
import com.adobe.adobepass.accessenabler.models.MetadataKey
import com.adobe.adobepass.accessenabler.models.MetadataStatus
import com.adobe.adobepass.accessenabler.models.Mvpd
import com.adobe.adobepass.accessenabler.utils.Utils

class AccessEnablerDelegate(private val handler: Handler) :
    IAccessEnablerDelegate {
    val SET_REQUESTOR_COMPLETE = 0
    val SET_AUTHN_STATUS = 1
    val SET_TOKEN = 2
    val TOKEN_REQUEST_FAILED = 3
    val SELECTED_PROVIDER = 4
    val DISPLAY_PROVIDER_DIALOG = 5
    val NAVIGATE_TO_URL = 6
    val SEND_TRACKING_DATA = 7
    val SET_METADATA_STATUS = 8
    private val PREAUTHORIZED_RESOURCES = 9
    private val ADVANCED_STATUS = 10

    private val serializedMVPDListSingleton: SerializedMVPDListSingleton =
        SerializedMVPDListSingleton.getInstance()!!

    private fun createMessagePayload(opCode: Int, message: String?): Bundle {
        val bundle = Bundle()
        bundle.putInt("op_code", opCode)
        if (message != null) {
            bundle.putString("message", message)
        }
        return bundle
    }

    override fun setRequestorComplete(status: Int) {
        val message = "setRequestorComplete($status)"
        Log.i(LOG_TAG, message)

        // signal the fact that the AccessEnabler work is done
        val msg = handler.obtainMessage()
        val bundle = createMessagePayload(SET_REQUESTOR_COMPLETE, message)
        bundle.putInt("status", status)
        msg.data = bundle
        handler.sendMessage(msg)
    }

    override fun setAuthenticationStatus(status: Int, errorCode: String) {
        val message = "setAuthenticationStatus($status, $errorCode)"
        Log.i(LOG_TAG, message)

        // signal the fact that the AccessEnabler work is done
        val msg = handler.obtainMessage()
        val bundle = createMessagePayload(SET_AUTHN_STATUS, message)
        bundle.putInt("status", status)
        bundle.putString("err_code", errorCode)
        msg.data = bundle
        handler.sendMessage(msg)
    }

    override fun status(advancedStatus: AdvancedStatus) {
        val status =
            "status(" + advancedStatus.id + ", " + advancedStatus.level + ", " + advancedStatus.message + ", " + advancedStatus.resource + ")"
        Log.i(LOG_TAG, status)

        // signal the fact that the AccessEnabler work is done
        val msg = handler.obtainMessage()
        val bundle = createMessagePayload(ADVANCED_STATUS, status)
        bundle.putString("id", advancedStatus.id)
        bundle.putString("level", advancedStatus.level)
        bundle.putString("message", advancedStatus.message)
        bundle.putString("resource", advancedStatus.resource)
        msg.data = bundle
        handler.sendMessage(msg)
    }

    override fun setToken(token: String, resourceId: String) {
        val message = "setToken($token, $resourceId)"
        Log.i(LOG_TAG, message)

        // signal the fact that the AccessEnabler work is done
        val msg = handler.obtainMessage()
        val bundle = createMessagePayload(SET_TOKEN, message)
        bundle.putString("resource_id", resourceId)
        bundle.putString("token", token)
        msg.data = bundle
        handler.sendMessage(msg)
    }

    override fun tokenRequestFailed(
        resourceId: String,
        errorCode: String,
        errorDescription: String
    ) {
        val message =
            "tokenRequestFailed($resourceId, $errorCode, $errorDescription)"
        Log.i(LOG_TAG, message)

        // signal the fact that the AccessEnabler work is done
        val msg = handler.obtainMessage()
        val bundle = createMessagePayload(TOKEN_REQUEST_FAILED, message)
        bundle.putString("resource_id", resourceId)
        bundle.putString("err_code", errorCode)
        bundle.putString("err_description", errorDescription)
        msg.data = bundle
        handler.sendMessage(msg)
    }

    override fun selectedProvider(mvpd: Mvpd) {
        val message: String
        message = if (mvpd != null) {
            "selectedProvider(" + mvpd.id + ")"
        } else {
            "selectedProvider(null)"
        }
        Log.i(LOG_TAG, message)

        // signal the fact that the AccessEnabler work is done
        val msg = handler.obtainMessage()
        val bundle = createMessagePayload(SELECTED_PROVIDER, message)
        bundle.putString("mvpd_id", if (mvpd == null) null else mvpd.id)
        msg.data = bundle
        handler.sendMessage(msg)
    }

    override fun displayProviderDialog(mvpds: ArrayList<Mvpd>?) {
        val message = "displayProviderDialog(" + mvpds!!.size + " mvpds)"
        Log.i(LOG_TAG, message)

        // signal the fact that the AccessEnabler work is done
        val msg = handler.obtainMessage()
        val bundle = createMessagePayload(DISPLAY_PROVIDER_DIALOG, message)
        serializedMVPDListSingleton.setMvpdsList(mvpds)
        msg.data = bundle
        handler.sendMessage(msg)
    }

    override fun preauthorizedResources(resources: ArrayList<String>) {
        val message = "preauthorizedResources(" + Utils.joinStrings(resources, ", ") + ")"
        Log.i(LOG_TAG, message)

        // signal the fact that the AccessEnabler work is done
        val msg = handler.obtainMessage()
        val bundle = createMessagePayload(PREAUTHORIZED_RESOURCES, message)
        bundle.putStringArrayList("resources", resources)
        msg.data = bundle
        handler.sendMessage(msg)
    }


    override fun navigateToUrl(url: String) {
        val message = "navigateToUrl: " + url
        Log.i(LOG_TAG, message)

        // signal the fact that the AccessEnabler work is done
        val msg = handler.obtainMessage()
        val bundle = createMessagePayload(NAVIGATE_TO_URL, message)
        bundle.putString("url", url)
        msg.data = bundle
        handler.sendMessage(msg)
    }

    override fun sendTrackingData(event: Event, data: ArrayList<String>) {
        val message = "sendTrackingData(" + Utils.joinStrings(data, "|") + ", " + event.type + ")"
        Log.i(LOG_TAG, message)

        // signal the fact that the AccessEnabler work is done
        val msg = handler.obtainMessage()
        val bundle = createMessagePayload(SEND_TRACKING_DATA, message)
        bundle.putInt("event_type", event.type)
        bundle.putStringArrayList("event_data", data)
        msg.data = bundle
        handler.sendMessage(msg)
    }

    override fun setMetadataStatus(key: MetadataKey, result: MetadataStatus) {
        val message = "setMetadataStatus(" + key.key + ", " + result + ")"
        Log.i(LOG_TAG, message)

        // signal the fact that the AccessEnabler work is done
        val msg = handler.obtainMessage()
        val bundle = createMessagePayload(SET_METADATA_STATUS, message)
        bundle.putSerializable("key", key)
        bundle.putSerializable("result", result)
        msg.data = bundle
        handler.sendMessage(msg)
    }

    companion object {
        private const val LOG_TAG = "AccessEnablerDelegate"
    }
}

