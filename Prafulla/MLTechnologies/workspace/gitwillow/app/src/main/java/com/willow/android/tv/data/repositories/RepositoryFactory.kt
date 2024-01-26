package com.willow.android.tv.data.repositories

import com.willow.android.tv.data.repositories.explorepage.ExplorePageRepository
import com.willow.android.tv.data.repositories.fixturespage.FixturesPageRepository
import com.willow.android.tv.data.repositories.fixturespage.ScorecardPageRepository
import com.willow.android.tv.data.repositories.helpfaq.HelpFaqPageRepository
import com.willow.android.tv.data.repositories.loginpage.LoginPageRepository
import com.willow.android.tv.data.repositories.mainactivity.MainActivityRepository
import com.willow.android.tv.data.repositories.matchcenterpage.MatchCenterPageRepository
import com.willow.android.tv.data.repositories.player.PlayerRepository
import com.willow.android.tv.data.repositories.pointtable.PointTablePageRepository
import com.willow.android.tv.data.repositories.resultspage.ResultsPageRepository
import com.willow.android.tv.data.repositories.signuppage.SignUpPageRepository
import com.willow.android.tv.data.repositories.tvconfig.TVConfigRepository
import com.willow.android.tv.data.repositories.videospage.VideosPageRepository
import willow.android.tv.data.repositories.IAppBilling.InAppBillingRepository

class RepositoryFactory private constructor(){

    companion object {
         fun getTVConfigRepository() : TVConfigRepository = TVConfigRepository()
         fun getExplorePageRepository(): ExplorePageRepository = ExplorePageRepository()
         fun getLoginPageRepository(): LoginPageRepository = LoginPageRepository()
         fun getSignUpPageRepository(): SignUpPageRepository = SignUpPageRepository()
         fun getVideosPageRepository(): VideosPageRepository = VideosPageRepository()
         fun getFixturesPageRepository(): FixturesPageRepository = FixturesPageRepository()
         fun getScorecardPageRepository(): ScorecardPageRepository = ScorecardPageRepository()
         fun getResultsPageRepository(): ResultsPageRepository = ResultsPageRepository()
         fun getMatchCenterPageRepository(): MatchCenterPageRepository = MatchCenterPageRepository()
         fun getPlayerRepository(): PlayerRepository = PlayerRepository()
         fun getIAppBillingRepository(): InAppBillingRepository = InAppBillingRepository()
        fun getMainActivityRepository(): MainActivityRepository = MainActivityRepository()
        fun getHelpFaqPageRepository(): HelpFaqPageRepository = HelpFaqPageRepository()
        fun getPointTablePageRepository(): PointTablePageRepository = PointTablePageRepository()
    }
}