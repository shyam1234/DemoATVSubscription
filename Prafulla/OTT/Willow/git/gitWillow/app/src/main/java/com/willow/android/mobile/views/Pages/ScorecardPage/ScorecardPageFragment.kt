package com.willow.android.mobile.views.pages.scorecardPage

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.willow.android.databinding.ScorecardPageFragmentBinding
import com.willow.android.mobile.configs.WiConfig
import com.willow.android.mobile.services.analytics.AnalyticsService


private const val SERIES_ID = "SERIES_ID_KEY"
private const val MATCH_ID = "MATCH_ID_KEY"

class ScorecardPageFragment : Fragment() {
    private var seriesId: String? = null
    private var matchId: String? = null

    private var scoreRefreshTimer: CountDownTimer? = null
    private var isLive: Boolean = false

    companion object {
        @JvmStatic
        fun newInstance(seriesId: String, matchId: String) =
            ScorecardPageFragment().apply {
                arguments = Bundle().apply {
                    putString(SERIES_ID, seriesId)
                    putString(MATCH_ID, matchId)
                }
            }
    }

    private lateinit var viewModel: ScorecardPageViewModel
    private lateinit var binding: ScorecardPageFragmentBinding

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
        binding = ScorecardPageFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        makeDataAPIRequest()
        setupScoreRefreshForLive()
        sendAnalyticsEvent()
    }

    override fun onDestroy() {
        super.onDestroy()
        scoreRefreshTimer?.cancel()
    }

    fun makeDataAPIRequest() {
        binding.spinner.visibility = View.VISIBLE
        if ((matchId != null) && (seriesId != null)) {
            viewModel = ViewModelProvider(requireActivity()).get(ScorecardPageViewModel::class.java)
            viewModel.makeScorecardPageDataRequest(requireContext(), seriesId!!, matchId!!)

            viewModel.scorecardData.observe(viewLifecycleOwner, Observer {
                val fragmentAdapter = ScorecardPageAdapter(childFragmentManager, it)
                isLive = it.result.IsLive
                binding.tabViewPager.adapter = fragmentAdapter
                binding.tabLayout.setupWithViewPager(binding.tabViewPager)

                for (i in 0 until  binding.tabLayout.tabCount) {
                    val tab = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
                    val p = tab.layoutParams as MarginLayoutParams
                    p.setMargins(10, 0, 10, 0)
                    tab.requestLayout()

                    /** Select Latest Inning for Live Match */
                    if (it.result.IsLive && (i == binding.tabLayout.tabCount - 1)) {
                        val lastTab = binding.tabLayout.getTabAt(i)
                        lastTab?.select()
                    }
                }

                setTitleSubtitle(it.result.MatchName, it.result.MatchResult)
                binding.spinner.visibility = View.GONE
            })
        }
    }

    private fun setTitleSubtitle(title: String, subtitle: String) {
        binding.scorecardPageHeader.pageHeaderTitle.text = title
        binding.scorecardPageHeader.pageHeaderSubtitle.text = subtitle
    }

    fun sendAnalyticsEvent() {
         AnalyticsService.trackFirebaseScreen("AN_SCORECARD_PAGE")
    }

    // ********** Live Score Refresh Implementation
    private fun setupScoreRefreshForLive() {
        if (!isLive) {
            return
        }

        val oneDayMillis = 86400000.toLong()
        val interval = WiConfig.liveScorecardUpdateTimeInterval.toLong() * 1000

        scoreRefreshTimer = object: CountDownTimer(oneDayMillis, interval) {
            override fun onTick(millisUntilFinished: Long) {
                makeDataAPIRequest()
            }
            override fun onFinish() {}
        }

        scoreRefreshTimer?.start()
    }
}