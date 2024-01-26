package com.willow.android.mobile.views.pages.wiPlayer

import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.multidex.MultiDex
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.MEDIA_ITEM_TRANSITION_REASON_AUTO
import com.google.android.exoplayer2.ext.cast.CastPlayer
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.R
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import com.willow.android.databinding.WiPlayerFragmentBinding
import com.willow.android.mobile.configs.AdConfig
import com.willow.android.mobile.configs.WiConfig
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.models.video.VideoModel
import com.willow.android.mobile.services.analytics.AnalyticsService
import com.willow.android.mobile.views.pages.videoDetailPage.VideoDetailPlaylistInterface
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy


interface PlayerFullscreenInterface {
    fun showFullscreenPlayer()
    fun showEmbeddedPlayer()
}

interface LivePlayerInterface {
    fun reloadLiveStream()
}

class WiPlayerFragment : Fragment(), Player.Listener, SessionAvailabilityListener {
    val STREAMS_MIME_TYPE = MimeTypes.APPLICATION_M3U8
    // Player STATES
    private var isPlayerFullscreen = false
    private var isPlayingInCastPlayer = false

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        WiPlayerFragmentBinding.inflate(layoutInflater)
    }

    private lateinit var currentVideoModel: VideoModel
    private lateinit var currentMediaItem: MediaItem
    private var clipsList: List<VideoModel> = mutableListOf() // If This is Playlist then it should contain the value of all playlist items.

    private lateinit var localPlayer: ExoPlayer //Player on Mobile Devices
    private lateinit var playerView: PlayerView //View of The Local Player
    private lateinit var adsLoader: ImaAdsLoader //Ads Loader

    private var castPlayer: CastPlayer? = null //Player on Cast Supported TV
    private var castContext: CastContext? = null

    private var playerListener: Player.Listener? = null

    var videoDetailPlaylistInterface: VideoDetailPlaylistInterface? = null
    var playerFullscreenInterface: PlayerFullscreenInterface? = null
    var livePlayerInterface: LivePlayerInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showBelowCutoutInMobile()

        initializePlayer()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkInitialScreenOrientation()
    }

    // Listening to the orientation config
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            makePlayerFullScreen()
            disableFullScreenButton()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            embedPlayerInsideView()
            enableFullScreenButton()
        }
    }

    private fun checkInitialScreenOrientation() {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            makePlayerFullScreen()
            disableFullScreenButton()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onStop() {
        super.onStop()
        pausePlayer()
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
        activity?.finish()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return viewBinding.root
    }

    private fun initializePlayer() {
        handleCookies()
        context?.let {
            MultiDex.install(it)
        }

        // Init Player View
        playerView = viewBinding.videoView
        playerView.setBackgroundColor(Color.BLACK)
        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
        playerView.setControllerHideDuringAds(false)
        playerView.controllerAutoShow = false
        playerView.useController = true

        // Init LocalPlayer & AdsLoader
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSource.Factory(requireContext())

        adsLoader = ImaAdsLoader.Builder(requireContext()).build()
        val mediaSourceFactory: MediaSource.Factory = DefaultMediaSourceFactory(dataSourceFactory)
            .setAdsLoaderProvider { unusedAdTagUri: MediaItem.AdsConfiguration? -> adsLoader }
            .setAdViewProvider(playerView)


        localPlayer = ExoPlayer.Builder(requireContext()).setMediaSourceFactory(mediaSourceFactory).build()
        playerView.setPlayer(localPlayer)
        adsLoader.setPlayer(localPlayer)

        initCastPlayer()
        setupPlayerButtonEvents()
    }

    private fun releasePlayer() {
        adsLoader.setPlayer(null)
        playerView.player = null
        adsLoader.release()
        localPlayer.release()
        if (playerListener != null) {
            localPlayer.removeListener(playerListener!!)
        }
    }


    fun pausePlayer() {
        localPlayer.pause()
    }

    fun reloadLiveStream(videoModel: VideoModel) {
        val contentUri = Uri.parse(videoModel.streamUrl)
        val videoMetadata = MediaMetadata.Builder()
            .setArtworkUri(Uri.parse(videoModel.imageUrl))
            .setTitle(videoModel.duration)
            .setSubtitle(videoModel.title)
            .build()

        currentMediaItem = MediaItem.Builder().setTag(videoModel.title).setUri(contentUri).setMimeType(STREAMS_MIME_TYPE).setMediaMetadata(videoMetadata).build()
        choosePlayerForPlayLaunch()
    }

    fun setMediaItem(videoModel: VideoModel) {
        currentVideoModel = videoModel

        playerView.adViewGroup.removeAllViews()
        AnalyticsService.sendPlayEvent("PLAYER_SCREEN", videoModel)

        if (videoModel.isLive) {
            hideControlsForLivePlayer()
        }

        val contentUri = Uri.parse(videoModel.streamUrl)
        val adTagUri = Uri.parse(WiConfig.dfpTag)
        val adsConfiguration = MediaItem.AdsConfiguration.Builder(adTagUri).build()

        val videoMetadata = MediaMetadata.Builder()
            .setArtworkUri(Uri.parse(videoModel.imageUrl))
            .setTitle(videoModel.duration)
            .setSubtitle(videoModel.title)
            .build()

        if (shouldShowAds(videoModel) == true) {
            currentMediaItem = MediaItem.Builder().setTag(videoModel.title).setUri(contentUri).setMimeType(STREAMS_MIME_TYPE).setMediaMetadata(videoMetadata).setAdsConfiguration(adsConfiguration).build()
        } else {
            currentMediaItem = MediaItem.Builder().setTag(videoModel.title).setUri(contentUri).setMimeType(STREAMS_MIME_TYPE).setMediaMetadata(videoMetadata).build()
        }

        choosePlayerForPlayLaunch()

        if (videoModel.isLive) {
            initLivePlayerListener(currentMediaItem)
        }
    }

    fun setPlaylistItems(clipsList: List<VideoModel>) {

        this.clipsList = clipsList
        playerView.adViewGroup.removeAllViews()

        val adTagUri = Uri.parse(WiConfig.dfpTag)
        val adsConfiguration = MediaItem.AdsConfiguration.Builder(adTagUri).build()

        if (clipsList.isNotEmpty() && (shouldShowAds(clipsList[0]) == true)) {
            val contentUri = Uri.parse(clipsList[0].streamUrl)
            currentVideoModel = clipsList[0]
            currentMediaItem = MediaItem.Builder().setUri(contentUri).setMimeType(STREAMS_MIME_TYPE).setAdsConfiguration(adsConfiguration).build()
            localPlayer.addMediaItem(currentMediaItem)

            for (i in 1 until clipsList.size) {
                val itemUri = Uri.parse(clipsList[i].streamUrl)
                currentVideoModel = clipsList[i]
                currentMediaItem = MediaItem.Builder().setTag(clipsList[i].title).setUri(itemUri).setMimeType(STREAMS_MIME_TYPE).build()
                localPlayer.addMediaItem(currentMediaItem)
            }
        } else {
            for (i in 0 until clipsList.size) {
                val itemUri = Uri.parse(clipsList[i].streamUrl)
                currentVideoModel = clipsList[i]
                currentMediaItem = MediaItem.Builder().setTag(clipsList[i].title).setUri(itemUri).setMimeType(STREAMS_MIME_TYPE).build()
                localPlayer.addMediaItem(currentMediaItem)
            }
        }

        initPlaylistListener()

        localPlayer.prepare()
        localPlayer.playWhenReady = true
    }

    fun playSelectedPlaylistItem(videoModel: VideoModel) {
        for (i in 0 until clipsList.size) {
            if (videoModel.contentId.equals(clipsList[i].contentId)) {
                localPlayer.seekTo(i, 0)
            }
        }
    }

    private fun initPlaylistListener() {
        playerListener = object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)

                if (reason.equals(MEDIA_ITEM_TRANSITION_REASON_AUTO)) {
                    var title = ""
                    val tag = mediaItem?.localConfiguration?.tag
                    if ( tag != null) { title = tag.toString() }
                    videoDetailPlaylistInterface?.highlightNextVideo(title)
                }
            }
        }

        localPlayer.addListener(playerListener!!)
    }

    private fun initLivePlayerListener(currentMediaItem: MediaItem) {
        playerListener = object : Player.Listener {

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Log.e("LIVE LISTENER", "Player got error")
                livePlayerInterface?.reloadLiveStream()
            }
        }

        localPlayer.addListener(playerListener!!)
    }

    private fun enableFullScreenButton() {
        playerView.findViewById<ImageButton>(R.id.exo_fullscreen)?.visibility = View.VISIBLE
    }

    private fun disableFullScreenButton() {
        playerView.findViewById<ImageButton>(R.id.exo_fullscreen)?.visibility = View.INVISIBLE
    }

    private fun toggleFullscreen() {
        isPlayerFullscreen = !isPlayerFullscreen

        if (isPlayerFullscreen) {
            makePlayerFullScreen()
        } else {
            embedPlayerInsideView()
        }
    }

    private fun makePlayerFullScreen() {
        isPlayerFullscreen = true
        hideStatusBar()

        // Change Icon
        playerView.findViewById<ImageButton>(R.id.exo_fullscreen).setImageResource(com.willow.android.R.drawable.player_fullscreen_min)
        playerFullscreenInterface?.showFullscreenPlayer()
    }

    private fun embedPlayerInsideView() {
        isPlayerFullscreen = false
        showStatusBar()

        playerView.findViewById<ImageButton>(R.id.exo_fullscreen).setImageResource(com.willow.android.R.drawable.player_fullscreen)
        playerFullscreenInterface?.showEmbeddedPlayer()
    }

    private fun hideStatusBar() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        WindowInsetsControllerCompat(requireActivity().window, viewBinding.videoView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.let {
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                it.hide(WindowInsets.Type.systemBars())
            }
        } else {
            requireActivity().window.decorView.apply {
                systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
            }
        }
         */
    }

    private fun showStatusBar() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
        WindowInsetsControllerCompat(requireActivity().window, viewBinding.videoView).let { controller ->
            controller.show(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(false)
            requireActivity().window.insetsController?.show(WindowInsets.Type.systemBars())
        } else {
            requireActivity().window.decorView.apply {
                systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
         */
    }

    private fun showBelowCutoutInMobile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            requireActivity().window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        }
    }


    private fun hideControlsForLivePlayer() {
        playerView.findViewById<ImageButton>(R.id.exo_ffwd)?.imageAlpha = 0
        playerView.findViewById<ImageButton>(R.id.exo_rew)?.imageAlpha = 0
        playerView.findViewById<DefaultTimeBar>(R.id.exo_progress)?.visibility = View.GONE
        playerView.findViewById<TextView>(R.id.exo_position)?.visibility = View.GONE
        playerView.findViewById<TextView>(R.id.exo_duration)?.visibility = View.GONE
    }


    fun handleCookies() {
        val DEFAULT_COOKIE_MANAGER = CookieManager()
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)

        val client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(DEFAULT_COOKIE_MANAGER))
            .build()

        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER)
        {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
    }

    private fun initCastPlayer() {
        castContext = CastContext.getSharedInstance()
        if (castContext != null) {
            castPlayer = CastPlayer(castContext!!)
            castPlayer?.addListener(this)
            castPlayer?.setSessionAvailabilityListener(this)

            drawCastButton()
        } else {
            Log.e("CAST_ERROR", "Cast Context is null")
        }
    }

    private fun drawCastButton() {
        CastButtonFactory.setUpMediaRouteButton(requireActivity(), playerView.findViewById(com.willow.android.R.id.cast_media_route_button))
    }

    override fun onCastSessionAvailable() {
        if (castPlayer != null) {
//            setCurrentPlayer(castPlayer!!)
            launchPlayInCastPlayer()
        }
    }

    override fun onCastSessionUnavailable() {
        if (localPlayer != null) {
//            setCurrentPlayer(localPlayer)
            launchPlayInLocalPlayer()
        }
    }

    private fun choosePlayerForPlayLaunch() {
        if (castPlayer != null && castPlayer!!.isCastSessionAvailable) {
            launchPlayInCastPlayer()
        } else {
            launchPlayInLocalPlayer()
        }
    }

    private fun launchPlayInCastPlayer() {
        pausePlayer()
        viewBinding.castOverlay.root.visibility = View.VISIBLE

        isPlayingInCastPlayer = true

        castPlayer?.setMediaItem(currentMediaItem)
        castPlayer?.prepare()
        castPlayer?.playWhenReady = true
    }

    private fun launchPlayInLocalPlayer() {
        isPlayingInCastPlayer = false

        localPlayer.setMediaItem(currentMediaItem)
        localPlayer.prepare()
        localPlayer.playWhenReady = true
    }

    /*
    private fun setCurrentPlayer(currentPlayer: Player) {
        if (this.localPlayer === currentPlayer) {
            return
        }

        playerView.setPlayer(currentPlayer)
        playerView.setControllerHideOnTouch(currentPlayer === localPlayer)
        if (currentPlayer === castPlayer) {
            playerView.setControllerShowTimeoutMs(0)
            playerView.showController()
            playerView.setDefaultArtwork(
                ResourcesCompat.getDrawable(
                    requireContext().resources,
                    R.drawable.ct_volume_on,  /* theme= */
                    null
                )
            )
        } else { // currentPlayer == localPlayer
            playerView.setControllerShowTimeoutMs(StyledPlayerControlView.DEFAULT_SHOW_TIMEOUT_MS)
            playerView.setDefaultArtwork(null)
        }

        // Player state management.
        var playbackPositionMs = C.TIME_UNSET
        var currentItemIndex = C.INDEX_UNSET
        var playWhenReady = false
        val previousPlayer: Player = currentPlayer
        if (previousPlayer != null) {
            // Save state from the previous player.
            val playbackState = previousPlayer.playbackState
            if (playbackState != Player.STATE_ENDED) {
                playbackPositionMs = previousPlayer.currentPosition
                playWhenReady = previousPlayer.playWhenReady
//                currentItemIndex = previousPlayer.getCurrentMediaItemIndex()
                if (currentItemIndex != currentItemIndex) {
                    playbackPositionMs = C.TIME_UNSET
                    currentItemIndex = currentItemIndex
                }
            }
            previousPlayer.stop()
            previousPlayer.clearMediaItems()
        }

//        this.localPlayer = currentPlayer

        // Media queue management.
//        currentPlayer.setMediaItems(mediaQueue, currentItemIndex, playbackPositionMs)
        currentPlayer.playWhenReady = playWhenReady
        currentPlayer.prepare()
    }
     */

    private fun setupPlayerButtonEvents() {
        val playerCloseButton = playerView.findViewById<ImageButton>(com.willow.android.R.id.player_close_button)
        playerCloseButton.setOnClickListener {
            activity?.finish()
        }

        val playerFullScreenButton = playerView.findViewById<ImageButton>(R.id.exo_fullscreen)
        playerFullScreenButton.setOnClickListener {
            toggleFullscreen()
        }
    }

    // Logic Specific to Willow Videos /***********************************************************************************

    // This Should be used to check whether to play ads or not. Rest are helper private funtions only.
    private fun shouldShowAds(videoModel: VideoModel): Boolean? {
        return if (videoModel.isLive) {
            shouldShowAdForLive()
        } else {
            shouldShowAdForVod()
        }
    }

    /** For Live  */
    private fun shouldShowAdForLive(): Boolean? {
        return try {
            val mainConfigForLive: Boolean =
                AdConfig.mainConfig.getBoolean("enable_ads_for_live")
            if (!mainConfigForLive) {
                false
            } else shouldShowAdForLiveAsPerUserConfig()
        } catch (e: Exception) {
            // Main Config is not available
            Log.e("AdConfig: ", "enable_ads_for_live main_config missing")
            AdConfig.defaultShowAdForLive
        }
    }

    private fun shouldShowAdForLiveAsPerUserConfig(): Boolean? {
        return try {
            val userConfigForLive: JSONObject =
                AdConfig.userConfig.getJSONObject(UserModel.ads_category)
            try {
                userConfigForLive.getBoolean("enable_ads_for_live")
            } catch (e: Exception) {
                Log.e(
                    "AdConfig: ",
                    "user_category enableAdsForLive missing from Ad Config"
                )
                AdConfig.defaultShowAdForLive
            }
        } catch (e: Exception) {
            Log.e("AdConfig: ", "user_category missing from Ad Config")
            AdConfig.defaultShowAdForLive
        }
    }


    /** For Vod  */
    private fun shouldShowAdForVod(): Boolean? {
        return try {
            val mainConfigForVod: Boolean =
                AdConfig.mainConfig.getBoolean("enable_ads_for_vod")
            if (!mainConfigForVod) {
                false
            } else shouldShowAdForVodAsPerUserConfig()
        } catch (e: Exception) {
            // Main Config is not available
            Log.e("AdConfig: ", "enable_ads_for_vod main_config missing")
            AdConfig.defaultShowAdForVod
        }
    }

    private fun shouldShowAdForVodAsPerUserConfig(): Boolean? {
        return try {
            val userConfigForVod: JSONObject =
                AdConfig.userConfig.getJSONObject(UserModel.ads_category)
            try {
                userConfigForVod.getBoolean("enable_ads_for_vod")
            } catch (e: Exception) {
                Log.e("AdConfig: ", "user_category enableAdsForVod missing from Ad Config")
                AdConfig.defaultShowAdForVod
            }
        } catch (e: Exception) {
            Log.e("AdConfig: ", "user_category missing from Ad Config")
            AdConfig.defaultShowAdForVod
        }
    }
}