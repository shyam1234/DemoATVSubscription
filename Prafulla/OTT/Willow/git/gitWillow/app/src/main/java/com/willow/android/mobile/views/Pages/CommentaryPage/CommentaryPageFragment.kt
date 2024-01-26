package com.willow.android.mobile.views.pages.commentaryPage


import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.willow.android.databinding.CommentaryPageFragmentBinding
import com.willow.android.mobile.configs.WiConfig
import com.willow.android.mobile.models.pages.CommentaryPageModel
import com.willow.android.mobile.services.analytics.AnalyticsService



private const val SERIES_ID = "SERIES_ID_KEY"
private const val MATCH_ID = "MATCH_ID_KEY"

class CommentaryPageFragment : Fragment() {
    private var seriesId: String? = null
    private var matchId: String? = null

    private var scoreRefreshTimer: CountDownTimer? = null
    private var commentaryPageModel: CommentaryPageModel? = null // Data Model which needs to be referred even after latest score fetch for live match

    companion object {
        @JvmStatic
        fun newInstance(seriesId: String, matchId: String) =
            CommentaryPageFragment().apply {
                arguments = Bundle().apply {
                    putString(SERIES_ID, seriesId)
                    putString(MATCH_ID, matchId)
                }
            }
    }

    private lateinit var viewModel: CommentaryPageViewModel
    private lateinit var binding: CommentaryPageFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            seriesId = it.getString(SERIES_ID)
            matchId = it.getString(MATCH_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CommentaryPageFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(CommentaryPageViewModel::class.java)

        makeDataAPIRequest()
    }

    override fun onDestroy() {
        super.onDestroy()

        scoreRefreshTimer?.cancel()
    }

    fun makeDataAPIRequest() {
        binding.spinner.visibility = View.VISIBLE
        if (matchId != null && seriesId != null) {
            viewModel.getCommentaryData(requireContext(), matchId!!, seriesId!!)
            viewModel.commentaryData.observe(viewLifecycleOwner, Observer {
                commentaryPageModel = it
                renderCommentaryData(it)
                setTitleSubtitle(it.MatchName, it.MatchResult)

                setupScoreRefreshForLive(it.IsLive)
                binding.spinner.visibility = View.GONE
            })

            sendAnalyticsEvent()
        }
    }


    private fun setTitleSubtitle(title: String, subtitle: String) {
        binding.commentaryPageHeader.pageHeaderTitle.text = title
        binding.commentaryPageHeader.pageHeaderSubtitle.text = subtitle
    }

    fun sendAnalyticsEvent() {
         AnalyticsService.trackFirebaseScreen("AN_COMMENTARY_PAGE")
    }

    // ********** Live Score Refresh Implementation
    private fun setupScoreRefreshForLive(isLive: Boolean) {
        if (!isLive) {
            return
        }

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
            viewModel.getScoreRefreshData(requireContext(), matchId!!, seriesId!!)
            viewModel.scoreRefreshData.observe(viewLifecycleOwner, Observer {
                if (commentaryPageModel != null) {
                    val oldCommentaryData = commentaryPageModel!!
                    oldCommentaryData.updateLastOverData(it)
                    val newCommentaryData = oldCommentaryData

                    renderCommentaryData(newCommentaryData)
                }

            })
        }
    }

    private fun renderCommentaryData(updatedModel: CommentaryPageModel) {
        commentaryPageModel = updatedModel

        val fragmentAdapter = CommentaryPageAdapter(childFragmentManager, updatedModel)
        binding.tabViewPager.adapter = fragmentAdapter
        binding.tabLayout.setupWithViewPager(binding.tabViewPager)

        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(10, 0, 10, 0)
            tab.requestLayout()

            /** Select Latest Inning for Live Match */
            if (updatedModel.IsLive && (i == binding.tabLayout.tabCount - 1)) {
                val lastTab = binding.tabLayout.getTabAt(i)
                lastTab?.select()
            }
        }
    }
}