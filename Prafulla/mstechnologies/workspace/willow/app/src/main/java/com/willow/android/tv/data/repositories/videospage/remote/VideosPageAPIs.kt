package com.willow.android.tv.data.repositories.videospage.remote

import com.willow.android.tv.data.repositories.commondatamodel.CommonCardRow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url


interface VideosPageAPIs {

    @GET
    suspend fun getVideosPagePayload(@Url url: String) : Response<CommonCardRow>
}