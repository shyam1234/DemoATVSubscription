package com.willow.android.mobile.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.component1
import com.google.firebase.dynamiclinks.ktx.component2
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.ktx.Firebase
import com.willow.android.mobile.configs.Keys
import com.willow.android.mobile.configs.WiConfig
import com.willow.android.mobile.models.video.VideoModel


object DeeplinkService {
    fun generateShareUrl(context: Context, videoModel: VideoModel) {
        val shareFallbackUrl = WiConfig.deeplinkFallbackUrl

        val shortLinkTask = Firebase.dynamicLinks.shortLinkAsync {
            val videoParams = getVideoShareParams(videoModel)
            val urlString = WiConfig.deeplinkDomain + videoModel.slugUrl + "?" + videoParams

            link = Uri.parse(urlString)
            domainUriPrefix = WiConfig.deeplinkDomainUriPrefix

            androidParameters(Keys.androidPlayStoreId) {
                minimumVersion = Keys.androidPlayStoreMinVersion
                fallbackUrl = Uri.parse(shareFallbackUrl)
            }

            iosParameters(Keys.appStoreBundleId) {
                appStoreId = Keys.appStoreId
                minimumVersion = Keys.appStoreMinVersion
                setFallbackUrl(Uri.parse(shareFallbackUrl))
                ipadFallbackUrl = Uri.parse(shareFallbackUrl)
            }

            socialMetaTagParameters {
                title = videoModel.title
                imageUrl = Uri.parse(videoModel.imageUrl)
            }
        }.addOnSuccessListener { (shortLink, flowchartLink) ->
            // Short link created
            processShortLink(context, videoModel, shortLink.toString())
        }.addOnFailureListener {
            Log.e("FirebaseError", it.localizedMessage)
        }
    }

    fun processShortLink(context: Context, videoModel: VideoModel, shortLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"

        shareIntent.putExtra(Intent.EXTRA_TEXT, shortLink)
        context.startActivity(Intent.createChooser(shareIntent, videoModel.title))
    }

    private fun getVideoShareParams(videoModel: VideoModel): String {
        var params = "content_type=" + videoModel.contentType + "&match_id=" + videoModel.matchId

        when (videoModel.contentType.lowercase().trim()) {
            "live" -> {
                val liveSpecificParams = getLiveShareParams(videoModel)
                params = params + liveSpecificParams
            }

            "highlight" -> {
                val vodSpecificParams = getVODShareParams(videoModel)
                params = params + vodSpecificParams
            }

            "replay" -> {
                val vodSpecificParams = getVODShareParams(videoModel)
                params = params + vodSpecificParams
            }

            "clip" -> {
                val clipSpecificParams = getClipsShareParams(videoModel)
                params = params + clipSpecificParams
            }
        }

        return params
    }

    private fun getLiveShareParams(videoModel: VideoModel): String {
        return "&series_id=" + videoModel.seriesId
    }

    private fun getVODShareParams(videoModel: VideoModel): String {
        return "&slug=" + videoModel.slugWithoutDomain + "&duration=" + videoModel.durationSecondsString
    }

    private fun getClipsShareParams(videoModel: VideoModel): String {
        return "&series_id=" + videoModel.seriesId + "&clip_id=" + videoModel.clipId
    }
}