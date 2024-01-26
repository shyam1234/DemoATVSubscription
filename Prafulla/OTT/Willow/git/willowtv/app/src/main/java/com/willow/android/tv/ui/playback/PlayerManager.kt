package com.willow.android.tv.ui.playback

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.deficiency.ErrorEvent
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.event.SourceEvent
import com.bitmovin.player.api.event.on
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.ui.ScalingMode
import com.bitmovin.player.api.ui.StyleConfig
import com.willow.android.tv.utils.GlobalConstants
import timber.log.Timber

class PlayerManager {
    private var runnableForPlayingContent: Runnable? = null
    private var handlerForPlayingContent: Handler? = null
    private var context: Context?= null
    private var playerView: PlayerView? = null
    private var url: String = ""
    private var listener: IPlayerStatus? = null

    companion object{
        var shouldPlayContent: Boolean = true
    }
    fun initPlayer(context: Context?, playerView: PlayerView?,url: String,listener: IPlayerStatus){
        this.context = context
        this.playerView = playerView
        this.url = url
        this.listener = listener
        handlerForPlayingContent = Handler(Looper.myLooper()!!)
    }
    init {
        listener?.onPrepare()
    }

    fun playContent() {
        context?.let {
            initializePlayer(it)
        }
    }

    fun getContentDuration(): Long?{
        context?.let {
            return  (playerView?.player?.duration?.times(1000))?.toLong()
        }
        return null
    }

    private fun initializePlayer(context: Context) {
        // Create a new PlayerConfig containing a StyleConfig with disabled UI
        val playerConfig = PlayerConfig(styleConfig = StyleConfig(isUiEnabled = false, scalingMode = ScalingMode.Stretch))
        playerView?.player = null
        val player = Player.create(context, playerConfig).also { playerView?.player = it }

        //load url
        if(!TextUtils.isEmpty(url)) {
            playerView?.player?.load(SourceConfig.fromUrl(url))
        }
        addEventListener()
        playMediaWithDelay(GlobalConstants.DELAY_IN_VIDEO_TRAILER_PLAYBACK)
    }

    private fun playMediaWithDelay(delayInMillis: Long) {
        if (runnableForPlayingContent == null) {
            runnableForPlayingContent = Runnable {
                if ( playerView?.player?.isPlaying == false) {
                    playerView?.player?.play()
                }
            }
        }
        runnableForPlayingContent?.let {
            handlerForPlayingContent?.postDelayed(it, delayInMillis)
        }
    }

    private fun onPlayerEvent(event: PlayerEvent) {
        Timber.d("playerManager >> event $event")
        when (event) {
            is PlayerEvent.Play -> {
                listener?.onPlay()
            }
            is PlayerEvent.Destroy,
            is PlayerEvent.Paused,
            is PlayerEvent.PlaybackFinished -> {
                listener?.onEnd()
            }
            is PlayerEvent.Playing,
            is PlayerEvent.TimeChanged ->{
                Timber.d("playerManager >> shouldPlayContent :: $shouldPlayContent ")
                if(!shouldPlayContent){
                    if( playerView?.player?.isPlaying == true){
                        playerView?.player?.onPause()
                        playerView?.player?.onStop()
                    }
                    shouldPlayContent = true
                }
            }
            else -> {
            }
        }
    }

    private fun addEventListener() {
        playerView?.player?.on<PlayerEvent.Error>(::onErrorEvent)
        playerView?.player?.on<SourceEvent.Error>(::onErrorEvent)
        playerView?.player?.on<PlayerEvent.Play>(::onPlayerEvent)
        playerView?.player?.on<PlayerEvent.PlaybackFinished>(::onPlayerEvent)
        playerView?.player?.on<PlayerEvent.Paused>(::onPlayerEvent)
        playerView?.player?.on<PlayerEvent.StallEnded>(::onPlayerEvent)
        playerView?.player?.on<PlayerEvent.Playing>(::onPlayerEvent)
        playerView?.player?.on<PlayerEvent.Ready>(::onPlayerEvent)
        playerView?.player?.on<PlayerEvent.TimeChanged>(::onPlayerEvent)
    }

    private fun removePlayerListener() {
        playerView?.player?.off(::onErrorEvent)
        playerView?.player?.off(::onPlayerEvent)
    }

    private fun onErrorEvent(errorEvent: ErrorEvent) {
        listener?.onEnd()
    }

    fun stopContent() {
        Timber.d("playerManager >> stopContent")
        if(playerView?.player?.isPlaying == true) {
            playerView?.player?.onPause()
            playerView?.onPause()
        }
        playerView?.player?.onStop()
        playerView?.onStop()
        playerView?.player?.unload()
        runnableForPlayingContent?.let { handlerForPlayingContent?.removeCallbacks(it) }
        onDestroy()
    }

    private fun onDestroy() {
        removePlayerListener()
        playerView?.player?.destroy()
        playerView?.player = null
        playerView?.onDestroy()
        runnableForPlayingContent = null
        handlerForPlayingContent = null
    }

    fun setURL(url: String?) {
        if(!TextUtils.isEmpty(url)) {
            this.url = url!!
        }
    }
}