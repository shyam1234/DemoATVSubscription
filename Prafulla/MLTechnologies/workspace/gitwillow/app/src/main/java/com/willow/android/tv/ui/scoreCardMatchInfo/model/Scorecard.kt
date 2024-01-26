package com.willow.android.tv.ui.scoreCardMatchInfo.model

data class ScorecardTableData(
    val tabName: String,
    val battingTable: TableStructure,
    val bowlingTable: TableStructure,
    val FallOfWicket: String
) : java.io.Serializable

data class TableStructure(
    var countryName: String,
    val tableData:List<Any> = listOf()
) : java.io.Serializable

data class ScorecardHeader(
    val isBatting: Boolean
) : java.io.Serializable


data class ScorecardRow(
    val isBigTable: Boolean,
    val Header: String,
    val discription: String,
    val Score1: String,
    val Score2: String,
    val Score3: String,
    val Score4: String,
    val Score5: String
) : java.io.Serializable


data class ScorecardExtra(
    val Header: String,
    val discription: String,
    val Score1: String
) : java.io.Serializable


data class ScorecardTotal(
    val Header: String,
    val discription: String,
    val Score1: String,
    val Score2: String
) : java.io.Serializable

