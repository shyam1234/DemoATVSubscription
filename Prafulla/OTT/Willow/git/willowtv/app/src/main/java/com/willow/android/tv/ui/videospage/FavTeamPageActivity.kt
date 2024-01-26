package com.willow.android.tv.ui.videospage

import android.os.Bundle
import com.willow.android.R
import com.willow.android.databinding.ActivityMainBinding
import com.willow.android.tv.common.Types
import com.willow.android.tv.common.base.BaseActivity
import com.willow.android.tv.ui.login.LoginActivity
import com.willow.android.tv.ui.playback.PlaybackActivity
import com.willow.android.tv.ui.playback.PlayerManager
import com.willow.android.tv.ui.subscription.SubscriptionActivity
import com.willow.android.tv.utils.CheckConnection
import com.willow.android.tv.utils.CommonFunctions
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.GlobalConstants
import com.willow.android.tv.utils.GoTo
import com.willow.android.tv.utils.NavigationUtils
import com.willow.android.tv.utils.config.GlobalTVConfig
import com.willow.android.tv.utils.events.CardClickedEvent
import com.willow.android.tv.utils.extension.startActivityWithData
import com.willow.android.tv.utils.extension.startActivityWithOutData
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class FavTeamPageActivity : BaseActivity(){
    private val checkConnection by lazy { CheckConnection(application) }
    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlayerManager.shouldPlayContent = false
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        checkConnection.observe(this@FavTeamPageActivity){
            Timber.d("MainActivity Connection Check:: $it")

            if(!it){
                showError(mBinding.root, ErrorType.NONE,"NO INTERNET",  backBtnListener = {onBackPressed()},btnText = "Back")
            }else{
                hideError(mBinding.root)
            }
        }
        loadVideoFragment()

    }

    private fun loadVideoFragment() {
        NavigationUtils.onReplaceToFragmentContainer(this,
        R.id.fragment_container_view_holder,
        VideosFragment.newInstance(true,null,intent.getStringExtra(GlobalConstants.Keys.URL)))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: CardClickedEvent) {

        Timber.d("onMessageEvent :: $event")
        //given the temp solution to handle the redirect page. We need to use MainFragment instead of activity
        redirectToTarget(event)
    }

    private fun redirectToTarget(event: CardClickedEvent) {
        Timber.d("redirectToTarget target_type :: "+event.card.target_type +" > target_action:: "+event.card.target_action)

        if (event.card.target_type?.uppercase() == Types.TargetType.TEAM.name
            && event.card.target_action?.uppercase() == Types.TargetAction.DETAIL.name
        ) {
            GlobalTVConfig.currentPage = resources.getStringArray(R.array.arrayPages)[7]

            startActivityWithData<FavTeamPageActivity>(
                GlobalConstants.ActivityType.DETAILS_PAGE,
                GlobalTVConfig.getBaseUrl(event.card.base_url_type) + event.card.target_url
            )

        } else if (event.card.target_type?.uppercase() == Types.TargetType.MATCH.name
            && event.card.target_action?.uppercase() == Types.TargetAction.DETAIL.name
        ) {

            //In future, we need to handle this from MainFragment, direct using VideoFragment, without using FavTeamPageActivity
           // redirectToMatchCenter(event)

        } else if (event.card.target_type?.uppercase() == Types.TargetType.SERIES.name
            && event.card.target_action?.uppercase() == Types.TargetAction.DETAIL.name
        ) {
            //In future, we need to handle this from MainFragment, direct using VideoFragment ,without using FavTeamPageActivity
           // redirectToMatchCenter(event)

        } else if (event.card.target_type?.uppercase() == Types.TargetType.PLAYER.name
            && event.card.target_action?.uppercase() == Types.TargetAction.VIDEO.name
        ) {

            redirectToPlayer(event)
        }

    }



    fun redirectToPlayer(event: CardClickedEvent) {
        val whereTo = CommonFunctions.whereToGo(event.card, this)
        when (whereTo) {
            GoTo.LOGIN -> {
                startActivityWithOutData<LoginActivity>()
            }
            GoTo.PLAY_VIDEO -> {
                startActivityWithData<PlaybackActivity>(
                    event.card,
                    event.cardRow
                )
            }
            GoTo.SUBSCRIPTION -> {
                startActivityWithData<SubscriptionActivity>(event.card)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PlayerManager.shouldPlayContent = true
    }
}