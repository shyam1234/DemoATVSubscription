package com.willow.android.tv.di

import com.willow.android.tv.data.repositories.InAppBilling.remote.IAppBillingRemoteDataSource
import com.willow.android.tv.data.repositories.explorepage.remote.ExplorePageRemoteDataSource
import com.willow.android.tv.data.repositories.fixturespage.remote.FixturesPageRemoteDataSource
import com.willow.android.tv.data.repositories.fixturespage.remote.ScorecardPageRemoteDataSource
import com.willow.android.tv.data.repositories.helpfaq.remote.HelpFaqPageRemoteDataSource
import com.willow.android.tv.data.repositories.loginpage.remote.LoginPageRemoteDataSource
import com.willow.android.tv.data.repositories.mainactivity.remote.MainActivityRemoteDataSource
import com.willow.android.tv.data.repositories.matchcenterpage.remote.MatchCenterPageRemoteDataSource
import com.willow.android.tv.data.repositories.player.remote.PlayerRemoteDataSource
import com.willow.android.tv.data.repositories.pointtable.remote.PointTablePageRemoteDataSource
import com.willow.android.tv.data.repositories.resultspage.remote.ResultsPageRemoteDataSource
import com.willow.android.tv.data.repositories.signuppage.remote.SignUpPageRemoteDataSource
import com.willow.android.tv.data.repositories.tvconfig.remote.TVConfigRemoteDataSource
import com.willow.android.tv.data.repositories.videospage.remote.VideosPageRemoteDataSource
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, DatabaseModule::class])
interface ApplicationComponent {

    fun inject(explorePageRemoteDataSource: ExplorePageRemoteDataSource)
    fun inject(tvConfigRemoteDataSource: TVConfigRemoteDataSource)
    fun inject(loginPageRemoteDataSource: LoginPageRemoteDataSource)
    fun inject(signUpPageRemoteDataSource: SignUpPageRemoteDataSource)
    fun inject(videosPageRemoteDataSource: VideosPageRemoteDataSource)
    fun inject(fixturesPageRemoteDataSource: FixturesPageRemoteDataSource)
    fun inject(resultsPageRemoteDataSource: ResultsPageRemoteDataSource)
    fun inject(matchCenterPageRemoteDataSource: MatchCenterPageRemoteDataSource)
    fun inject(playerRemoteDataSource: PlayerRemoteDataSource)
    fun inject(scorecardPageRemoteDataSource: ScorecardPageRemoteDataSource)
    fun inject(mainActivityRemoteDataSource: MainActivityRemoteDataSource)
    fun inject(helpFaqPageRemoteDataSource: HelpFaqPageRemoteDataSource)
    fun inject(inAppBillingRemoteDataSource: IAppBillingRemoteDataSource)
    fun inject(pointTablePageRemoteDataSource: PointTablePageRemoteDataSource)
}