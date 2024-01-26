package com.willow.android.mobile.models.pages

import android.util.Log
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.models.video.VideoModel
import com.willow.android.mobile.models.video.VideosSectionModel
import com.willow.android.mobile.utils.Utils
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

class ResultsPageModel {
    var playback_base_url: String = ""
    var thumb_base_url: String = ""
    var slugBaseUrl: String = ""
    var slugDict: JSONObject = JSONObject()
    var result: MutableList<ResultModel> = mutableListOf()
    var results_by_date: MutableList<ResultByDateModel> = mutableListOf()
    var results_by_series: MutableList<ResultBySeriesModel> = mutableListOf()
    var uiResults: MutableList<UIResultModel> = mutableListOf() /** Used only for UI **/
    var shouldHeaderVisible: Boolean = true

    fun setData(data: JSONObject) {
        try {
            playback_base_url = data.getString("playback_base_url")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultsPageModel field: " + "playback_base_url")
        }

        try {
            thumb_base_url = data.getString("thumb_base_url")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultsPageModel field: " + "thumb_base_url")
        }

        try {
            slugBaseUrl = data.getString("VideoSlugBaseUrl")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultsPageModel field: " + "VideoSlugBaseUrl")
        }

        try {
            slugDict = data.getJSONObject("VideoMatchSlugUrls")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultsPageModel field: " + "VideoMatchSlugUrls")
        }

        try {
            val resultsJson = data.getJSONArray("result")
            for (i in 0 until resultsJson.length()) {
                val resultJson = resultsJson[i] as? JSONObject
                if (resultJson != null) {
                    val resultModel = ResultModel()
                    resultModel.setData(thumb_base_url, playback_base_url, slugBaseUrl, slugDict,  resultJson)
                    result.add(resultModel)
                }
            }
        } catch (e: Exception) {
            Log.e("FieldError:", "FixturesPageModel field: " + "fixtures")
        }

        createRequiredModels()
    }

    fun createRequiredModels() {
        initTeamsInFilter()
        createResultsByDate()
        createResultsBySeries()
        createUIResults()
    }

    private fun createResultsByDate() {
        var initialTimestamp = 0L
        var byDateModel = ResultByDateModel()

        for (resultUnit in result) {
            // Don't add if team is not selected in filter list
            if (shouldCheckFilterTeams()) {
                if (!shouldAddInFilteredResults(resultUnit)) { continue }
            }

            if (!Utils.isAllowedInCountry(resultUnit.CC)) {
                continue
            }

            if (initialTimestamp == 0L) {
                initialTimestamp = resultUnit.start_date_time_ts
                byDateModel.initialize(resultUnit.start_date_time)
                byDateModel.results.add(resultUnit)
                results_by_date.add(byDateModel)
                continue
            }

            if (Utils.areDatesEqual(initialTimestamp, resultUnit.start_date_time_ts)) {
                byDateModel.results.add(resultUnit)
            } else {
                initialTimestamp = resultUnit.start_date_time_ts
                byDateModel = ResultByDateModel()
                byDateModel.initialize(resultUnit.start_date_time)
                byDateModel.results.add(resultUnit)
                results_by_date.add(byDateModel)
            }
        }
    }

    private fun createResultsBySeries() {
        for (resultUnit in result) {
            val uiResultUnit = UIResultModel(resultUnit, UIResultModel.TYPE_RESULT_ITEM)

            // Don't add if team is not selected in filter list
            if (shouldCheckFilterTeams()) {
                if (!shouldAddInFilteredResults(resultUnit)) { continue }
            }

            if (!Utils.isAllowedInCountry(resultUnit.CC)) {
                continue
            }

            val isAddedInExistingSeries = addInExistingSeries(uiResultUnit)
            if (!isAddedInExistingSeries) {
                val bySeriesModel = ResultBySeriesModel()
                bySeriesModel.initializeSeries(resultUnit)
                results_by_series.add(bySeriesModel)
            }
        }

        results_by_series.sortByDescending { it.startDateTS }
    }

    private fun addInExistingSeries(uiResultUnit: UIResultModel): Boolean {
        for (series in results_by_series) {
            if (series.series_id.equals(uiResultUnit.result.series_id)) {
                series.results.add(uiResultUnit)
                return true
            }
        }
        return false
    }

    private fun createUIResults() {
        for (series in results_by_series) {
            for (uiResult in series.results) {
                if (uiResult.type == UIResultModel.TYPE_RESULT_HEADER) {
                    uiResults.add(uiResult)
                }
            }
        }
    }

    /**
     * Filter Helpers
     */
    private fun initTeamsInFilter() {
        for (resultUnit in result) {
            FilterModel.initResultInTeamList(resultUnit.team_one_fname)
            FilterModel.initResultInTeamList(resultUnit.team_one_fname)
        }

        FilterModel.updateResultsTeamList()
        FilterModel.updateSelectedResultsTeams()
    }

    private fun shouldCheckFilterTeams() : Boolean {
        if (FilterModel.selectedResultsTeams.size > 0) { return true }
        return false
    }

    private fun shouldAddInFilteredResults(result: ResultModel) : Boolean {
        if (FilterModel.selectedResultsTeams.contains(result.team_one_fname)) { return true }
        if (FilterModel.selectedResultsTeams.contains(result.team_two_fname)) { return true }

        return false
    }
}

class ResultByDateModel {
    var date: String = ""
    val results: MutableList<ResultModel> = mutableListOf<ResultModel>()

    fun initialize(startDate: String) {
        date = startDate
    }
}

class ResultBySeriesModel {
    var series_id: String = ""
    var results: MutableList<UIResultModel> = mutableListOf()
    var position: Int = 0
    var startDateTS: Long = 0L
    var text: String? = null

    /** Add Header in the list while creating a new model */
    fun initializeSeries(resultUnit: ResultModel) {
        series_id = resultUnit.series_id
        startDateTS = resultUnit.series_start_ts

        val headerResult = UIResultModel(resultUnit, UIResultModel.TYPE_RESULT_HEADER)
        results.add(headerResult)

        val itemResult = UIResultModel(resultUnit, UIResultModel.TYPE_RESULT_ITEM)
        results.add(itemResult)
    }
}


/**
 * Fixture Model used in Results by Series Section UI
 */
class UIResultModel(val result: ResultModel, val type: Int) : Serializable {
    var isExpanded: Boolean = false

    companion object {
        const val TYPE_RESULT_HEADER = 0
        const val TYPE_RESULT_ITEM = 1
    }
}

class ResultModel {
    var CC: String = ""
    var commentaryEnabled: Boolean = false
    var scorecardEnabled: Boolean = false
    var is_live: Boolean = false
    var match_center_present: Boolean = true
    var match_id: String = ""
    var match_name: String = ""
    var match_result: String = ""
    var match_short_name: String = ""
    var matchTitle: String = ""
    var series_id: String = ""
    var series_info: JSONArray = JSONArray()
    var series_name: String = ""
    var short_score: ShortScore = ShortScore()
    var start_date_time: String = ""
    var start_date_time_ts: Long = 0L
    var series_start_ts: Long = 0L
    var team_one_fname: String = ""
    var team_one_logo: String = ""
    var team_one_name: String = ""
    var team_one_score: String = ""
    var team_two_fname: String = ""
    var team_two_logo: String = ""
    var team_two_name: String = ""
    var team_two_score: String = ""
    var tve_only_series: Boolean = false
    var winning_team_short_name: String = ""

    var result_with_name: String = ""
    var seriesSubtitle: String = ""
    var teamOneWon: Boolean = false
    var teamTwoWon: Boolean = false
    var videos: MutableList<VideoModel> = mutableListOf()

    var videosSectionModel: VideosSectionModel = VideosSectionModel()
    var sources: MultipleLiveSourcesModel = MultipleLiveSourcesModel()

    fun setData(imageBaseUrl: String, videoBaseUrl: String, slugBaseUrl: String, slugDict: JSONObject, data: JSONObject) {
        try {
            CC = data.getString("CC")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "CC")
        }

        try {
            scorecardEnabled = data.getBoolean("bscard")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "bscard")
        }

        try {
            commentaryEnabled = data.getBoolean("bcomm")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "bcomm")
        }

        try {
            is_live = data.getBoolean("is_live")
        } catch (e: Exception) {}

        try {
            match_center_present = data.getBoolean("match_center_present")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "match_center_present")
        }

        try {
            match_id = data.getString("match_id")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "match_id")
        }

        try {
            match_name = data.getString("match_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "match_name")
        }

        try {
            match_result = data.getString("match_result")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "match_result")
        }

        try {
            match_short_name = data.getString("match_short_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "match_short_name")
        }

        try {
            matchTitle = data.getString("match_s_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "match_s_name")
        }

        try {
            series_id = data.getString("series_id")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "series_id")
        }

        try {
            series_name = data.getString("series_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "series_name")
        }

        try {
            val shortScoreValue = data.getJSONObject("short_score")
            short_score.setData(shortScoreValue)
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "short_score")
        }

        try {
            start_date_time = data.getString("start_date_time")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "start_date_time")
        }

        try {
            start_date_time_ts = data.getLong("start_date_time_ts")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "start_date_time_ts")
        }

        try {
            series_start_ts = data.getLong("series_start_ts")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "series_start_ts")
        }

        try {
            team_one_fname = data.getString("team_one_fname")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "team_one_fname")
        }

        try {
            val logoValue = data.getString("team_one_logo")
            team_one_logo = imageBaseUrl + logoValue
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "team_one_logo")
        }

        try {
            team_one_name = data.getString("team_one_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "team_one_name")
        }

        try {
            team_two_fname = data.getString("team_two_fname")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "team_two_fname")
        }

        try {
            val logoValue = data.getString("team_two_logo")
            team_two_logo = imageBaseUrl + logoValue
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "team_two_logo")
        }

        try {
            team_two_name = data.getString("team_two_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "team_two_name")
        }

        try {
            tve_only_series = data.getBoolean("tve_only_series")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "tve_only_series")
        }


        try {
            series_info = data.getJSONArray("sh_series_info")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "sh_series_info")
        }

        try {
            winning_team_short_name = data.getString("winning_team_short_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "winning_team_short_name")
        }

        try {
            val videosArray = data.getJSONArray("videos")
            for (i in 0 until videosArray.length()) {
                val videoJson = videosArray[i] as? JSONObject
                if (videoJson != null) {
                    val videoModel = VideoModel()
                    videoModel.setBaseData(imageBaseUrl, videoBaseUrl, "", "", matchTitle = matchTitle)
                    videoModel.setIdsData(match_id, series_id, CC)
                    videoModel.setData(videoJson)
                    videoModel.setVideoSlugUrl(slugBaseUrl, slugDict)
                    videos.add(videoModel)
                }
            }
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "videos")
        }

        try {
            val streamValue = data.getJSONObject("stream")
            sources.setIds(match_id, series_id)
            sources.setData(streamValue)
        } catch (e: Exception) { }

        formatData()
    }

    fun formatData(){
        result_with_name  = match_short_name + ", " + match_result
        start_date_time = Utils.getFormattedDate(timestamp = start_date_time_ts)

        if (short_score.T11InningsScore.isNotEmpty()) {
            team_one_score = short_score.T11InningsScore
        }

        if (short_score.T11InningsOvers.isNotEmpty()) {
            team_one_score = team_one_score + "(" + short_score.T11InningsOvers + ")"
        }

        if (short_score.T12InningsScore.isNotEmpty()) {
            team_one_score = team_one_score + " | " +  short_score.T12InningsScore
        }

        if (short_score.T12InningsOvers.isNotEmpty()) {
            team_one_score = team_one_score + "(" + short_score.T12InningsOvers + ")"
        }

        if (short_score.T21InningsScore.isNotEmpty()) {
            team_two_score = short_score.T21InningsScore
        }

        if (short_score.T21InningsOvers.isNotEmpty()) {
            team_two_score = team_two_score + "(" + short_score.T21InningsOvers + ")"
        }

        if (short_score.T22InningsScore.isNotEmpty()) {
            team_two_score = team_two_score + " | " + short_score.T22InningsScore
        }

        if (short_score.T22InningsOvers.isNotEmpty()) {
            team_two_score = team_two_score + "(" + short_score.T22InningsOvers + ")"
        }

        videosSectionModel.mobile_view = "horizontal_min_grid"
        videosSectionModel.videos = videos


        if (winning_team_short_name.isNotEmpty()) {
            if (winning_team_short_name.equals(team_one_name, true)) {
                teamOneWon = true
            } else if (winning_team_short_name.equals(team_two_name, true)) {
                teamTwoWon = true
            }
        }

        for (i in 0 until series_info.length()) {
            val matches = series_info[i] as? String
            if (!matches.isNullOrEmpty()) {
                if (seriesSubtitle == "") {
                    seriesSubtitle = matches
                } else {
                    seriesSubtitle = seriesSubtitle + " | " + matches
                }
            }
        }

        if (tve_only_series) {
            seriesSubtitle = seriesSubtitle + " | " + MessageConfig.tveOnlyMessage
        }
    }
}

class ShortScore {
    var T11InningsOvers: String = ""
    var T11InningsScore: String = ""
    var T12InningsOvers: String = ""
    var T12InningsScore: String = ""
    var T21InningsOvers: String = ""
    var T21InningsScore: String = ""
    var T22InningsOvers: String = ""
    var T22InningsScore: String = ""
    var result: String = ""

    fun setData(data: JSONObject) {
        try {
            T11InningsOvers = data.getString("T11InningsOvers")
        } catch (e: Exception) {
            Log.e("FieldError:", "ShortScore field: " + "T11InningsOvers")
        }

        try {
            T11InningsScore = data.getString("T11InningsScore")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "T11InningsScore")
        }

        try {
            T12InningsOvers = data.getString("T12InningsOvers")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "T12InningsOvers")
        }

        try {
            T12InningsScore = data.getString("T12InningsScore")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "T12InningsScore")
        }

        try {
            T21InningsOvers = data.getString("T21InningsOvers")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "T21InningsOvers")
        }

        try {
            T21InningsScore = data.getString("T21InningsScore")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "T21InningsScore")
        }

        try {
            T22InningsOvers = data.getString("T22InningsOvers")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "T22InningsOvers")
        }

        try {
            T22InningsScore = data.getString("T22InningsScore")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "T22InningsScore")
        }

        try {
            result = data.getString("result")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "result")
        }
    }
}
