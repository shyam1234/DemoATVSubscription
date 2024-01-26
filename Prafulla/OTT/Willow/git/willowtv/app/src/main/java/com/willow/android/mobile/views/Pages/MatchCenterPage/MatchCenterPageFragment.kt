package com.willow.android.mobile.views.pages.matchCenterPage


import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.willow.android.R
import com.willow.android.databinding.CompTeamScoreBinding
import com.willow.android.databinding.MatchCenterPageFragmentBinding
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.configs.ResultCodes
import com.willow.android.mobile.configs.WiConfig
import com.willow.android.mobile.models.PreferencesModel
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.models.pages.MatchCenterPageModel
import com.willow.android.mobile.models.pages.MatchCenterResultModel
import com.willow.android.mobile.models.video.VideoModel
import com.willow.android.mobile.services.ReloadService
import com.willow.android.mobile.services.analytics.AnalyticsService
import com.willow.android.mobile.views.pages.PagesNavigator
import com.willow.android.mobile.views.pages.commentaryPage.CommentaryPageActivity
import com.willow.android.mobile.views.pages.scorecardPage.ScorecardPageActivity
import com.willow.android.mobile.views.pages.tVELoginPage.TVELoginService
import com.willow.android.mobile.views.pages.wiPlayer.LivePlayerInterface
import com.willow.android.mobile.views.pages.wiPlayer.PlayerFullscreenInterface
import com.willow.android.mobile.views.pages.wiPlayer.WiPlayerFragment
import com.willow.android.mobile.views.popup.messagePopup.MessagePopupActivity
import com.willow.android.tv.utils.ImageUtility

private const val SERIES_ID = "SERIES_ID_KEY"
private const val MATCH_ID = "MATCH_ID_KEY"
private const val LIVE_PRIORITY = "LIVE_PRIORITY"


class MatchCenterPageFragment : Fragment(), PlayerFullscreenInterface, LivePlayerInterface {
    private lateinit var viewModel: MatchCenterPageViewModel
    private lateinit var binding: MatchCenterPageFragmentBinding

    private var seriesId: String? = null
    private var matchId: String? = null
    private var livePriority: String? = null

    // Video Models with Proper Stream Url for the users
    private var currentVideoModel: VideoModel? = null   // Current Video Model which player is trying to play
    private var nextReqVideoModel: VideoModel? = null   // Next Video Model which user has requested
    private var liveVideoModel: VideoModel? = null   // Live VideoModel if current video is live
    private var wiPlayerFragment: WiPlayerFragment? = null

    // Parameters Used for Live Match
    private var isLive: Boolean = false         // True if the current match is live
    private var isLivePlaying: Boolean = false  // True if the Live Stream in playing in the player
    private var pollerTimer: CountDownTimer? = null
    private var pollerGuid: String = ""
    private var scoreRefreshTimer: CountDownTimer? = null

    companion object {
        @JvmStatic
        fun newInstance(seriesId: String, matchId: String, livePriority: String?) =
            MatchCenterPageFragment().apply {
                arguments = Bundle().apply {
                    putString(SERIES_ID, seriesId)
                    putString(MATCH_ID, matchId)
                    putString(LIVE_PRIORITY, livePriority)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReloadService.reloadHome = true
        arguments?.let {
            seriesId = it.getString(SERIES_ID)
            matchId = it.getString(MATCH_ID)
            livePriority = it.getString(LIVE_PRIORITY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MatchCenterPageFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        initializePlayer()
        loadPageData()
        sendAnalyticsEvent()
    }

    override fun onDestroy() {
        super.onDestroy()

        pollerTimer?.cancel()
        scoreRefreshTimer?.cancel()
    }

    override fun onResume() {
        super.onResume()
        if (ReloadService.reloadMatchCenter) {
            loadPageData()
            ReloadService.reloadMatchCenter = false
        }
    }

    private fun setTeamData(binding: CompTeamScoreBinding, logo: String, name: String, score: String, isWinningTeam: Boolean) {
        ImageUtility.loadImageInto(logo,binding.icon)

        binding.name.text = name
        if (PreferencesModel.showScores) {
            binding.score.text = score
        }

        if (isWinningTeam && PreferencesModel.showResults) {
            binding.name.typeface = Typeface.DEFAULT_BOLD
            binding.score.typeface = Typeface.DEFAULT_BOLD

            binding.name.setTextColor(resources.getColor(R.color.team_won))
            binding.score.setTextColor(resources.getColor(R.color.team_won))
        }
    }

    fun loadPageData() {
        if ((matchId != null) && (seriesId != null)) {
            viewModel = ViewModelProvider(this).get(MatchCenterPageViewModel::class.java)
            viewModel.getMatchCenterData(requireContext(), matchId!!, seriesId!!)

            viewModel.matchCenterData.observe(viewLifecycleOwner, Observer {
                initSelectedVideo(it.result)
                initMatchOverview(it.result)
                initMatchVideos(it)
            })
        } else {
            showMessage(MessageConfig.matchNotFound)
        }
    }
    
    private fun initSelectedVideo(data: MatchCenterResultModel) {
        val clipExtension = data.clipExt

        var livePriorityValue = 1
        if (livePriority != null){
            if (livePriority!!.isNotEmpty()) {
                livePriorityValue = livePriority!!.toInt()
            }
        }
        
        val selectedVideo = VideoModel()
        selectedVideo.setBaseData(data.imageBaseUrl, data.videoBaseUrl, data.clipUrlDict, clipExtension, data.matchTitle)
        selectedVideo.setMatchCenterVideoData(data.selectedVideo, livePriorityValue)
        selectedVideo.setVideoSlugUrl(data.slugBaseUrl, data.slugDict)
        binding.matchCenterSelTitle.text = selectedVideo.title

        // Set initial values of live match
        isLive = selectedVideo.isLive
        isLivePlaying = selectedVideo.isLive
        if (isLive) {
            liveVideoModel = selectedVideo
        }
        setupPollerForLive()

        chooseAuthPlayController(selectedVideo)
    }
    
    private fun initMatchOverview(data: MatchCenterResultModel) {
        binding.matchOverview.matchName.text = data.matchTitle
        binding.matchOverview.seriesName.text = data.seriesName
        if (PreferencesModel.showResults) {
            binding.matchOverview.matchResult.text = data.selectedVideo.matchResult
        }

        setTeamData(binding.matchOverview.teamOne, data.selectedVideo.teamOneLogo, data.selectedVideo.teamOneName, data.selectedVideo.teamOneScore, data.selectedVideo.teamOneWon)
        setTeamData(binding.matchOverview.teamTwo, data.selectedVideo.teamTwoLogo, data.selectedVideo.teamTwoName, data.selectedVideo.teamTwoScore, data.selectedVideo.teamTwoWon)

        if (data.scorecardEnabled && data.commentaryEnabled) {
            binding.scoreComm.root.visibility = View.VISIBLE
        }

        if (data.scorecardEnabled) {
            binding.scoreComm.scorecard.visibility = View.VISIBLE
            binding.scoreComm.scorecard.setOnClickListener(View.OnClickListener {
                wiPlayerFragment?.pausePlayer()
                val intent = Intent(context, ScorecardPageActivity::class.java).apply {}
                intent.putExtra("SERIES_ID", seriesId)
                intent.putExtra("MATCH_ID", matchId)
                startActivity(intent)
            })
        }

        if (data.commentaryEnabled) {
            binding.scoreComm.commentary.visibility = View.VISIBLE
            binding.scoreComm.commentary.setOnClickListener(View.OnClickListener {
                wiPlayerFragment?.pausePlayer()
                val intent = Intent(context, CommentaryPageActivity::class.java).apply {}
                intent.putExtra("SERIES_ID", seriesId)
                intent.putExtra("MATCH_ID", matchId)
                startActivity(intent)
            })

            setupScoreRefreshForLive()
        }

        setLiveMatchButtonListeners()
    }

    private fun setLiveMatchButtonListeners() {
        if (isLive) {
            updateLiveMatchButtonStates()
            binding.matchOverview.liveButton.setOnClickListener {
                if (!isLivePlaying) {
                    isLivePlaying = !isLivePlaying
                    if (liveVideoModel != null) {
                        chooseAuthPlayController(liveVideoModel!!)
                    }
                }
                updateLiveMatchButtonStates()
            }
        }
    }

    private fun updateLiveMatchButtonStates() {
        if (isLive) {
            binding.matchOverview.liveButton.visibility = View.VISIBLE
            if (isLivePlaying) {
                binding.matchOverview.liveButton.setImageResource(R.drawable.live)
            } else {
                binding.matchOverview.liveButton.setImageResource(R.drawable.live_watch)
            }
        } else {
            binding.matchOverview.liveButton.visibility = View.INVISIBLE
        }
    }
    
    private fun initMatchVideos(data: MatchCenterPageModel) {
        binding.spinner.visibility = View.GONE
        val categoryAdapter = MatchCenterPageAdapter(requireContext(), data, this)
        val categoryLinearLayoutManager = LinearLayoutManager(requireContext())
        categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.matchCenterPageRecycler.layoutManager = categoryLinearLayoutManager
        binding.matchCenterPageRecycler.adapter = categoryAdapter
    }

    /** !!!!!!!!!! Conditions of this method Should be similar to PagesNavigator !!!!!!!!!! */
    // Can be called from other fragments as well to play the videos
    fun chooseAuthPlayController(videoModel: VideoModel) {
        nextReqVideoModel = videoModel

        if (PagesNavigator.needToShowLoginForVideo(videoModel.needLogin)) {
            binding.selVideoImage.root.visibility = View.VISIBLE
            binding.selVideoImage.centerTitle.text = MessageConfig.signInToWatch
            ImageUtility.loadImageInto(videoModel.imageUrl,binding.selVideoImage.backgroundImageView)
            binding.selVideoImage.root.setOnClickListener {
                PagesNavigator.launchLoginPage(requireContext())
            }
            wiPlayerFragment?.pausePlayer()
            binding.matchCenterSelTitle.text = videoModel.title
        } else if (PagesNavigator.needToShowSubscriptionForVideo(videoModel.needSubscription)) {
            binding.selVideoImage.root.visibility = View.VISIBLE
            binding.selVideoImage.centerTitle.text = MessageConfig.subscribeToWatch
            ImageUtility.loadImageInto(videoModel.imageUrl,binding.selVideoImage.backgroundImageView)
            binding.selVideoImage.backgroundImageView.setOnClickListener {
                PagesNavigator.launchIAPPage(requireContext())
            }
            wiPlayerFragment?.pausePlayer()
            binding.matchCenterSelTitle.text = videoModel.title
        } else {
            binding.selVideoImage.root.visibility = View.GONE
            fetchStreamForNextReqVideo()
        }
    }
    /** !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */


    /** ^^^^^^^^^^^^^^^^^^^^^ Similar Methods as Video Detail Page ^^^^^^^^^^^^^^^^^^^^^ */
    private fun initializePlayer() {
        wiPlayerFragment = childFragmentManager.findFragmentById(R.id.wi_player) as WiPlayerFragment?
        wiPlayerFragment?.playerFullscreenInterface = this
        wiPlayerFragment?.livePlayerInterface = this
    }

    private fun playSelectedVideo() {
        binding.spinner.visibility = View.GONE
        if (currentVideoModel != null) {
            /** Update Button States for Live Match */
            if (isLive) {
                isLivePlaying = currentVideoModel!!.isLive
                updateLiveMatchButtonStates()
            }

            binding.matchCenterSelTitle.text = currentVideoModel!!.title
            wiPlayerFragment?.setMediaItem(currentVideoModel!!)
        } else {
            showMessage(MessageConfig.playbackFailMsg)
        }
    }

    private fun fetchStreamForNextReqVideo() {
        binding.spinner.visibility = View.VISIBLE
        if (nextReqVideoModel != null) {
            if (nextReqVideoModel!!.isClip) {
                currentVideoModel = nextReqVideoModel
                playSelectedVideo()
            } else if (UserModel.isTVEUser) {
                TVELoginService.initPlayback(nextReqVideoModel!!.contentId, this, requireContext())
            } else {
                viewModel.getStreamData(requireContext(), nextReqVideoModel!!)
                viewModel.streamData.observe(viewLifecycleOwner, Observer {
                    if (it.Videos.size > 0) {
                        nextReqVideoModel!!.streamUrl = it.Videos[0].Url
                        currentVideoModel = nextReqVideoModel
                        playSelectedVideo()
                    } else {
                        showMessage(MessageConfig.playbackFailMsg)
                    }
                })
            }
        } else {
            showMessage(MessageConfig.playbackFailMsg)
        }
    }

    fun makeTVEStreamRequest(token: String) {
        if (nextReqVideoModel != null) {
            viewModel.getTVEStreamData(requireContext(), token, nextReqVideoModel!!)
            viewModel.tveStreamData.observe(viewLifecycleOwner, Observer {
                if (it.Videos.size > 0) {
                    nextReqVideoModel!!.streamUrl = it.Videos[0].Url
                    currentVideoModel = nextReqVideoModel
                    playSelectedVideo()
                } else {
                    showMessage(MessageConfig.playbackFailMsg)
                }
            })
        }
    }
    /** ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ */
    

    fun sendAnalyticsEvent() {
         AnalyticsService.trackFirebaseScreen("AN_MATCH_CENTER_PAGE")
    }

    // ********** Poller Implementation
    private fun setupPollerForLive() {
        if (!isLive) {
            return
        }
        if (!WiConfig.pollerEnabled) {
            return
        }

        val oneDayMillis = 86400000.toLong()
        val pollerInterval = WiConfig.pollerInterval.toLong() * 1000

        pollerTimer = object: CountDownTimer(oneDayMillis, pollerInterval) {
            override fun onTick(millisUntilFinished: Long) {
                sendPollerRequest()
            }

            override fun onFinish() {}
        }

        pollerTimer?.start()
    }

    private fun sendPollerRequest() {
        if (currentVideoModel != null) {
            if (context != null) {
                viewModel = ViewModelProvider(requireActivity()).get(MatchCenterPageViewModel::class.java)
                viewModel.sendPollerRequest(requireContext(), currentVideoModel!!, pollerGuid)
                viewModel.pollerData.observe(viewLifecycleOwner, Observer {
                    if (it.status.equals("N", true)) {
                        activity?.setResult(ResultCodes.POLLER_ERROR)
                        activity?.finish()
                    }

                    if (it.guid.isNotEmpty()) {
                        pollerGuid = it.guid
                    }
                })
            }
        }
    }

    // ********** Live Score Refresh Implementation
    private fun setupScoreRefreshForLive() {
        if (!isLive) { return }

        val oneDayMillis = 86400000.toLong()
        val interval = WiConfig.liveScoreUpdateTimeInterval.toLong() * 1000

        scoreRefreshTimer = object: CountDownTimer(oneDayMillis, interval) {
            override fun onTick(millisUntilFinished: Long) {
                sendScoreRefreshAPIRequest()
            }
            override fun onFinish() {}
        }

        scoreRefreshTimer?.start()
    }

    private fun sendScoreRefreshAPIRequest() {
        if (matchId != null && seriesId != null) {
            if (context != null) {
                viewModel = ViewModelProvider(requireActivity()).get(MatchCenterPageViewModel::class.java)
                viewModel.getScoreRefreshData(requireContext(), matchId!!, seriesId!!)
                viewModel.scoreRefreshData.observe(viewLifecycleOwner, Observer {
                    binding.matchOverview.matchResult.text = it.MatchResult
                    binding.matchOverview.teamOne.score.text = it.t1score
                    binding.matchOverview.teamTwo.score.text = it.t2score
                })
            }
        }
    }

    override fun showFullscreenPlayer() {
        val constraintSet = ConstraintSet()

        constraintSet.clone(binding.root)
        constraintSet.clear(binding.wiPlayer.id)

        constraintSet.connect(binding.wiPlayer.id, ConstraintSet.TOP, binding.root.id, ConstraintSet.TOP)
        constraintSet.connect(binding.wiPlayer.id, ConstraintSet.START, binding.root.id, ConstraintSet.START)
        constraintSet.connect(binding.wiPlayer.id, ConstraintSet.END, binding.root.id, ConstraintSet.END)
        constraintSet.connect(binding.wiPlayer.id, ConstraintSet.BOTTOM, binding.root.id, ConstraintSet.BOTTOM)

        constraintSet.applyTo(binding.root)
    }

    override fun showEmbeddedPlayer() {
        val constraintSet = ConstraintSet()

        constraintSet.clone(binding.root)
        constraintSet.clear(binding.wiPlayer.id)

        constraintSet.connect(binding.wiPlayer.id, ConstraintSet.TOP, binding.root.id, ConstraintSet.TOP)
        constraintSet.connect(binding.wiPlayer.id, ConstraintSet.START, binding.root.id, ConstraintSet.START)
        constraintSet.connect(binding.wiPlayer.id, ConstraintSet.END, binding.root.id, ConstraintSet.END)
        constraintSet.setDimensionRatio(binding.wiPlayer.id, "H, 16:9")

        constraintSet.applyTo(binding.root)
    }

    override fun reloadLiveStream() {
        viewModel.getStreamData(requireContext(), currentVideoModel!!)
        viewModel.streamData.observe(viewLifecycleOwner, Observer {
            if (it.Videos.size > 0) {
                currentVideoModel!!.streamUrl = it.Videos[0].Url
                wiPlayerFragment?.reloadLiveStream(currentVideoModel!!)
            } else {
                showMessage(MessageConfig.playbackFailMsg)
            }
        })
    }

    fun showMessage(message: String) {
        val intent = Intent(requireActivity(), MessagePopupActivity::class.java).apply {}
        intent.putExtra("MESSAGE", message)
        startActivity(intent)
    }
}