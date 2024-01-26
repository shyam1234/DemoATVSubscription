package com.willow.android.tv.ui.playback


import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bitmovin.player.PlayerView
import com.bitmovin.player.SubtitleView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.event.Event
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.event.SourceEvent
import com.bitmovin.player.api.event.next
import com.bitmovin.player.api.event.on
import com.bitmovin.player.api.media.Quality
import com.bitmovin.player.api.media.thumbnail.Thumbnail
import com.bitmovin.player.api.media.video.quality.VideoQuality
import com.bitmovin.player.api.source.Source
import com.bitmovin.player.api.ui.FullscreenHandler
import com.willow.android.R
import com.willow.android.databinding.PlayerUiBinding
import com.willow.android.tv.utils.GlobalConstants.DELAY_IN_HIDE_PLAYER_CONTROLLER
import com.willow.android.tv.utils.ImageUtility
import com.willow.android.tv.utils.hide
import com.willow.android.tv.utils.show
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask


private const val SEEK_TIME_IN_SEC: Int = 10
class PlayerUI(
    private val activityContext: Activity,
    private val listener: IPlayerUIListener,
    playerConfig: PlayerConfig
) : RelativeLayout(activityContext), AdapterView.OnItemSelectedListener, SettingsSpinner.OnSpinnerEventsListener {

    private var dialog: Dialog? = null
    private var isPlayerReady:Boolean = false
    private var binding: PlayerUiBinding = PlayerUiBinding.inflate(LayoutInflater.from(activityContext),this,true)
    private var playDrawable: Drawable? = null
    private var pauseDrawable: Drawable? = null
    private var replayDrawable: Drawable? = null
    private var lastUiInteraction: Long = 0
    private var uiHideTimer: Timer = Timer()
    private var uiHideTask: TimerTask? = null
    private var live: Boolean = false
    private var isTrackerSticky: Boolean = false
    // Create new Player with our PlayerConfig
    val player:Player
    // Create new PlayerView with our Player
    private val playerView: PlayerView
    private val seekBarChangeListener : SeekBar.OnSeekBarChangeListener
    // on click, toggle Playback
    private val onClickListener: OnClickListener
    private var isSought: Int = 0
    private var layoutThumbnail: View? = null
    private var subtitleView: SubtitleView? = null
    private var lastSelectedSubtitle: Int = 0
    private var lastSelectedQuality: Int = -1
    private var lastSelectedSpeed: Int = 3

    init {
        playDrawable = ContextCompat.getDrawable(activityContext, R.drawable.player_play_icon_anim)
        pauseDrawable = ContextCompat.getDrawable(activityContext, R.drawable.player_pause_icon_anim)
        replayDrawable = ContextCompat.getDrawable(activityContext, R.drawable.player_replay_icon_anim)

        player = Player.create(activityContext, playerConfig)
        subtitleView = SubtitleView(activityContext)
        subtitleView?.setPlayer(player)
        binding.playerCCaptionContainer.addView(subtitleView)
        setControllerVisibility(false)
        playerView = PlayerView(activityContext, player).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Only seek/timeShift when the user changes the progress (and not the PlayerEvent.TimeChanged )
                if(isSought > 0){
                    //showThumbnail(player, progress, seekBar)
                    isSought = 0
                }
                if (fromUser){
                    isSought = progress
                    showThumbnail(player, progress, seekBar)
                    player.seekOrTimeShift(progress, seekBar)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        }
        onClickListener = OnClickListener { view ->
           // setMediaControlVisible(true)
            when {
                view === binding.incPlayerController.incPlayer.playButton || view === this@PlayerUI -> player.togglePlayback()
                view === binding.imageviewSetting -> {
                    showSetting()
                }
                view === binding.incPlayerController.incPlayer.imageviewPrevious -> {
                    player.onStop()
                    listener.onPrevProgramClicked()
                    setControllerVisibility(false)
                }
                view === binding.incPlayerController.incPlayer.imageviewRewind -> {
                    player.seekRewind()
                }
                view === binding.incPlayerController.incPlayer.imageviewFf -> {
                    player.seekForward()
                }
                view === binding.incPlayerController.incPlayer.imageviewNext -> {
                    player.onStop()
                    listener.onNextProgramClicked()
                    setControllerVisibility(false)
                }
            }
        }
        binding.apply {
            if(player.isLive){
                binding.incSeekbar.seekbar.progressDrawable =
                    ContextCompat.getDrawable(activityContext, R.drawable.player_live_seekbar)
            }else{
                binding.incSeekbar.seekbar.progressDrawable =
                    ContextCompat.getDrawable(activityContext, R.drawable.player_seekbar)
            }

            incSeekbar.seekbar.setOnSeekBarChangeListener(seekBarChangeListener)
            incPlayerController.incPlayer.playButton.setOnClickListener(onClickListener)
            incPlayerController.incPlayer.imageviewNext.setOnClickListener(onClickListener)
            incPlayerController.incPlayer.imageviewPrevious.setOnClickListener(onClickListener)
            incPlayerController.incPlayer.imageviewRewind.setOnClickListener(onClickListener)
            incPlayerController.incPlayer.imageviewFf.setOnClickListener(onClickListener)
            imageviewSetting.setOnClickListener(onClickListener)
            incSeekbar.seekbar.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    hideThumbnail()
                }
            }
        }

        addView(playerView, 0)
        addPlayerListener()
        handlePlayerEvent()
        showProgressLoader()
    }

    private fun setDefaultVideoQuality() {
        val maxBitrate = getMaxBitrate(getMaxBitRatesInDiffCategories(player.source?.availableVideoQualities))
        maxBitrate?.let {
            player.source?.setVideoQuality(it.id)
        }
    }

    private fun getMaxBitrate(bitrate: List<Pair<String, VideoQuality>>): VideoQuality? {
          var value = 0
          var maxBitrate :VideoQuality ? = null
          bitrate.forEach {
              if(value < it.second.bitrate){
                  value = it.second.bitrate
                  maxBitrate = it.second
              }
          }
        return maxBitrate
    }

    private fun setControllerVisibility(flag: Boolean) {
        if (flag) {
            binding.controlView.show()
        } else {
            dialog?.dismiss()
            dialog = null
            binding.controlView.hide()
        }
    }

    private fun hideThumbnail() {
        layoutThumbnail?.visibility = View.GONE
        isSought = 0
    }

    fun load(source: Source) {
        source.next<SourceEvent.Loaded>(::handlePlayerEvent)
        player.load(source)
    }


    private fun showSetting() {
        val layoutInflater = activityContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout: View = layoutInflater.inflate(R.layout.player_setting_options, null)
        val spinnerQuality = layout.findViewById<Spinner>(R.id.setting_spinner_quality) as SettingsSpinner
        val spinnerPlaySpeed = layout.findViewById<Spinner>(R.id.setting_spinner_play_speed) as SettingsSpinner
        val spinnerSubtitles = layout.findViewById<Spinner>(R.id.setting_spinner_subtitles) as SettingsSpinner
        val qualityHolder = layout.findViewById<LinearLayoutCompat>(R.id.quality_holder)
        val subtitlesHolder = layout.findViewById<LinearLayoutCompat>(R.id.subtitle_holder)
        val speedHolder = layout.findViewById<LinearLayoutCompat>(R.id.speed_holder)

        //default values
        qualityHolder.visibility = View.GONE
        subtitlesHolder.visibility = View.GONE

        //getting the avail video bitrate
        val listOfBitrate = getMaxBitRatesInDiffCategories(player.source?.availableVideoQualities)
        //set bitrate
        val listOfQuality: ArrayList<String?> = ArrayList()
        listOfQuality.add("Auto")
        listOfBitrate.forEach {
            listOfQuality.add(convertBitrateToStandard(it.second))
            qualityHolder.visibility = View.VISIBLE
        }
        var dataAdapter = SetingsSpinnerItemsAdapter(activityContext, listOfQuality)
        spinnerQuality.adapter = dataAdapter
        //getting and setting the available subtitle tracks
        var listOfSubtitle: ArrayList<String?> = ArrayList()
        listOfSubtitle.add("Off")
        player.source?.availableSubtitleTracks?.forEach {
            listOfSubtitle.add(it.label)
            subtitlesHolder.visibility = View.VISIBLE
        }
        dataAdapter = SetingsSpinnerItemsAdapter(activityContext, listOfSubtitle)
        spinnerSubtitles.adapter = dataAdapter

        //getting and setting the available speed types
        val listOfSpeed = ArrayList(resources.getStringArray(R.array.arraySpeed).toList())
        dataAdapter = SetingsSpinnerItemsAdapter(activityContext, listOfSpeed)
        spinnerPlaySpeed.adapter = dataAdapter

        // Displaying the popup at the specified location, + offsets.
        spinnerQuality.onItemSelectedListener = this
        spinnerPlaySpeed.onItemSelectedListener = this
        spinnerSubtitles.onItemSelectedListener = this

        // Creating the Dialog
        dialog =  Dialog(activityContext)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(layout)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window: Window? = dialog?.window
        val windowLayoutParams = window?.attributes
        windowLayoutParams?.gravity = (Gravity.TOP or Gravity.END)
        windowLayoutParams?.x = resources.getDimension(R.dimen.popup_x_offset).toInt() // right margin
        windowLayoutParams?.y = resources.getDimension(R.dimen.popup_y_offset).toInt() // top margin
        window?.attributes = windowLayoutParams
        dialog?.show()

        if(lastSelectedQuality >=0 ){
            spinnerQuality.setSelection(lastSelectedQuality)
        }
        spinnerSubtitles.setSelection(lastSelectedSubtitle)
        spinnerPlaySpeed.setSelection(lastSelectedSpeed)

        dialog?.setOnDismissListener {
            lastUiInteraction = System.currentTimeMillis()
        }

        if (player.isLive) {

            speedHolder.visibility = View.GONE
            subtitlesHolder.visibility = View.GONE
        }

        spinnerQuality.setSpinnerEventsListener(this@PlayerUI)
        spinnerPlaySpeed.setSpinnerEventsListener(this@PlayerUI)
        spinnerSubtitles.setSpinnerEventsListener(this@PlayerUI)

        qualityHolder.setOnClickListener { spinnerQuality.performClick() }
        subtitlesHolder.setOnClickListener { spinnerSubtitles.performClick() }
        speedHolder.setOnClickListener { spinnerPlaySpeed.performClick() }
    }

    private fun getSelectedIndex(spinner: Spinner?, selectedValue: String?): Int {
        return spinner?.let { sp ->
            (0 until sp.count).firstOrNull { sp.getItemAtPosition(it) == selectedValue } ?: 0
        } ?: 0
    }



    private fun onReady(event: Event? = null) {
        isPlayerReady = true
        //Play the content after init the page
        onClickListener.onClick(binding.incPlayerController.incPlayer.playButton )
    }

    private fun removePlayerListener() {
        player.off(::handlePlayerEvent)
    }

    private fun startUiHiderTask() {
        stopUiHiderTask()

        // Create Task which hides the UI after a specified time (UiHideTimer)
        uiHideTask = object : TimerTask() {
            override fun run() {
                    val timeSincelastUiInteraction = System.currentTimeMillis() - lastUiInteraction
                    if (timeSincelastUiInteraction > DELAY_IN_HIDE_PLAYER_CONTROLLER) {
                        if(!isTrackerSticky) {
                            setControlsVisible(false)
                        }
                    }
            }
        }
        // Schedule the hider task, so it checks the state every 100ms
        uiHideTimer.scheduleAtFixedRate(uiHideTask, 0, 100)
    }

    private fun stopUiHiderTask() {
        uiHideTask?.cancel()
        uiHideTask = null
    }

    private var isFlag:Boolean = false
    fun setMediaControlVisible(visible: Boolean) {
        if(isPlayerReady) {
            lastUiInteraction = System.currentTimeMillis()
            setControlsVisible(visible)
            if(!isFlag){
                binding.incPlayerController.incPlayer.playButton.requestFocus()
            }
        }
    }

    private fun setControlsVisible(visible: Boolean) {
        post {
            if (visible) {
                startUiHiderTask()
            } else {
                stopUiHiderTask()
            }
            isFlag = visible
            setControllerVisibility(visible)
            if (!visible) {
                hideThumbnail()
            }
        }
    }

    fun setFullscreenHandler(fullscreenHandler: FullscreenHandler) {
        playerView.setFullscreenHandler(fullscreenHandler)
    }

    fun onStart() = playerView.onStart()

    fun onResume(){
        playerView.onResume()
        if(player.isLive && !player.isPlaying){
            player.play()
        }
    }

    fun onPause() = playerView.onPause()

    fun onStop() = playerView.onStop()

    fun onDestroy() {
        playerView.onDestroy()
        destroy()
    }

    private fun destroy() {
        player.source?.off(::handlePlayerEvent)
        removePlayerListener()
        uiHideTimer.cancel()
    }

    private fun addPlayerListener() {
        player.on<PlayerEvent.TimeChanged>(::handlePlayerEvent)
        player.on<PlayerEvent.Ready>(::handlePlayerEvent)
        player.on<PlayerEvent.Ready>(::onReady)
        player.on<PlayerEvent.Play>(::handlePlayerEvent)
        player.on<PlayerEvent.Paused>(::handlePlayerEvent)
        player.on<PlayerEvent.Destroy>(::handlePlayerEvent)
        player.on<PlayerEvent.StallStarted>(::handlePlayerEvent)
        player.on<PlayerEvent.StallEnded>(::handlePlayerEvent)
        player.on<PlayerEvent.Seek>(::handlePlayerEvent)
        player.on<PlayerEvent.Seeked>(::handlePlayerEvent)
        player.on<PlayerEvent.PlaybackFinished>(::handlePlayerEvent)
        player.on<PlayerEvent.AdBreakStarted>(::handlePlayerEvent)
        player.on<PlayerEvent.AdBreakFinished>(::handlePlayerEvent)
    }

    /**
     * Methods for UI update
     */
    private fun handlePlayerEvent(event: Event? = null) {

        when(event){
            is PlayerEvent.StallStarted,
            is PlayerEvent.Seek->{
                binding.loadingview.layoutLoading.post {
                    binding.loadingview.layoutLoading.show()
                }
            }
            is PlayerEvent.StallEnded -> {
                binding.loadingview.layoutLoading.post {
                    binding.loadingview.layoutLoading.hide()
                }
            }
            is PlayerEvent.Seeked ->{
                binding.loadingview.layoutLoading.post {
                    binding.loadingview.layoutLoading.hide()
                }
                updateUI()
            }

            is PlayerEvent.PlaybackFinished -> {
                live = !player.isLive
                binding.incPlayerController.incPlayer.playButton.post{
                    binding.incPlayerController.incPlayer.playButton.setImageDrawable(replayDrawable)
                    binding.incPlayerController.incPlayer.playButton.requestFocus()
                    isTrackerSticky = true
                    setMediaControlVisible(true)
                }
                binding.incPlayerController.incPlayer.imageviewRewind.post{
                    binding.incPlayerController.incPlayer.imageviewRewind.disabled()
                }
                binding.incPlayerController.incPlayer.imageviewFf.post {
                    binding.incPlayerController.incPlayer.imageviewFf.disabled()
                }
                binding.incSeekbar.seekbar.post {
                    binding.incSeekbar.seekbar.disabled()
                }
            }
            is PlayerEvent.AdBreakStarted ->{
                binding.playerController.hide()
            }
            is PlayerEvent.AdBreakFinished ->{
                binding.playerController.show()
            }
            else ->{
                updateUI()
            }
        }
    }

    private fun updateUI(){
        binding.incSeekbar.seekbar.post {
            // if the live state of the player changed, the UI should change it's mode
            val positionMs: Int
            val durationMs: Int
            if ( live != player.isLive) {
                live = player.isLive
                if (live) {
                    binding.incSeekbar.imageLiveTag.visibility = View.VISIBLE
                    binding.incPlayerController.durationView.visibility = View.INVISIBLE
                    binding.incPlayerController.positionView.visibility = View.INVISIBLE
                    binding.incPlayerController.incPlayer.imageviewRewind.visibility = View.INVISIBLE
                    binding.incPlayerController.incPlayer.imageviewFf.visibility = View.INVISIBLE
                    binding.incPlayerController.incPlayer.imageviewNext.visibility = View.INVISIBLE
                    binding.incPlayerController.incPlayer.imageviewPrevious.visibility = View.INVISIBLE
                    binding.incPlayerController.imgBtnFavorite.visibility = View.INVISIBLE
                    binding.incPlayerController.imgBtnBookmark.visibility = View.INVISIBLE
                    binding.incPlayerController.divider.visibility = View.GONE
                    binding.incPlayerController.incPlayer.playButton.visibility = View.INVISIBLE
                    binding.incSeekbar.seekbar.disabledKeyEvent()
                    binding.incSeekbar.seekbar.progressDrawable.setColorFilter(
                        ContextCompat.getColor(activityContext, R.color.red), PorterDuff.Mode.SRC_IN)
                } else {
                    binding.incSeekbar.imageLiveTag.visibility = View.GONE
                    binding.incPlayerController.durationView.visibility = View.VISIBLE
                    binding.incPlayerController.incPlayer.imageviewRewind.visibility = View.VISIBLE
                    binding.incPlayerController.incPlayer.imageviewFf.visibility = View.VISIBLE
                    binding.incPlayerController.incPlayer.imageviewNext.visibility = View.INVISIBLE
                    binding.incPlayerController.incPlayer.imageviewPrevious.visibility = View.INVISIBLE
                    binding.incPlayerController.positionView.visibility = View.VISIBLE
                    binding.incPlayerController.imgBtnFavorite.visibility = View.INVISIBLE
                    binding.incPlayerController.imgBtnBookmark.visibility = View.INVISIBLE
                    binding.incPlayerController.divider.visibility = View.VISIBLE
                    binding.incPlayerController.incPlayer.playButton.visibility = View.VISIBLE
                    binding.incSeekbar.seekbar.enabled()
                }
            }

            if (live) {
                // The Seekbar does not support negative values
                // so the seekable range is shifted to the positive
                durationMs = 100//(-player.maxTimeShift * 1000).toInt()
                positionMs = 100//(durationMs + player.timeShift * 1000).toInt()
            } else {
                // Converting to milliseconds
                positionMs = (player.currentTime * 1000).toInt()
                durationMs = (player.duration * 1000).toInt()

                // Update the TextViews displaying the current position and duration
                binding.incPlayerController.positionView.text = millisecondsToTimeString(positionMs)
                binding.incPlayerController.durationView.text = millisecondsToTimeString(durationMs)
            }

            // Update the values of the Seekbar
            binding.incSeekbar.seekbar.progress = positionMs
            binding.incSeekbar.seekbar.max = durationMs

            // Update the image of the playback button
            if (player.isPlaying) {
                if(binding.loadingview.layoutLoading.isVisible) {
                    binding.loadingview.layoutLoading.hide()
                }
                isTrackerSticky = false
                binding.incPlayerController.incPlayer.playButton.setImageDrawable(pauseDrawable)
            } else {
                isTrackerSticky = true
                binding.incPlayerController.incPlayer.playButton.setImageDrawable(playDrawable)
            }

            if(positionMs < (SEEK_TIME_IN_SEC * 1000)){
                if(binding.incPlayerController.incPlayer.imageviewRewind.isEnabled){
                    binding.incPlayerController.incPlayer.imageviewRewind.disabled()
                }
            }else{
                if(!binding.incPlayerController.incPlayer.imageviewRewind.isEnabled){
                    binding.incPlayerController.incPlayer.imageviewRewind.enabled()
                }
            }

            if(positionMs > (durationMs - (SEEK_TIME_IN_SEC*1000))){
                if(binding.incPlayerController.incPlayer.imageviewFf.isEnabled) {
                    binding.incPlayerController.incPlayer.imageviewFf.disabled()
                }
            }else{
                if(!binding.incPlayerController.incPlayer.imageviewFf.isEnabled){
                    binding.incPlayerController.incPlayer.imageviewFf.enabled()
                }
            }
        }
    }

    private fun millisecondsToTimeString(milliseconds: Int): String {
        var second = milliseconds / 1000 % 60
        val minute = milliseconds / (1000 * 60) % 60
        val hour = milliseconds / (1000 * 60 * 60) % 24
        if(second < 0) second = 0
        return if (hour > 0) {
            String.format("%02d:%02d:%02d", hour, minute, second)
        } else {
            String.format("%02d:%02d", minute, second)
        }
    }

    override fun onSpinnerOpened(spinner: Spinner) {
        spinner.setBackgroundResource(R.drawable.bg_player_settings_spinner_opened)
    }

    override fun onSpinnerClosed(spinner: Spinner) {
        spinner.setBackgroundResource(R.drawable.bg_player_settings_spinner_normal)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        lastUiInteraction = System.currentTimeMillis()
        val item: String? = parent?.getItemAtPosition(position) as String?
        when(parent?.id){
             R.id.setting_spinner_quality -> {
                 item?.let {
                     if(it.contains("auto",true)){
                         player.source?.setVideoQuality(Quality.AUTO_ID)
                     }else{
                         val listOfBitrate = getMaxBitRatesInDiffCategories(player.source?.availableVideoQualities)
                         listOfBitrate.forEach{ bitrate ->
                             if (convertBitrateToStandard(bitrate.second) == it){
                                 player.source?.setVideoQuality(bitrate.second.id)
                             }
                         }
                         player.source?.setVideoQuality(it)
                     }
                 }
                 lastSelectedQuality = position
             }
            R.id.setting_spinner_play_speed -> {
                item?.let {
                    if(it.contains("normal",true)){
                        player.playbackSpeed = 1F
                    }else{
                        player.playbackSpeed = it.toFloat()
                    }
                }
                lastSelectedSpeed = position
            }
            R.id.setting_spinner_subtitles -> {
                item?.let {
                    if(it.contains("off",true)){
                        player.source?.setSubtitleTrack(null)
                    }else{
                        val subtitleTracks = player.source?.availableSubtitleTracks
                        player.source?.setSubtitleTrack(subtitleTracks?.get(position-1)?.id)
                    }
                }
                lastSelectedSubtitle = position
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        lastUiInteraction = System.currentTimeMillis()
    }

    private val handleShowThumbnail = Handler(Looper.myLooper()!!)
    fun showThumbnail(player: Player, progress: Int, seekBar: SeekBar) {
        val thumbnail: Thumbnail? = player.source?.getThumbnail((progress/1000).toDouble())
        val layoutInflater = activityContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if(layoutThumbnail != null) {
            removeView(layoutThumbnail)
        }
        layoutThumbnail = layoutInflater.inflate(R.layout.player_preview_thumbnail, null)
        layoutThumbnail?.let {
            val ll_holder = it.findViewById<LinearLayout>(R.id.previewFrameLayout)
            val thumbnailHolder = it.findViewById<ImageView>(R.id.imgview_thumbnail)
            val seekTime = it.findViewById<TextView>(R.id.textview_seek_time)
            seekTime.text = millisecondsToTimeString(progress)
            ll_holder.visibility = View.GONE

            val thumbnailWidth = resources.getDimension(R.dimen.thumbnail_width)
            val thumbPos: Int = seekBar.measuredWidth * seekBar.progress / seekBar.max - seekBar.thumbOffset
            ll_holder.translationX = (thumbPos- thumbnailWidth/2)
            if(ll_holder.translationX  >= seekBar.width-thumbnailWidth){
                 ll_holder.translationX = (seekBar.width - thumbnailWidth)
            }else if ( ll_holder.translationX < 0){
                 ll_holder.translationX = seekBar.left.toFloat()
            }
            ll_holder.translationY =
                (binding.linControlHolder.y - (resources.getDimension(R.dimen.thumbnail_height) + resources.getDimension(
                    R.dimen.thumbnail_gap
                )))
            addView(layoutThumbnail)

            Timber.d("showThumbnail $thumbnail")
            thumbnail?.let {
                Thread() {
                     val thumbnailSpriteImage = ImageUtility.getImage(it.uri)
                     val currThumbnail = Bitmap.createBitmap(
                         thumbnailSpriteImage,
                         it.x,
                         it.y,
                         it.width,
                         it.height
                     )
                     val runnable = Runnable{
                         if(currThumbnail != null ){
                             thumbnailHolder.setImageBitmap(currThumbnail)
                             ll_holder.visibility = View.VISIBLE
                         }else{
                             ll_holder.visibility = View.GONE
                         }
                     }
                     handleShowThumbnail.post(runnable)
                 }.start()
             }

        }
    }

    fun setNextBtnVisibility(visible: Boolean){
        binding.incPlayerController.incPlayer.imageviewNext.visibility = View.INVISIBLE
        if(visible){
            binding.incPlayerController.incPlayer.imageviewNext.enabled()
        }
    }

    fun setPrevBtnVisibility(visible: Boolean){
        binding.incPlayerController.incPlayer.imageviewPrevious.visibility = View.INVISIBLE
        if(visible){
            binding.incPlayerController.incPlayer.imageviewPrevious.enabled()
        }
    }

    private fun showProgressLoader() {

        val imageViewAnimator = ObjectAnimator.ofFloat(binding.loadingview.progressBar, View.ROTATION, 359f)
        imageViewAnimator.repeatCount = Animation.INFINITE
        imageViewAnimator.duration = 1000
        imageViewAnimator.start()
    }

    fun setTitle(title: String?) {
        binding.playerContentTitle.text = title
    }

    private fun convertBitrateToStandard(bitrate: VideoQuality): String {
        return when {
            bitrate.height >= 4320 -> "8K"
            bitrate.height >= 2880 -> "5K"
            bitrate.height >= 2160 -> "4K"
            bitrate.height >= 1440 -> "2K"
            bitrate.height >= 1080 -> "${bitrate.height}p HD"
            else -> "${bitrate.height}p"
        }
    }
    private fun getMaxBitRatesInDiffCategories(bitrates: List<VideoQuality>?): List<Pair<String, VideoQuality>> {
        val maxBitrates = mutableMapOf<String, VideoQuality>()
        bitrates?.forEach { bitrate ->
            if(maxBitrates["${bitrate.height}"] == null || (maxBitrates["${bitrate.height}"]?.bitrate!! < bitrate.bitrate))
                maxBitrates["${bitrate.height}"] = bitrate
        }
        maxBitrates.toSortedMap()
        return maxBitrates.toList().sortedByDescending { it.second.height }
    }


}//end of class


private fun TextView.setLive() {
    visibility = VISIBLE
    text = "Live"
    setTextColor(ContextCompat.getColor(context, R.color.red))
}


//extension function
private fun Player.togglePlayback() = if (isPlaying) pause() else play()

private fun PlayerView.toggleFullscreen() = if (isFullscreen) {
    exitFullscreen()
} else {
    enterFullscreen()
}

// If the current stream is a live stream, we have to use the timeShift method
private fun Player.seekOrTimeShift(progress: Int, seekBar: SeekBar) {
    if (!isLive) {
        seek(progress / 1000.0)
    } else {
        timeShift((progress - seekBar.max) / 1000.0)
    }
}

fun Player.seekForward(time:Int = SEEK_TIME_IN_SEC){
    seek(currentTime + time)
}

fun Player.seekRewind(time:Int = SEEK_TIME_IN_SEC){
    seek(currentTime - time)
}

private fun ImageButton.disabled() {
    alpha = 0.5f
    isEnabled = false
}

private fun ImageButton.enabled() {
    alpha = 1.0f
    isEnabled = true
}

private fun SeekBar.disabledKeyEvent() {
    setOnKeyListener { v, keyCode, event ->
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                true
            }
            else -> {
                false
            }
        }

    }
}

private fun SeekBar.disabled() {
    alpha = 0.5f
    clearFocus()
    disabledKeyEvent()
}
fun SeekBar.setProgressDrawable(drawable: Drawable) {
    progressDrawable = drawable
}

private fun SeekBar.enabled() {
    alpha = 1.0f
   // isEnabled = true
    setOnKeyListener(null)
}

interface IPlayerUIListener{

    fun onNextProgramClicked()
    fun onPrevProgramClicked()
}


