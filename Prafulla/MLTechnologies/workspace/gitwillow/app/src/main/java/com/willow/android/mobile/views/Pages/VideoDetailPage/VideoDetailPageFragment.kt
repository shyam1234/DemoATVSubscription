package com.willow.android.mobile.views.pages.videoDetailPage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.willow.android.R
import com.willow.android.databinding.VideoDetailPageFragmentBinding
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.models.video.SuggestedVideosModel
import com.willow.android.mobile.models.video.VideoModel
import com.willow.android.mobile.services.ReloadService
import com.willow.android.mobile.views.pages.PagesNavigator
import com.willow.android.mobile.views.pages.tVELoginPage.TVELoginService
import com.willow.android.mobile.views.pages.wiPlayer.PlayerFullscreenInterface
import com.willow.android.mobile.views.pages.wiPlayer.WiPlayerFragment
import com.willow.android.mobile.views.popup.messagePopup.MessagePopupActivity

private const val VDP_DATA = "VDP_DATA"
private const val SUGGESTED_VIDEOS = "SUGGESTED_VIDEOS"
private const val IS_PLAYLIST = "IS_PLAYLIST"
private const val IS_DEEPLINK_VIDEO = "IS_DEEPLINK_VIDEO"

interface VideoDetailPlaylistInterface {
    fun highlightNextVideo(title: String)
}

class VideoDetailPageFragment : Fragment(), PlayerFullscreenInterface, VideoDetailPageInterface,
    VideoDetailPlaylistInterface {
    private var wiPlayerFragment: WiPlayerFragment? = null

    // Video Models with Proper Stream Url for the users
    private var currentVideoModel: VideoModel? = null   // Current Video Model which player is trying to play
    private var nextReqVideoModel: VideoModel? = null   // Next Video Model which user has requested
    private var suggestedVideos: SuggestedVideosModel? = null

    private var isPlaylist: Boolean = false
    private var isDeeplinkVideo: Boolean = false

    private var videoDetailPageAdapter: VideoDetailPageAdapter? = null
    private var videoDetailPageTabletAdapter: VideoDetailPageTabletAdapter? = null

    companion object {
        fun newInstance() = VideoDetailPageFragment()
        @JvmStatic
        fun newInstance(vdpData: VideoModel, suggestedVideosData: SuggestedVideosModel, isPlaylist: Boolean) =
            VideoDetailPageFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(VDP_DATA, vdpData)
                    putSerializable(SUGGESTED_VIDEOS, suggestedVideosData)
                    putBoolean(IS_PLAYLIST, isPlaylist)
                    putBoolean(IS_PLAYLIST, isDeeplinkVideo)
                }
            }
    }

    private lateinit var viewModel: VideoDetailPageViewModel
    private lateinit var binding: VideoDetailPageFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            nextReqVideoModel = it.getSerializable(VDP_DATA) as? VideoModel
            suggestedVideos = it.getSerializable(SUGGESTED_VIDEOS) as? SuggestedVideosModel
            isPlaylist = it.getBoolean(IS_PLAYLIST)
            isDeeplinkVideo = it.getBoolean(IS_DEEPLINK_VIDEO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = VideoDetailPageFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(VideoDetailPageViewModel::class.java)
        drawPageData()
    }

    override fun onResume() {
        super.onResume()
        if (ReloadService.reloadVideoDetail) {
            drawPageData()
            ReloadService.reloadVideoDetail = false
        }
    }

    private fun drawPageData() {
        initializePlayer()

        if (isDeeplinkVideo) {
            currentVideoModel = nextReqVideoModel
            playSelectedVideo()
        } else {
            fetchStreamForNextReqVideo()
        }
        drawSuggestedVideos()
    }

    private fun drawSuggestedVideos() {
        if (suggestedVideos != null) {
            if (suggestedVideos!!.list.size > 1) {
                binding.suggestedVideoText.visibility = View.VISIBLE
                val isDeviceTablet: Boolean = requireContext().resources.getBoolean(R.bool.isTablet)
                if (isDeviceTablet) {
                    drawSuggestedVideosInTablet(suggestedVideos!!)
                } else {
                    drawSuggestedVideos(suggestedVideos!!)
                }
            }
        }
    }

    private fun initializePlayer() {
        wiPlayerFragment = childFragmentManager.findFragmentById(R.id.wi_player) as WiPlayerFragment?
        wiPlayerFragment?.playerFullscreenInterface = this
    }

    private fun playSelectedVideo() {
        binding.spinner.visibility = View.GONE

        if (currentVideoModel != null) {
            binding.selectedVideoTitle.text = currentVideoModel!!.title
            videoDetailPageAdapter?.highlightSelectedVideo(currentVideoModel!!)
            videoDetailPageTabletAdapter?.highlightSelectedVideo(currentVideoModel!!)

            if ((suggestedVideos != null) && (isPlaylist)) {
                wiPlayerFragment?.setPlaylistItems(suggestedVideos!!.list)
                wiPlayerFragment?.videoDetailPlaylistInterface = this
            } else {
                wiPlayerFragment?.setMediaItem(currentVideoModel!!)
            }
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

    private fun drawSuggestedVideos(suggestedVideos: SuggestedVideosModel) {
        videoDetailPageAdapter = VideoDetailPageAdapter(requireContext(), nextReqVideoModel!!, suggestedVideos, this)
        val categoryLinearLayoutManager = LinearLayoutManager(context)
        categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.videoDetailPageRecycler.layoutManager = categoryLinearLayoutManager
        binding.videoDetailPageRecycler.adapter = videoDetailPageAdapter
    }


    private fun drawSuggestedVideosInTablet(suggestedVideos: SuggestedVideosModel) {
        videoDetailPageTabletAdapter = VideoDetailPageTabletAdapter(requireContext(), nextReqVideoModel!!, suggestedVideos, this)
        val categoryLinearLayoutManager = LinearLayoutManager(context)
        categoryLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.videoDetailPageRecycler.layoutManager = categoryLinearLayoutManager
        binding.videoDetailPageRecycler.adapter = videoDetailPageTabletAdapter
    }

    override fun changeSelectedVideo(videoModel: VideoModel) {
        if (isPlaylist) {
            currentVideoModel = videoModel
            binding.selectedVideoTitle.text = videoModel.title
            videoDetailPageAdapter?.highlightSelectedVideo(videoModel)
            videoDetailPageTabletAdapter?.highlightSelectedVideo(videoModel)
            wiPlayerFragment?.playSelectedPlaylistItem(videoModel)
            return
        }

        val shouldShowAuthScreens = PagesNavigator.showAuthScreensIfRequired(requireContext(), videoModel)
        if (!shouldShowAuthScreens) {
            if (PagesNavigator.isAuthorizedToWatchVideo(videoModel.needLogin, videoModel.needSubscription)) {
                nextReqVideoModel = videoModel
                fetchStreamForNextReqVideo()
            } else {
                PagesNavigator.showPlaybackFailError(requireContext())
            }
        } else {
            ReloadService.reloadVideoDetail = true
        }
    }


    override fun highlightNextVideo(title: String) {
        binding.selectedVideoTitle.text = title
        videoDetailPageAdapter?.highlightNextVideo()
        videoDetailPageTabletAdapter?.highlightNextVideo()
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

    fun showMessage(message: String) {
        binding.spinner.visibility = View.GONE

        val intent = Intent(requireActivity(), MessagePopupActivity::class.java).apply {}
        intent.putExtra("MESSAGE", message)
        startActivity(intent)
    }
}