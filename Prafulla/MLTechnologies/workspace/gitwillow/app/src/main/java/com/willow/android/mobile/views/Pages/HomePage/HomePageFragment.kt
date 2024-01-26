package com.willow.android.mobile.views.pages.homePage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.willow.android.databinding.HomePageFragmentBinding
import com.willow.android.mobile.MainActivity
import com.willow.android.mobile.models.video.SuggestedVideosModel
import com.willow.android.mobile.services.ReloadService
import com.willow.android.mobile.services.StorageService
import com.willow.android.mobile.services.analytics.AnalyticsService
import com.willow.android.mobile.views.pages.PagesNavigator
import com.willow.android.mobile.views.pages.onboardingPage.OnboardingActivity

class HomePageFragment : Fragment() {
    companion object {
        fun newInstance() = HomePageFragment()
    }

    private lateinit var viewModel: HomePageViewModel
    private lateinit var binding: HomePageFragmentBinding
    private lateinit var refreshContainer: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomePageFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        refreshContainer = binding.refreshContainer
        refreshContainer.setOnRefreshListener {
            loadPageData()
            refreshContainer.isRefreshing = false
        }

        viewModel = ViewModelProvider(this).get(HomePageViewModel::class.java)

        setPageTitle()
        loadPageData()
        sendAnalyticsEvent()

        launchDeepLinkActivity(FirebaseDeeplinkState.receivedUri)

        showOnboardingIfRequired()
    }


    override fun onResume() {
        super.onResume()
        if (ReloadService.reloadHome) {
            loadPageData()
            ReloadService.reloadHome = false
        }
    }

    fun loadPageData() {
        binding.spinner.visibility = View.VISIBLE

        viewModel.getHomeData(requireContext())
        viewModel.homeData.observe(viewLifecycleOwner, Observer {
            binding.spinner.visibility = View.GONE

            val categoryAdapter = HomePageAdapter(this, requireContext(), it, activity as MainActivity)
            val categoryLinearLayoutManager = LinearLayoutManager(requireContext())
            categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            binding.homeRecyclerView.layoutManager = categoryLinearLayoutManager
            binding.homeRecyclerView.adapter = categoryAdapter
        })
    }

    fun sendAnalyticsEvent() {
         AnalyticsService.trackFirebaseScreen("AN_HOME_PAGE")
    }

    fun showOnboardingIfRequired() {
        if (StorageService.getShowOnboard()) {
            StorageService.storeShowOnboard(false)
            val intent = Intent(context, OnboardingActivity::class.java).apply {}
            context?.startActivity(intent)
        } else {
            Log.d("Home: ", "No need to show onboarding")
        }
    }

    private fun setPageTitle() {
        (activity as? MainActivity)?.setPageTitle("Explore")
    }


    /***
     * DeepLinking
     */
    fun launchDeepLinkActivity(deepLink: Uri?) {
        /**
         * Don't do anything if receivedUri value is null
         */
        if (FirebaseDeeplinkState.receivedUri == null) { return }
        FirebaseDeeplinkState.receivedUri = null

        val contentType = deepLink?.getQueryParameter("content_type")
        val matchId = deepLink?.getQueryParameter("match_id")
        val seriesId = deepLink?.getQueryParameter("series_id")
        val clipId = deepLink?.getQueryParameter("clip_id")
        val duration = deepLink?.getQueryParameter("duration")
        val slugUrl = deepLink?.getQueryParameter("slug")

        if (contentType != null) {
            when (contentType.trim().lowercase()) {
                "live" -> {
                    if ((matchId != null) && (seriesId != null)) {
                        launchDeepLinkMatchCenter(matchId, seriesId)
                    }
                }

                "clip" -> {
                    if ((matchId != null) && (seriesId != null) && (clipId != null)) {
                        launchDeepLinkClipVideoDetailPage(matchId, seriesId, clipId)
                    }
                }

                else -> {
                    if ((matchId != null) && (slugUrl != null) && (duration != null)) {
                        launchDeepLinkVideoDetailPage(slugUrl, duration, matchId, contentType)
                    }
                }
            }
        }

        if (contentType.equals("highlight")) {
            Log.d("DeepLink", "It's a highlight")
        }
    }

    fun launchDeepLinkMatchCenter(matchId: String, seriesId: String) {
        val currentActivity = activity
        if (currentActivity is MainActivity) {
            currentActivity.launchMatchCenter(matchId, seriesId)
        }
    }

    fun launchDeepLinkClipVideoDetailPage(matchId: String, seriesId: String, clipId: String) {
        viewModel.getCommentaryData(requireContext(), matchId, seriesId)
        viewModel.commentaryData.observe(viewLifecycleOwner) {
            val foundClipVideoModel = it.findVideoModelFromClipId(clipId)
            if (foundClipVideoModel.isClip) {
                val relatedVideos = it.findRelatedVideoModelsOfClip()
                val suggestedVideosModel = SuggestedVideosModel()
                suggestedVideosModel.setData(relatedVideos)
                PagesNavigator.launchVideoDetailPage(requireContext(), foundClipVideoModel, suggestedVideosModel, false, true)
            } else {
                Log.e("Deeplink", "Couldn't found the clip with provided id")
            }
        }
    }

    fun launchDeepLinkVideoDetailPage(slug: String, duration: String, matchId: String, contentType: String) {
        viewModel.getVideoDetailPage(requireContext(), slug, duration, matchId, contentType)
        viewModel.videoDetailPageData.observe(viewLifecycleOwner) {
            if (it.selectedVideo.streamUrl.isEmpty()) {
                PagesNavigator.showAuthScreensIfRequired(requireActivity(), it.selectedVideo)
            } else {
                val suggestedVideosModel = SuggestedVideosModel()
                suggestedVideosModel.setData(it.relatedVideos)

                PagesNavigator.chooseAuthPlayController(requireActivity(), it.selectedVideo, suggestedVideosModel, true)
            }
        }
    }
}