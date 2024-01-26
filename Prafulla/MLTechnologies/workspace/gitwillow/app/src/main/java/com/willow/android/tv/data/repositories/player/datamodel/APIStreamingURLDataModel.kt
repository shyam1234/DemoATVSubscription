package com.willow.android.tv.data.repositories.player.datamodel

data class APIStreamingURLDataModel(
    val Videos: List<Video>?,
    val subscribe: Data?,
    val error: Data?
)


data class Video(
    val EventName: String,
    val Image: String,
    val SeriesName: String,
    val Title: String,
    val Url: String,
    val baseURL: String,
    val priority: Int
)


data class Data(
    val title: String,
    val description: String
)