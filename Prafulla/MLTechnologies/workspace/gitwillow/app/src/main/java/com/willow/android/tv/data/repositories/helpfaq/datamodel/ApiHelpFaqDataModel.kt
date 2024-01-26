package com.willow.android.tv.data.repositories.helpfaq.datamodel

data class ApiHelpDataModel(
    val settings: List<Setting>
)

data class Setting(
    val description: String, val title: String, val url: String
)