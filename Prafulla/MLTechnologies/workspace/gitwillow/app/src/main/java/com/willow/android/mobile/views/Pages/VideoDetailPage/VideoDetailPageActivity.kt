package com.willow.android.mobile.views.pages.videoDetailPage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.willow.android.R
import com.willow.android.mobile.models.video.SuggestedVideosModel
import com.willow.android.mobile.models.video.VideoModel

class VideoDetailPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_detail_page_activity)

        val videoData = intent.extras?.getSerializable("VDP_DATA") as VideoModel?
        val suggestedVideos = intent.extras?.getSerializable("SUGGESTED_VIDEOS") as SuggestedVideosModel?
        val isPlaylist = intent.extras?.getSerializable("IS_PLAYLIST") as Boolean?
        val isDeeplinkVideo = intent.extras?.getSerializable("IS_DEEPLINK_VIDEO") as Boolean?

        if (savedInstanceState == null) {
            val videoDetailPageFragment = VideoDetailPageFragment.newInstance()
            val bundle = Bundle().apply {
                putSerializable("VDP_DATA", videoData)
                putSerializable("SUGGESTED_VIDEOS", suggestedVideos)
                putSerializable("IS_PLAYLIST", isPlaylist)
                putSerializable("IS_DEEPLINK_VIDEO", isDeeplinkVideo)
            }
            videoDetailPageFragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, videoDetailPageFragment)
                .commitNow()
        }
    }
}