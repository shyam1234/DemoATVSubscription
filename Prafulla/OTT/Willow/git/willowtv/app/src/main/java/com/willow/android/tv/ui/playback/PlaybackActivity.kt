package com.willow.android.tv.ui.playback

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bitmovin.analytics.BitmovinAnalytics
import com.bitmovin.analytics.BitmovinAnalyticsConfig
import com.bitmovin.analytics.bitmovin.player.BitmovinPlayerCollector
import com.bitmovin.analytics.data.AdEventData
import com.bitmovin.analytics.data.EventData
import com.bitmovin.analytics.enums.CDNProvider
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.advertising.AdItem
import com.bitmovin.player.api.advertising.AdSource
import com.bitmovin.player.api.advertising.AdSourceType
import com.bitmovin.player.api.advertising.AdvertisingConfig
import com.bitmovin.player.api.event.Event
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.event.SourceEvent
import com.bitmovin.player.api.event.on
import com.bitmovin.player.api.media.AdaptationConfig
import com.bitmovin.player.api.media.thumbnail.ThumbnailTrack
import com.bitmovin.player.api.media.video.quality.VideoAdaptation
import com.bitmovin.player.api.source.Source
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.ui.StyleConfig
import com.willow.android.BuildConfig
import com.willow.android.R
import com.willow.android.WillowApplication
import com.willow.android.databinding.ActivityPlaybackBinding
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.configs.WiConfig
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.tv.common.base.BaseActivity
import com.willow.android.tv.data.repositories.commondatamodel.Card
import com.willow.android.tv.data.repositories.commondatamodel.CardRow
import com.willow.android.tv.data.repositories.commondatamodel.ContentDetail
import com.willow.android.tv.data.repositories.player.datamodel.APIStreamingURLDataModel
import com.willow.android.tv.data.room.db.VideoProgressDao
import com.willow.android.tv.ui.playback.adapter.PlayerSourceSelectionAdapter
import com.willow.android.tv.ui.playback.logging.EventLogger
import com.willow.android.tv.ui.playback.logging.LoggerConfig
import com.willow.android.tv.ui.playback.model.PlayerRequestModel
import com.willow.android.tv.ui.playback.viewmodel.PlaybackViewModelFactory
import com.willow.android.tv.ui.playback.viewmodel.PlaybackViewmodel
import com.willow.android.tv.ui.subscription.SubscriptionActivity
import com.willow.android.tv.utils.CheckConnection
import com.willow.android.tv.utils.CommonFunctions.getPlaybackParams
import com.willow.android.tv.utils.CommonFunctions.getPlayerReqModelFromHashMap
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.GlobalConstants
import com.willow.android.tv.utils.PrefRepository
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.TVEPlaybackService
import com.willow.android.tv.utils.Utils
import com.willow.android.tv.utils.config.GlobalTVConfig
import com.willow.android.tv.utils.extension.parcelable
import com.willow.android.tv.utils.extension.showAlertDialog
import com.willow.android.tv.utils.extension.startActivityWithOutData
import com.willow.android.tv.utils.hide
import com.willow.android.tv.utils.show
import timber.log.Timber


class PlaybackActivity : BaseActivity(), IPlayerUIListener, IPlayerStatus{
    private lateinit var bitmovinAnalyticsConfig: BitmovinAnalyticsConfig
    private lateinit var analyticsCollector: BitmovinPlayerCollector
    private lateinit var playerUi: PlayerUI
    private lateinit var videoProgressDao: VideoProgressDao
    private var videoId: Int? = 0
    private var thumbnailtrack: String? = ""
    private lateinit var binding: ActivityPlaybackBinding
    private lateinit var playbackViewmodel: PlaybackViewmodel
    private lateinit var prefRepository: PrefRepository
    private var card: Card? = null
    private var cardRow: CardRow? = null
    private var screenName: String? = null

    // Initialize loggers
    private val playerLogger = EventLogger(LoggerConfig.PlayerLoggerConfig())
    private val sourceLogger = EventLogger(LoggerConfig.SourceLoggerConfig())
    private val viewLogger = EventLogger(LoggerConfig.ViewLoggerConfig())
    private var pollerGuid: String = ""
    private var pollerTimer: CountDownTimer? = null
    private var isLive: Boolean = false         // True if the current match is live
    private var progressFromDB: Double = 0.0
    private var isErrorOccurred: Boolean = false
    private val mHandlerInActiveState = Handler(Looper.myLooper()!!)
    private val mRunnableInActiveState = Runnable{
        if(!isErrorOccurred) {
            binding.loadingview.layoutLoading.post {
                binding.loadingview.layoutLoading.hide()
            }
            showErrorPage(MessageConfig.unpredictedError)
        }
    }
    private val checkConnection by lazy { CheckConnection(application) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaybackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        card = intent.parcelable<Card>("data1")
        cardRow = intent.parcelable<CardRow>("data2")
        screenName = intent.getStringExtra("screen_name")
        videoProgressDao = WillowApplication.dbBuilder.videoProgressDao()
        prefRepository = PrefRepository(WillowApplication.instance)
        initBitmovinAnalytics()

        checkConnection.observe(this@PlaybackActivity){
            Timber.d("MainActivity Connection Check:: $it")

            if(!it){
                showError(binding.root,ErrorType.NONE,"NO INTERNET", backBtnListener = {onBackPressed()},btnText = "Back")
            }else{
                hideError(binding.root)
            }
        }

        playerUi = PlayerUI(this, this,  getPlayerConfig())

        // Set the FullscreenHandler of the PlayerUI
        val fullscreenHandler = CustomFullscreenHandler(this, playerUi)
        playerUi.setFullscreenHandler(fullscreenHandler)

        playerUi.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        if (card?.isShowLiveTag() == true && ((card?.content_details?.size ?: 0) > 1)) {
            binding.sourceSelection.sourceHolder.show()
            initMultiSourceSelection(card?.content_details as ArrayList<ContentDetail>)
        }else{
            binding.sourceSelection.sourceHolder.hide()
            loadData(card)
        }
        binding.rootView.addView(playerUi)

        addPlayerListener(playerUi.player)

        initProgressbar()
        Utils.memoryLogs()
    }

    private fun initProgressbar() {
        val imageViewAnimator = ObjectAnimator.ofFloat(
            binding.loadingview.progressBar,
            View.ROTATION, 359f
        )
        imageViewAnimator.repeatCount = Animation.INFINITE
        imageViewAnimator.duration = 1000
        imageViewAnimator.start()
    }

    private fun initMultiSourceSelection(contentDetailsList: ArrayList<ContentDetail>?) {
        binding.sourceSelection.recyclerviewSource.layoutManager = LinearLayoutManager(this)
        contentDetailsList?.add(ContentDetail(-1, resources.getString(R.string.back), 10, ""))
        // Define a custom comparator
        val comparator = Comparator<ContentDetail> { item1, item2 ->
            item1.priority- item2.priority
        }
        // Sort the list using the custom comparator
        val sortedList = contentDetailsList?.sortedWith(comparator)
        binding.sourceSelection.recyclerviewSource.adapter = PlayerSourceSelectionAdapter(sortedList) {
                val position = it.tag as Int
                if(position < ((sortedList?.size?:0)-1)) {
                    val contentDetails = sortedList?.get(position)
                    card?.content_id = contentDetails?.content_id
                    card?.priority = contentDetails?.priority
                    binding.sourceSelection.sourceHolder.hide()
                    loadData(card)
                }else{
                    //in case of "back" selection
                    onBackPressedDispatcher.onBackPressed()
                }
            }
    }

    private fun loadData(data: Card?) {
        card = data
        playerUi.setTitle("${card?.title} - ${card?.sub_title}")
        videoId = data?.content_id

        if(card?.content_type.equals("live")){
            isLive = true
            /**
             * TODO Uncomment when poller api is working
            * */
            setupPollerForLive(card?.content_id)
        }

        playbackViewmodel = ViewModelProvider(this, PlaybackViewModelFactory(WillowApplication.instance, videoProgressDao))[PlaybackViewmodel::class.java]

        val playerRequestModel = PlayerRequestModel(matchId = data?.event_id,willowUserID = prefRepository.getUserID(),contentId = data?.content_id,
            contentType = data?.content_type, data?.getTargetUrl(), priority = data?.priority.toString(),
            needLogin = (data?.need_login != 0), needSubscription = (data?.need_subscription != 0))

/*
        //Test Code
        if(GlobalConstants.bypassVPN) {
            setupPlayer(videoId, "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd")
        }else{

        }
*/
        if(prefRepository.getTVELoggedIn()==true){
            onLoading()
            TVEPlaybackService.setActivity(this,data?.event_id.toString())
            TVEPlaybackService.getAuthorizeHeader(getPlaybackParams(playerRequestModel.matchId.toString(),playerRequestModel.contentType,playerRequestModel.contentId,playerRequestModel.priority,this,playerRequestModel.needLogin, playerRequestModel.needSubscription))
        }else{

            getStreamingUrlForPlayer(playerRequestModel)

        }

        videoId?.let { playbackViewmodel.loadVideoProgress(it) }

        playbackViewmodel.videoProgress.observe(this) { videoProgress ->
            // Update UI with video progress
            Timber.d("videoProgress:: " + videoProgress?.progress)
            progressFromDB = videoProgress?.progress?:0.0
        }

        playbackViewmodel.renderPage.observe(this) { it ->
            when (it) {
                is Resource.Success -> {
                    onSuccess(it.data)
                }
                is Resource.Loading -> {
                    onLoading()
                }
                else -> {
                   // showErrorPage("AccessDenied")
                    finish()
                    Toast.makeText(applicationContext,it.message,Toast.LENGTH_SHORT).show()
                }
            }
        }

        playbackViewmodel.pollerData.observe(this) {
            if (it?.status.equals("N", true)) {

                GlobalTVConfig.getPollerDesc()

                showAlertDialog(GlobalTVConfig.getPollerTitle().toString(), GlobalTVConfig.getPollerDesc().toString())
            }

            if (it?.guid?.isNotEmpty()==true) {
                pollerGuid = it.guid
            }

        }
    }


    fun getStreamingUrlForPlayer(playerRequestModel: PlayerRequestModel){

        playbackViewmodel.getPlayerStreamingURL(playerRequestModel)

    }



    private fun onSuccess(data: APIStreamingURLDataModel?){
        Timber.d("okhttp onSuccess >> $data")
        if(data?.Videos == null){
            if(data?.error != null){
                showErrorPage(data.error.description)
            }else if(data?.subscribe != null){
             //   showErrorPage(data.subscribe.description)
              //  launchActivity<SubscriptionActivity> {  }
                this.startActivityWithOutData<SubscriptionActivity>(true)
            }
        }else{
            val url = data.Videos[0].Url
            setupPlayer(videoId,url)
        }
    }



    private fun onLoading() {
        if(!binding.loadingview.layoutLoading.isVisible) {
            binding.loadingview.layoutLoading.show()
        }
    }

    private fun setupPlayer(videoId: Int?, url: String?) {

        Timber.d("setupPlayer videoId :: "+videoId)

        // Load the Source into the player
        url?.let { videoURL ->
            val sourceConfig = SourceConfig.fromUrl(videoURL)
            thumbnailtrack?.let { track ->
                sourceConfig.thumbnailTrack = ThumbnailTrack(track)
            }
            //for playback poster
            sourceConfig.posterSource = "https://bitmovin-a.akamaihd.net/content/poster/hd/RedBull.jpg"
            val source = Source.create(sourceConfig)
            playerUi.load(source)
            attachLogger(source)
        }

        //set prev and next visibility based on the cardRow
      /*  playerUi.setNextBtnVisibility((card?.index ?: 0) < (cardRow?.getListOfCards()?.size ?: 0) - 1)
        playerUi.setPrevBtnVisibility((card?.index ?: -1) > 0)*/
        playerUi.setNextBtnVisibility(false)
        playerUi.setPrevBtnVisibility(false)
    }

    override fun onStart() {
        super.onStart()
        playerUi.onStart()
    }

    override fun onResume() {
        super.onResume()
        playerUi.onResume()
    }

    override fun onPause() {
        if(!playerUi.player.isAd) {
            playerUi.onPause()
        }
        super.onPause()
    }

    override fun onStop() {
        playerUi.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        playerUi.onDestroy()
        super.onDestroy()
        detachAnalytics()
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        // This method is called on key down and key up, so avoid being called twice
        if (event?.action == KeyEvent.ACTION_DOWN) {
            if (handleUserInput(event.keyCode)) {
                return true
            }
        }
        // Make sure to return super.dispatchKeyEvent(event) so that any key not handled yet will work as expected
        return super.dispatchKeyEvent(event)
    }

    private fun handleUserInput(keyCode: Int): Boolean {
        println("handleUserInput keyCode: $keyCode")
        if(!playerUi.player.isAd) {
            playerUi.setMediaControlVisible(true)
            return when (keyCode) {
                KeyEvent.KEYCODE_DPAD_CENTER,
                KeyEvent.KEYCODE_ENTER,
                KeyEvent.KEYCODE_NUMPAD_ENTER,
                KeyEvent.KEYCODE_SPACE -> {
                    false
                }
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                    if (!playerUi.player.isPlaying) {
                        playerUi.player.play()
                    } else if (playerUi.player.isPlaying) {
                        playerUi.player.pause()
                    }
                    true
                }
                KeyEvent.KEYCODE_MEDIA_PLAY -> {
                    if(!playerUi.player.isPlaying) {
                        playerUi.player.play()
                    }
                    true
                }
                KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    if(!playerUi.player.isAd && playerUi.player.isPlaying) {
                        playerUi.player.pause()
                    }
                    true
                }
                KeyEvent.KEYCODE_MEDIA_STOP -> {
                    playerUi.player.onStop()
                    true
                }
                // KeyEvent.KEYCODE_DPAD_RIGHT,
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                    playerUi.player.seekForward()
                    true
                }
                // KeyEvent.KEYCODE_DPAD_LEFT,
                KeyEvent.KEYCODE_MEDIA_REWIND -> {
                    playerUi.player.seekRewind()
                    true
                }
                else -> {
                    false
                }
            }
        }else{
           return when(keyCode){
                KeyEvent.KEYCODE_DPAD_CENTER,
                KeyEvent.KEYCODE_ENTER,
                KeyEvent.KEYCODE_NUMPAD_ENTER,
                KeyEvent.KEYCODE_SPACE,
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                    //to handle the ads pause issue, suggested by Bitmovin team
                    playerUi.player.skipAd()
                    true
                }
               else -> {
                    true
               }
           }
        }
    }

    override fun onPrepare() {

    }

    override fun onPlay() {

    }

    override fun onEnd() {

    }

    fun onPausePlayback(posMs: Int, durMs: Int) {
        Timber.d("#### onPausePlayback")
        if(GlobalTVConfig.isContinueWatchingEnabled()==true) {
            if (screenName == "Explore") {
                videoId?.let {
                    if (!isLive)
                        if (::playbackViewmodel.isInitialized) {
                            playbackViewmodel.saveVideoProgress(
                                it,
                                posMs.toDouble(),
                                durMs.toDouble()
                            )
                        }
                }
            }
        }
    }

    fun deleteVideoFromDbIfPlayebackFiished(){
        videoId?.let {
            if(!isLive) {
                progressFromDB = 0.0
                playbackViewmodel.callDeleteVideoFromDb(it)
            }
        }
    }

    private fun addPlayerListener(player: Player) {
        player.on<PlayerEvent.Ready>(::handlePlayerEvent)
        player.on<PlayerEvent.Play>(::handlePlayerEvent)
        player.on<PlayerEvent.Paused>(::handlePlayerEvent)
        player.on<PlayerEvent.Destroy>(::handlePlayerEvent)
        player.on<PlayerEvent.PlaybackFinished>(::handlePlayerEvent)
        player.on<PlayerEvent.Error>(::handlePlayerEvent)
        player.on<PlayerEvent.AdError>(::handlePlayerEvent)
        player.on<SourceEvent.Error>(::handlePlayerEvent)
        player.on<PlayerEvent.Warning>(::handlePlayerEvent)
        player.on<SourceEvent.Warning>(::handlePlayerEvent)
        player.on<SourceEvent.DownloadFinished>(::handlePlayerEvent)
        player.on<SourceEvent.Info>(::handlePlayerEvent)
        player.on<PlayerEvent.Info>(::handlePlayerEvent)
        //for observing event
        player.on<PlayerEvent.AdBreakStarted>(::handlePlayerEvent)
        player.on<PlayerEvent.AdBreakFinished>(::handlePlayerEvent)
        player.on<PlayerEvent.Seek>(::handlePlayerEvent)
        player.on<PlayerEvent.Seeked>(::handlePlayerEvent)
        player.on<PlayerEvent.StallStarted>(::handlePlayerEvent)
        player.on<PlayerEvent.StallEnded>(::handlePlayerEvent)
        player.on<PlayerEvent.Playing>(::handlePlayerEvent)
        player.on<PlayerEvent.VideoPlaybackQualityChanged>(::handlePlayerEvent)
        player.on<PlayerEvent.AdStarted>(::handlePlayerEvent)
        player.on<PlayerEvent.AdFinished>(::handlePlayerEvent)
        player.on<PlayerEvent.AdScheduled>(::handlePlayerEvent)
        player.on<PlayerEvent.Active>(::handlePlayerEvent)
        player.on<PlayerEvent.Inactive>(::handlePlayerEvent)
        player.on<PlayerEvent.AdManifestLoaded>(::handlePlayerEvent)
        player.on<PlayerEvent.AdManifestLoad>(::handlePlayerEvent)
        player.on<PlayerEvent.SourceAdded>(::handlePlayerEvent)
        player.on<PlayerEvent.RenderFirstFrame>(::handlePlayerEvent)
        //end of block testing
    }

    private var isAdScheduled  = false
    private fun handlePlayerEvent(event: Event?) {
        Utils.memoryLogs()
        Timber.i("Playback>>> playerEvent::  $event")
        when(event){
            is  PlayerEvent.SourceAdded ->{
                isAdScheduled = false
            }
            is  PlayerEvent.AdScheduled,
            is  PlayerEvent.AdManifestLoaded ->{
                isAdScheduled  = true
            }
            is PlayerEvent.AdBreakStarted ->{
                Timber.i("Playback>>>  AdBreakStarted >>> dfpTag: ${GlobalTVConfig.tvConfig?.dfpTag}")
            }
            is  PlayerEvent.AdStarted ->{
                isAdScheduled = false
                binding.loadingview.layoutLoading.post {
                    binding.loadingview.layoutLoading.hide()
                }
            }
            is PlayerEvent.Error -> {
                isAdScheduled = false
                isErrorOccurred = true
                showErrorPage("$event")
            }
            is SourceEvent.Error -> {
                 isAdScheduled = false
                isErrorOccurred = true
                showErrorPage("$event")
            }
            is PlayerEvent.AdError -> {
               isAdScheduled = false
                binding.loadingview.layoutLoading.post {
                    binding.loadingview.layoutLoading.hide()
                }
            }

            is SourceEvent.DownloadFinished -> {
                if (!event.isSuccess){
                    Timber.e("Playback>>> Unable to download ${event.url}, response code was ${event.httpStatus} after ${event.downloadTime} seconds.")
                }
            }

            is  PlayerEvent.Inactive ->{
                if(event.toString().contains("BehindLiveWindowException")){
                    Toast.makeText(this, "$event", Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    mHandlerInActiveState.postDelayed(mRunnableInActiveState, GlobalConstants.DELAY_TO_SHOW_INACTIVE_PLAYER_STATUS_ERROR)
                }
            }

            is  PlayerEvent.Active ->{
                isErrorOccurred = false
                mHandlerInActiveState.removeCallbacks(mRunnableInActiveState)
            }

            is PlayerEvent.Ready ->{
                if(!isAdScheduled) {
                    binding.loadingview.layoutLoading.post {
                        binding.loadingview.layoutLoading.hide()
                    }
                }
                detachAnalytics()
                attachAnalytics()
                updateOptionalParams()
            }
            is PlayerEvent.Play ->{
                mHandlerInActiveState.removeCallbacks(mRunnableInActiveState)

                if(progressFromDB>0.0)
                    playerUi.player.seek(progressFromDB/1000.0)
            }

            is PlayerEvent.Destroy-> {
                Timber.d("#### Destroyed")
                val posMs = (playerUi.player.currentTime * 1000).toInt()
                val durMs = (playerUi.player.duration * 1000).toInt()
                onPausePlayback(posMs, durMs)
                detachAnalytics()
            }
            is PlayerEvent.PlaybackFinished -> {
                deleteVideoFromDbIfPlayebackFiished()
                detachAnalytics()
            }
            else -> {
            }
        }
    }


    override fun onNextProgramClicked() {
        val position = card?.index
        cardRow?.getListOfCards()?.let {
            val next = position?.plus(1)
            if (next != null) {
                if(it.isNotEmpty() && next < it.size){
                    if(card?.isSubscriptionRequired() == false || prefRepository.getUserSubscribed() == true) {
                        loadData(cardRow?.getListOfCards()?.get(next))
                    }
                }
            }
        }
    }

    override fun onPrevProgramClicked() {
        val position = card?.index
        cardRow?.getListOfCards()?.let {
            val prev = position?.minus(1)
            if (prev != null) {
                if(it.isNotEmpty()  && prev >= 0 && prev < it.size){

                    if(card?.isSubscriptionRequired() == false || prefRepository.getUserSubscribed() == true) {
                        loadData(cardRow?.getListOfCards()?.get(prev))
                    }
                }
            }
        }
    }



    /**
     * this method uses for configuring player for diff in build features provided by Bitmovin
     */
    private fun getPlayerConfig(): PlayerConfig {
        val styleConfig = StyleConfig()
        // Disable default Bitmovin UI
        styleConfig.isUiEnabled = false
        val playerConfig = PlayerConfig(styleConfig = styleConfig)
        //add the player config
       // initAdaptationConfig(playerConfig)
        initAdsConfig(playerConfig)
        return playerConfig
    }


    //1.Setup adaptation config
    private fun getAdaptationConfig(): AdaptationConfig {
        val adaptationConfig = AdaptationConfig()
        adaptationConfig.isRebufferingAllowed = true
        adaptationConfig.maxSelectableVideoBitrate = 800000
        adaptationConfig.initialBandwidthEstimateOverride = 1200000
        adaptationConfig.videoAdaptation = videoAdaptationListener
        return adaptationConfig
    }
    private val videoAdaptationListener = VideoAdaptation { videoAdaptationData ->
        // Get the suggested video quality id
        val suggestedVideoQualityId = videoAdaptationData.suggested
        suggestedVideoQualityId
    }


    //2.Setup advertising config
    private fun initAdsConfig(playerConfig: PlayerConfig) {
        val showAds = shouldShowAds(card)==true
        if(showAds)
            GlobalTVConfig.tvConfig?.dfpTag?.let { dfpTag ->
            playerConfig.advertisingConfig = getAdvertisingConfig(dfpTag)
        }
        Timber.d("Playback>>>  AdConfig: $showAds dfpTag: ${GlobalTVConfig.tvConfig?.dfpTag}")
    }
    private fun getAdvertisingConfig(dfpTag: String): AdvertisingConfig {
        // Create AdSources
        val firstAdSource = AdSource(AdSourceType.Ima, dfpTag)

        // Set up a pre-roll ad
        val preRoll = AdItem("pre", firstAdSource)
        //tried to make it ads experience buffer free by adding preloadOffset
        // val preRoll1 = AdItem(listOf<AdSource>(firstAdSource), position = "pre", preloadOffset = 0.0, 11 )

        // Set up a mid-roll waterfalling ad at 10% of the content duration
        // NOTE: AdItems containing more than one AdSource, will be executed as waterfalling ad
        //val midRoll = AdItem("10%", firstAdSource, secondAdSource)

        // Set up a post-roll ad
        //val postRoll = AdItem("post", fourthAdSource)

        return  AdvertisingConfig(preRoll/*, midRoll, postRoll*/)
    }

    private fun shouldShowAds(videoModel: Card?): Boolean? {
        return if (videoModel?.isShowLiveTag() == true) {
            shouldShowAdForLive()
        } else {
            shouldShowAdForVod()
        }
    }

    /** For Live  */
    private fun shouldShowAdForLive(): Boolean? {
        Timber.d("AdConfig: shouldShowAdForLive ")
        return try {
            val enableLiveDfpForAll :Boolean? = GlobalTVConfig.tvConfig?.enableLiveDfpForAll
            val mainConfigForLive: Boolean ? = GlobalTVConfig.adsConfig?.androidtv?.main_config?.enable_ads_for_live
            if (enableLiveDfpForAll== false || mainConfigForLive == false) {
                false
            } else shouldShowAdForLiveAsPerUserConfig()
        } catch (e: Exception) {
            // Main Config is not available
            Timber.e("AdConfig: enable_ads_for_live main_config missing")
            GlobalConstants.DEFAULT_SHOW_ADS_FOR_LIVE
        }
    }

    private fun shouldShowAdForLiveAsPerUserConfig(): Boolean? {
        Timber.d("AdConfig: shouldShowAdForLiveAsPerUserConfig >> ${UserModel.ads_category}")
        return try {
            when(UserModel.ads_category.lowercase()){
                "paid"-> {
                    GlobalTVConfig.adsConfig?.androidtv?.user_config?.paid?.enable_ads_for_live
                }
                else  -> {
                    GlobalTVConfig.adsConfig?.androidtv?.user_config?.free?.enable_ads_for_live
                }
            }
        } catch (e: Exception) {
            Timber.e("AdConfig: enable_ads_for_live user_category missing from Ad Config")
            GlobalConstants.DEFAULT_SHOW_ADS_FOR_LIVE
        }
    }


    /** For Vod  */
    private fun shouldShowAdForVod(): Boolean? {
        Timber.d("AdConfig: shouldShowAdForVod ")
        return try {
            val enableVodDfpForAll :Boolean? = GlobalTVConfig.tvConfig?.enableVodDfpForAll
            val mainConfigForVod: Boolean? = GlobalTVConfig.adsConfig?.androidtv?.main_config?.enable_ads_for_vod
            if (enableVodDfpForAll == false || mainConfigForVod == false) {
                false
            } else shouldShowAdForVodAsPerUserConfig()
        } catch (e: Exception) {
            // Main Config is not available
            Timber.e("AdConfig: enable_ads_for_vod main_config missing")
            GlobalConstants.DEFAULT_SHOW_ADS_FOR_VOD
        }
    }

    private fun shouldShowAdForVodAsPerUserConfig(): Boolean? {
        Timber.d("AdConfig: shouldShowAdForVodAsPerUserConfig >> ${UserModel.ads_category}")
        return try {
            when(UserModel.ads_category.lowercase()){
                "paid"-> {
                    GlobalTVConfig.adsConfig?.androidtv?.user_config?.paid?.enable_ads_for_vod
                }
                else  -> {
                    GlobalTVConfig.adsConfig?.androidtv?.user_config?.free?.enable_ads_for_vod
                }
            }
        } catch (e: Exception) {
            Timber.e("AdConfig: enable_ads_for_vod user_category missing from Ad Config")
            GlobalConstants.DEFAULT_SHOW_ADS_FOR_VOD
        }
    }

    //For Bitmovin analytics
    private fun initBitmovinAnalytics() {
        bitmovinAnalyticsConfig = BitmovinAnalyticsConfig(resources.getString(R.string.ANALYTICS_LICENSE_KEY), resources.getString(R.string.PLAYER_LICENSE_KEY))
        analyticsCollector = BitmovinPlayerCollector(bitmovinAnalyticsConfig, applicationContext)
        analyticsCollector.addDebugListener(object : BitmovinAnalytics.DebugListener{
            override fun onDispatchAdEventData(data: AdEventData) {
                Timber.d("BitmovinAnalytics >> onDispatchAdEventData adTitle ${data.adTitle}")
            }

            override fun onDispatchEventData(data: EventData) {
                Timber.d("BitmovinAnalytics >> onDispatchEventData  userId ${data.customUserId} >  ${data.customData1} >  ${data.customData2} ")
            }

            override fun onMessage(message: String) {
                Timber.d("BitmovinAnalytics >> onMessage $message")
            }

        })
        Timber.d(
            "BitmovinAnalytics ANALYTICS_LICENSE_KEY > ${resources.getString(R.string.ANALYTICS_LICENSE_KEY)}  > PLAYER_LICENSE_KEY>  ${
                resources.getString(
                    R.string.PLAYER_LICENSE_KEY
                )
            }"
        )
        Timber.d("BitmovinAnalytics --------------------------------------------------------------------------")
    }

    private fun attachAnalytics() {
        if(this::analyticsCollector.isInitialized){
            analyticsCollector.attachPlayer(playerUi.player)
            Timber.d("BitmovinAnalytics attachAnalytics impressionId >> ${analyticsCollector.impressionId}")
        }
    }

    private fun detachAnalytics() {
        if(this::analyticsCollector.isInitialized){
            Timber.d("BitmovinAnalytics detachAnalytics")
            analyticsCollector.detachPlayer()
        }
    }

    /**
     * Update your config with new optional parameters
     * related to the new video playback
     */
    private fun updateOptionalParams(){
        card?.let {
            if (this::bitmovinAnalyticsConfig.isInitialized) {
                //predefine attribute analytics
                bitmovinAnalyticsConfig.customUserId = PrefRepository(application).getUserID()
                bitmovinAnalyticsConfig.title = it.title
                bitmovinAnalyticsConfig.cdnProvider = getCDNProvider(it)
                bitmovinAnalyticsConfig.videoId = "${it.content_id}"
                bitmovinAnalyticsConfig.isLive = it.isShowLiveTag()
                if(bitmovinAnalyticsConfig.isLive == false) {
                    bitmovinAnalyticsConfig.experimentName = GlobalTVConfig.currentPage  // send the <screen name> from where video has played
                }
                //custom attribute analytics
                bitmovinAnalyticsConfig.customData1 = "${PrefRepository(application).getUserSubscribed()}"
                bitmovinAnalyticsConfig.customData2 = it.content_type
                bitmovinAnalyticsConfig.customData3 = it.sub_title  //for match name
                bitmovinAnalyticsConfig.customData4 = it.series_name
                bitmovinAnalyticsConfig.customData5 = PrefRepository(application).getCustomerType()

            }
        }
    }

    private fun getCDNProvider(card: Card): String {
        //vod >> bitgravity and live >> akamai"
        return if (card.isShowLiveTag()) {
            CDNProvider.AKAMAI
        } else {
            CDNProvider.BITGRAVITY
        }
    }

    private fun setupPollerForLive(contentId: Int?) {
        if (!isLive) {
            return
        }
        if (GlobalTVConfig.isPollerEnabled() == false) {
            return
        }

        val oneDayMillis = 86400000.toLong()
        val pollerInterval = WiConfig.pollerInterval.toLong() * 1000

        pollerTimer = object: CountDownTimer(oneDayMillis, pollerInterval) {
            override fun onTick(millisUntilFinished: Long) {
                sendPollerRequest(contentId)
            }

            override fun onFinish() {}
        }

        pollerTimer?.start()
    }
    private fun sendPollerRequest(contentId: Int?) {
        playbackViewmodel.getPollerRequest(contentId.toString(),pollerGuid)


    }

    //end of Bitmovin analytics


    /**
     * Attach all loggers to their respective components
     */
    private fun attachLogger(source: Source) {
        if (BuildConfig.DEBUG) {
            playerUi.player.let {
                sourceLogger.attach(source)
                playerLogger.attach(it)
                viewLogger.attach(it)
            }
        }
    }

    fun showErrorPage(error: String?) {
        binding.loadingview.layoutLoading.hide()
        if(!error.isNullOrEmpty() ){
            showError(binding.root, ErrorType.NONE, MessageConfig.videoNotFound, error , backBtnListener = {onBackPressed()},btnText = "Back")
        }else{
            showError(binding.root, ErrorType.NO_VIDEO_FOUND,  backBtnListener = {onBackPressed()},btnText = "Back")
        }
    }



    fun getStreamingUrlForTVE(requestParams: Map<String, String>, mid: String) {

        Timber.d("getStreamingUrlForTVE :: $requestParams")
        val playerRequestModel = getPlayerReqModelFromHashMap(requestParams,this)
        playerRequestModel.url = card?.getTargetUrl()


        getStreamingUrlForPlayer(playerRequestModel)

    }


}