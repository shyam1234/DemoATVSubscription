package com.willow.android.mobile.models.pages

import java.io.Serializable


object FilterModel {
    var fixturesTeamList: MutableList<FilterTeamModel> = mutableListOf()
    var resultsTeamList: MutableList<FilterTeamModel> = mutableListOf()

    var selectedFixturesTeams: MutableList<String> = mutableListOf()
    var selectedResultsTeams: MutableList<String> = mutableListOf()

    // Temporary selection before clicking the apply button
    var tempFixturesTeamList: MutableList<FilterTeamModel> = mutableListOf()
    var tempResultsTeamList: MutableList<FilterTeamModel> = mutableListOf()

    fun initFixtureInTeamList(teamName: String) {
        if (teamName.isEmpty()) { return }
        val doesContain = fixturesTeamList.any { FilterTeamModel -> FilterTeamModel.name == teamName }

        if (!(doesContain)) {
            var teamModel = FilterTeamModel()
            teamModel.name = teamName
            fixturesTeamList.add(teamModel)
        }

        fixturesTeamList.sortBy { it.name }
    }

    fun updateFixturesTeamList() {
        for (team in fixturesTeamList) {

            if (selectedFixturesTeams.contains(team.name)) {
                team.isSelected = true
            }
        }
    }

    fun updateSelectedFixturesTeams() {
        selectedFixturesTeams.clear()
        for (team in fixturesTeamList) {
            if (team.isSelected) {
                selectedFixturesTeams.add(team.name)
            }
        }
    }

    fun initTempFixturesTeams() {
        tempFixturesTeamList.clear()

        for (team in fixturesTeamList) {
            val tempTeamModel = team.copy()
            tempFixturesTeamList.add(tempTeamModel)
        }
    }

    fun applyFixturesFilterSelections() {
        fixturesTeamList.clear()
        selectedFixturesTeams.clear()

        for (team in tempFixturesTeamList) {
            val tempTeamModel = team.copy()
            fixturesTeamList.add(tempTeamModel)

            if (team.isSelected) {
                selectedFixturesTeams.add(team.name)
            }
        }
    }

    fun clearAllFixtureSelections() {
        selectedFixturesTeams.clear()
        for (team in fixturesTeamList) {
            team.isSelected = false
        }
    }

    // ******************************** Results ********************************
    fun initResultInTeamList(teamName: String) {
        if (teamName.isEmpty()) { return }
        val doesContain = resultsTeamList.any { FilterTeamModel -> FilterTeamModel.name == teamName }

        if (!doesContain) {
            var teamModel = FilterTeamModel()
            teamModel.name = teamName
            resultsTeamList.add(teamModel)
        }

        resultsTeamList.sortBy { it.name }
    }

    fun updateResultsTeamList() {
        for (team in resultsTeamList) {
            if (selectedResultsTeams.contains(team.name)) {
                team.isSelected = true
            }
        }
    }

    fun updateSelectedResultsTeams() {
        selectedResultsTeams.clear()
        for (team in resultsTeamList) {
            if (team.isSelected) {
                selectedResultsTeams.add(team.name)
            }
        }
    }

    fun initTempResultsTeams() {
        tempResultsTeamList.clear()

        for (team in resultsTeamList) {
            val tempTeamModel = team.copy()
            tempResultsTeamList.add(tempTeamModel)
        }
    }

    fun applyResultsFilterSelections() {
        resultsTeamList.clear()
        selectedResultsTeams.clear()

        for (team in tempResultsTeamList) {
            val tempTeamModel = team.copy()
            resultsTeamList.add(tempTeamModel)

            if (team.isSelected) {
                selectedResultsTeams.add(team.name)
            }
        }
    }

    fun clearAllResultSelections() {
        selectedResultsTeams.clear()
        for (team in resultsTeamList) {
            team.isSelected = false
        }
    }
}

// Default value is selected
data class FilterTeamModel(
    var name: String = "",
    var isSelected: Boolean = false
) : Serializable
