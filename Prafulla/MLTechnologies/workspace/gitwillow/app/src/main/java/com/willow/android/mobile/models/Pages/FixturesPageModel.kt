package com.willow.android.mobile.models.pages

import android.util.Log
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.services.LocalNotificationService
import com.willow.android.mobile.utils.Utils
import org.json.JSONArray
import org.json.JSONObject


class FixturesPageModel {
    var stadium_base_url: String = ""
    var thumb_base_url: String = ""
    var fixtures: MutableList<FixtureModel> = mutableListOf()
    var fixtures_by_date: MutableList<FixtureByDateModel> = mutableListOf()
    var fixtures_by_series: MutableList<FixtureBySeriesModel> = mutableListOf()
    var uiFixtures: MutableList<UIFixtureModel> = mutableListOf() /** Used only for UI **/
    var shouldHeaderVisible: Boolean = true

    fun setData(data: JSONObject) {
        try {
            stadium_base_url = data.getString("stadium_base_url")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixturesPageModel field: " + "stadium_base_url")
        }

        try {
            thumb_base_url = data.getString("thumb_base_url")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixturesPageModel field: " + "thumb_base_url")
        }

        try {
            val fixturesJson = data.getJSONArray("fixtures")
            for (i in 0 until fixturesJson.length()) {
                val fixtureJson = fixturesJson[i] as? JSONObject
                if (fixtureJson != null) {
                    val fixtureModel = FixtureModel()
                    fixtureModel.setData(thumb_base_url, stadium_base_url, fixtureJson)
                    fixtures.add(fixtureModel)
                }
            }
        } catch (e: Exception) {
            Log.e("FieldError:", "FixturesPageModel field: " + "fixtures")
        }

        createRequiredModels()
    }

    fun createRequiredModels() {
        initTeamsInFilter()
        createFixturesByDate()
        createFixturesBySeries()
        createUIFixtures()
    }

    private fun createFixturesByDate() {
        var initialTimestamp:Long = 0
        var byDateModel = FixtureByDateModel()

        for (fixtureUnit in fixtures) {

            // Don't add if team is not selected in filter list
            if (shouldCheckFilterTeams()) {
                if (!shouldAddInFilteredFixtures(fixtureUnit)) { continue }
            }

            if (!Utils.isAllowedInCountry(fixtureUnit.CC)) {
                continue
            }

            if (initialTimestamp == 0L) {
                initialTimestamp = fixtureUnit.start_date_time_ts
                byDateModel.initialize(fixtureUnit.start_date_time)
                byDateModel.fixtures.add(fixtureUnit)
                fixtures_by_date.add(byDateModel)
                continue
            }

            if (Utils.areDatesEqual(initialTimestamp, fixtureUnit.start_date_time_ts)) {
                byDateModel.fixtures.add(fixtureUnit)
            } else {
                initialTimestamp = fixtureUnit.start_date_time_ts
                byDateModel = FixtureByDateModel()
                byDateModel.initialize(fixtureUnit.start_date_time)
                byDateModel.fixtures.add(fixtureUnit)
                fixtures_by_date.add(byDateModel)
            }
        }
    }

    private fun createFixturesBySeries() {
        for (fixtureUnit in fixtures) {
            val uiFixtureUnit = UIFixtureModel(fixtureUnit, UIFixtureModel.TYPE_FIXTURE_ITEM)

            // Don't add if team is not selected in filter list
            if (shouldCheckFilterTeams()) {
                if (!shouldAddInFilteredFixtures(fixtureUnit)) { continue }
            }

            if (!Utils.isAllowedInCountry(fixtureUnit.CC)) {
                continue
            }

            val isAddedInExistingSeries = addInExistingSeries(uiFixtureUnit)
            if (!isAddedInExistingSeries) {
                val bySeriesModel = FixtureBySeriesModel()
                bySeriesModel.initializeSeries(fixtureUnit)
                fixtures_by_series.add(bySeriesModel)
            }
        }

        fixtures_by_series.sortBy { it.startDateTS }
    }

    private fun addInExistingSeries(uiFixtureUnit: UIFixtureModel): Boolean {
        for (series in fixtures_by_series) {
            if (series.series_id.equals(uiFixtureUnit.fixture.series_id)) {
                series.fixtures.add(uiFixtureUnit)
                return true
            }
        }
        return false
    }

    private fun createUIFixtures() {
        for (series in fixtures_by_series) {
            for (uiFixture in series.fixtures) {
                if (uiFixture.type == UIFixtureModel.TYPE_FIXTURE_HEADER) {
                    uiFixtures.add(uiFixture)
                }
            }
        }
    }

    /**
     * Filter Helpers
     */
    private fun initTeamsInFilter() {
        for (fixture in fixtures) {
            FilterModel.initFixtureInTeamList(fixture.team_one_fname)
            FilterModel.initFixtureInTeamList(fixture.team_one_fname)
        }

        FilterModel.updateFixturesTeamList()
        FilterModel.updateSelectedFixturesTeams()
    }

    private fun shouldCheckFilterTeams() : Boolean {
        if (FilterModel.selectedFixturesTeams.size > 0) { return true }
        return false
    }

    private fun shouldAddInFilteredFixtures(fixture: FixtureModel) : Boolean {
        if (FilterModel.selectedFixturesTeams.contains(fixture.team_one_fname)) { return true }
        if (FilterModel.selectedFixturesTeams.contains(fixture.team_two_fname)) { return true }

        return false
    }
}
class FixtureByDateModel {
    var date: String = ""
    var fixtures: MutableList<FixtureModel> = mutableListOf<FixtureModel>()

    fun initialize(startDate: String) {
        date = startDate
    }
}

class FixtureBySeriesModel {
    var series_id: String = ""
    var startDateTS: Long = 0L
    var fixtures: MutableList<UIFixtureModel> = mutableListOf()

    /** Add Header in the list while creating a new model */
    fun initializeSeries(fixtureUnit: FixtureModel) {
        series_id = fixtureUnit.series_id
        startDateTS = fixtureUnit.series_start_ts

        val headerFixture = UIFixtureModel(fixtureUnit, UIFixtureModel.TYPE_FIXTURE_HEADER)
        fixtures.add(headerFixture)

        val itemFixture = UIFixtureModel(fixtureUnit, UIFixtureModel.TYPE_FIXTURE_ITEM)
        fixtures.add(itemFixture)
    }
}

/**
 * Fixture Model used in Fixtures by Series Section UI
 */
class UIFixtureModel(val fixture: FixtureModel, val type: Int) {
    var isExpanded: Boolean = false

    companion object {
        const val TYPE_FIXTURE_HEADER = 0
        const val TYPE_FIXTURE_ITEM = 1
    }
}

class FixtureModel {
    var CC: String = ""
    var end_date_time: String = ""
    var end_date_time_ts: Long = 0L
    var event_id: String = ""
    var match_id: String = ""
    var match_name: String = ""
    var match_short_name: String = ""
    var series_id: String = ""
    var series_info: JSONArray = JSONArray()
    var series_name: String = ""
    var seriesSubtitle: String = ""
    var stadium_image: String = ""
    var start_date_time: String = ""
    var start_date_time_ts: Long = 0L
    var series_start_ts: Long = 0L
    var team_one_fname: String = ""
    var team_one_logo: String = ""
    var team_two_fname: String = ""
    var team_two_logo: String = ""
    var subtitle: String = ""
    var venue: String = ""
    var tve_only_series: Boolean = false

    var notificationTitle: String = ""
    var isNotificationEnabled: Boolean = false


    fun setData(imageBaseUrl: String, stadiumImageBaseUrl: String, data: JSONObject) {
        try {
            CC = data.getString("CC")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "CC")
        }

        try {
            end_date_time = data.getString("end_date_time")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "end_date_time")
        }

        try {
            end_date_time_ts = data.getLong("end_date_time_ts")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "end_date_time_ts")
        }

        try {
            event_id = data.getString("event_id")
        } catch (e: Exception) {}

        try {
            match_id = data.getString("match_id")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "match_id")
        }

        try {
            match_name = data.getString("match_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "match_name")
        }

        try {
            match_short_name = data.getString("match_short_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "match_short_name")
        }

        try {
            series_id = data.getString("series_id")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "series_id")
        }

        try {
            series_info = data.getJSONArray("series_info")
        } catch (e: Exception) {
            Log.e("FieldError:", "ResultModel field: " + "series_info")
        }

        try {
            series_name = data.getString("series_name")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "series_name")
        }

        try {
            val stadiumImageValue = data.getString("stadium_image")
            stadium_image = stadiumImageBaseUrl + stadiumImageValue
        } catch (e: Exception) {}

        try {
            start_date_time = data.getString("start_date_time")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "start_date_time")
        }

        try {
            start_date_time_ts = data.getLong("start_date_time_ts")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "start_date_time_ts")
        }

        try {
            series_start_ts = data.getLong("series_start_ts")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "series_start_ts")
        }


        try {
            team_one_fname = data.getString("team_one_fname")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "team_one_fname")
        }

        try {
            val logoValue = data.getString("team_one_logo")
            team_one_logo = imageBaseUrl + logoValue
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "team_one_logo")
        }

        try {
            team_two_fname = data.getString("team_two_fname")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "team_two_fname")
        }

        try {
            val logoValue =  data.getString("team_two_logo")
            team_two_logo = imageBaseUrl + logoValue
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "team_two_logo")
        }


        try {
            venue = data.getString("venue")
        } catch (e: Exception) {}

        try {
            tve_only_series = data.getBoolean("tve_only_series")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "tve_only_series")
        }

        try {
            CC = data.getString("CC")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "CC")
        }

        try {
            CC = data.getString("CC")
        } catch (e: Exception) {
            Log.e("FieldError:", "FixtureModel field: " + "CC")
        }

        formatData()
    }

    fun formatData() {
        val formatted_date = Utils.getFormattedDate(timestamp = start_date_time_ts)
        val formatted_time = Utils.getFormattedTime(timestamp = start_date_time_ts)
        start_date_time = formatted_date
        subtitle = match_short_name + " | " + formatted_date + " | " + formatted_time

        notificationTitle = "Watch " + match_short_name
        iuiFixtureNotificationEnabled()

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

    fun iuiFixtureNotificationEnabled() {
        isNotificationEnabled = LocalNotificationService.scheduledNotificationIds[match_id] != null
    }

    fun toggleNotificationSelection() {
        isNotificationEnabled = !isNotificationEnabled
    }
}