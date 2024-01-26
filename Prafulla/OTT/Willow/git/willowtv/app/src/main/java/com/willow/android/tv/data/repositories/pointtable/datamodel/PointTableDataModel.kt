package com.willow.android.tv.data.repositories.pointtable.datamodel


data class PointTableDataModelResponse(
    val result: Result
)

data class Result(
    val event_group_id: Int,
    val series_name: String,
    val standings: List<Standing>,
    val status: String
)

data class Standing(
    val groups: List<Group>, val type: String
)

data class Group(
    val name: String, val team_standings: List<TeamStanding>
)

data class TeamStanding(
    val change: Int,
    val draw: Int,
    val loss: Int,
    val net_run_rate: Double,
    val no_result: Int,
    val played: Int,
    val points: Int,
    val rank: Int,
    val team: Team,
    val win: Int
)

data class Team(
    val id: String, val name: String
)