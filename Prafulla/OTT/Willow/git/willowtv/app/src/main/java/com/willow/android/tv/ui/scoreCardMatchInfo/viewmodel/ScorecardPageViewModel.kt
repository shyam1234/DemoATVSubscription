package com.willow.android.tv.ui.scoreCardMatchInfo.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.willow.android.R
import com.willow.android.tv.common.base.BaseAndroidViewModel
import com.willow.android.tv.data.repositories.RepositoryFactory
import com.willow.android.tv.data.repositories.scoreCard.datamodel.BatsmenInningSummary
import com.willow.android.tv.data.repositories.scoreCard.datamodel.BowlerInningSummary
import com.willow.android.tv.data.repositories.scoreCard.datamodel.Detail
import com.willow.android.tv.data.repositories.scoreCard.datamodel.Extras
import com.willow.android.tv.data.repositories.scoreCard.datamodel.Inning
import com.willow.android.tv.ui.scoreCardMatchInfo.model.MatchInfoData
import com.willow.android.tv.ui.scoreCardMatchInfo.model.MatchInfoParaData
import com.willow.android.tv.ui.scoreCardMatchInfo.model.MatchInfoRow
import com.willow.android.tv.ui.scoreCardMatchInfo.model.ScorecardExtra
import com.willow.android.tv.ui.scoreCardMatchInfo.model.ScorecardHeader
import com.willow.android.tv.ui.scoreCardMatchInfo.model.ScorecardRow
import com.willow.android.tv.ui.scoreCardMatchInfo.model.ScorecardTableData
import com.willow.android.tv.ui.scoreCardMatchInfo.model.ScorecardTotal
import com.willow.android.tv.ui.scoreCardMatchInfo.model.TableStructure
import com.willow.android.tv.utils.GlobalConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScorecardPageViewModel(
    application: Application
) :
    BaseAndroidViewModel(application) {
    var scorecardData: MutableLiveData<List<ScorecardTableData>> = MutableLiveData()
    var matchInfoData: MutableLiveData<MatchInfoData> = MutableLiveData()

    fun makeScorecardPageDataRequest(context: Context?, url: String) {
        val data = RepositoryFactory.getScorecardPageRepository()
            .getScorecardPageData(getApplication(), true)
        viewModelScope.launch(Dispatchers.IO) {
            scorecardData.postValue(setdata(data.getScorecardPageDetails(url).data?.result?.Innings))
        }
    }

    private fun setdata(_scorecardData: List<Inning>?): List<ScorecardTableData>? {
        return _scorecardData?.map {
            ScorecardTableData(
                tabName = it.innName,
                bowlingTable = getbowlingTable(it.BowlerInningSummaries),
                battingTable = getBattingTableData(
                    "${it.BattingTeam} ${it.TotalRuns}/${it.Wickets} (${it.Overs})",
                    it.BatsmenInningSummaries,
                    it.Extras, getScorecardTotal(it.Wickets, it.Overs, it.TotalRuns, it.RunRate)
                ),
                FallOfWicket = it.FOWData.joinToString ("," )
            )

        }
    }

    //
    private fun getbowlingTable(bowlerInningSummaries: List<BowlerInningSummary>): TableStructure {
        return TableStructure(
            countryName = "bowling team",
            tableData = mutableListOf<Any>().apply {
                add(getScorecardHeader(false))
                addAll(getBowinglingRowdRow(bowlerInningSummaries, false))
            }
        )
    }

    //
    private fun getBattingTableData(
        batsmenInningName: String,
        batsmenInningSummaries: List<BatsmenInningSummary>,
        extras: Extras,
        scorecardTotal: ScorecardTotal
    ): TableStructure {
        return TableStructure(
            countryName = batsmenInningName,
            tableData = mutableListOf<Any>().apply {
                add(getScorecardHeader(true))
                addAll(getScorecardRow(batsmenInningSummaries, true))
                add(getScorecardExtra(extras))
                add(scorecardTotal)
            }

        )
    }

    //
    private fun getScorecardTotal(
        wickets: String,
        overs: String,
        totalRuns: String,
        runRate: String
    ): ScorecardTotal {
        return ScorecardTotal(
            Header = getString(R.string.total),
            discription = "($wickets Wickets, $overs Over)",
            Score1 = totalRuns,
            Score2 = "RR:$runRate"
        )
    }

    private fun getScorecardExtra(extras: Extras): ScorecardExtra {
        return ScorecardExtra(
            Header = "Extra",
            discription = "(lb ${extras.lb}, W${extras.wd}",
            Score1 = extras.total

        )
    }

    //
    private fun getScorecardRow(
        batsmenInningSummaries: List<BatsmenInningSummary>,
        isBatting: Boolean
    ): List<ScorecardRow> {
        return batsmenInningSummaries.map {
            ScorecardRow(
                isBigTable = isBatting,
                Header = it.Name,
                discription = it.FallOfWicket,
                Score1 = it.Runs,
                Score2 = it.Balls,
                Score3 = it.Fours,
                Score4 = it.Sixes,
                Score5 = it.StrikeRate
            )
        }
    }

    private fun getBowinglingRowdRow(
        batsmenInningSummaries: List<BowlerInningSummary>,
        isBatting: Boolean
    ): List<ScorecardRow> {
        return batsmenInningSummaries.map {
            ScorecardRow(
                isBigTable = isBatting,
                Header = it.Name,
                discription = "",
                Score1 = it.Overs,
                Score2 = it.Maidens,
                Score3 = it.Runs,
                Score4 = it.Wickets,
                Score5 = it.Economy
            )
        }
    }

    private fun getScorecardHeader(isBating: Boolean): ScorecardHeader {
        return ScorecardHeader(
            isBatting = isBating
        )
    }

    fun makeMatchInfoPageDataRequest(context: Context?, url: String) {
        val data = RepositoryFactory.getScorecardPageRepository()
            .getScorecardPageData(getApplication(), true)
        viewModelScope.launch(Dispatchers.IO) {
            matchInfoData.postValue(getMatchInfoData(data.getMatchInfoPageDetails(url).data?.result?.detail))
        }
    }

    private fun getMatchInfoData(matchInfo: Detail?) = MatchInfoData(
        title = matchInfo?.title,
        rowData = getMAtchRowData(matchInfo),
        paraData = getMatchPAraData(matchInfo)
    )

    private fun getMatchPAraData(matchInfo: Detail?): List<MatchInfoParaData> {
        val matchData =
            matchInfo?.tabs?.find { it.tab_type == GlobalConstants.MatchCenterTab.MATCH_INFO }?.match_data
        return mutableListOf<MatchInfoParaData>().apply {
            add(MatchInfoParaData(matchInfo?.team_one_name, matchData?.getSquad1Data()))
            add(MatchInfoParaData(matchInfo?.team_two_name, matchData?.getSquad2Data()))
        }
    }

    private fun getMAtchRowData(matchInfo: Detail?): List<MatchInfoRow> {
        val matchData =
            matchInfo?.tabs?.find { it.tab_type == GlobalConstants.MatchCenterTab.MATCH_INFO }?.match_data
        return mutableListOf<MatchInfoRow>().apply {
            add(MatchInfoRow(getString(R.string.toss), matchInfo?.toss_won_by.toString()))
            //  add(MatchInfoRow("Series",matchInfo?.series_name.toString()))
            add(MatchInfoRow(getString(R.string.season), matchInfo?.gmt_start_date_ts.toString()))
            add(MatchInfoRow(getString(R.string.player_of_the_match), matchData?.potm.toString()))
            //  add(MatchInfoRow("Match number",matchData?.toss_won_by.toString()))
            add(
                MatchInfoRow(
                    getString(R.string.match_days),
                    matchInfo?.gmt_start_date_ts.toString()
                )
            )
            //  add(MatchInfoRow("Umpires",matchInfo?.toss_won_by.toString()))
            //  add(MatchInfoRow("TV Umpire",matchInfo?.toss_won_by.toString()))
            // add(MatchInfoRow("Reserve Umpire",matchInfo?.toss_won_by.toString()))
            //  add(MatchInfoRow("Reserve Umpire",matchInfo?.toss_won_by.toString()))
            // add(MatchInfoRow("Points",matchInfo?.poi.toString()))
        }
    }
}