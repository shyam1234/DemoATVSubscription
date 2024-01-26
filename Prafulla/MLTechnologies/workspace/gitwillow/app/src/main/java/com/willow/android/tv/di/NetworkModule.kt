package com.willow.android.tv.di

import com.google.gson.GsonBuilder
import com.willow.android.tv.data.repositories.InAppBilling.remote.IAppBillingAPIs
import com.willow.android.tv.data.repositories.explorepage.remote.ExplorePageAPIs
import com.willow.android.tv.data.repositories.fixturespage.remote.FixturesPageAPIs
import com.willow.android.tv.data.repositories.fixturespage.remote.ScorecardPageAPIs
import com.willow.android.tv.data.repositories.helpfaq.remote.HelpFaqPageAPIs
import com.willow.android.tv.data.repositories.loginpage.remote.LoginPageAPIs
import com.willow.android.tv.data.repositories.mainactivity.remote.MainActivityAPIs
import com.willow.android.tv.data.repositories.matchcenterpage.remote.MatchCenterPageAPIs
import com.willow.android.tv.data.repositories.player.remote.PlayerAPIs
import com.willow.android.tv.data.repositories.pointtable.remote.PointTablePageAPIs
import com.willow.android.tv.data.repositories.resultspage.remote.ResultsPageAPIs
import com.willow.android.tv.data.repositories.signuppage.remote.SignupPageAPIs
import com.willow.android.tv.data.repositories.tvconfig.remote.TVConfigAPI
import com.willow.android.tv.data.repositories.videospage.remote.VideosPageAPIs
import com.willow.android.tv.utils.config.GlobalTVConfig
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class NetworkModule {
    /**
     * provide the single instance of the Retrofit
     */
    @Singleton
    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient) : Retrofit {
       return  Retrofit.Builder().baseUrl(GlobalTVConfig.getBaseUrl())
           .client(okHttpClient)
           .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
           .build()
    }

    @Singleton
    @Provides
    fun providesOkHTTPLogging() : OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .retryOnConnectionFailure(true)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    /**
     * provides the single instance of the ExplorePageAPIs
     */
    @Singleton
    @Provides
    fun providesExplorePageAPIs(retrofit: Retrofit) : ExplorePageAPIs {
        return  retrofit.create(ExplorePageAPIs::class.java)
    }

    @Singleton
    @Provides
    fun providesVideosPageAPIs(retrofit: Retrofit) : VideosPageAPIs {
        return  retrofit.create(VideosPageAPIs::class.java)
    }

    @Singleton
    @Provides
    fun providesFixturesPageAPIs(retrofit: Retrofit) : FixturesPageAPIs {
        return  retrofit.create(FixturesPageAPIs::class.java)
    }
    @Singleton
    @Provides
    fun providesScorecardPageAPIs(retrofit: Retrofit) : ScorecardPageAPIs {
        return  retrofit.create(ScorecardPageAPIs::class.java)
    }

    @Singleton
    @Provides
    fun providesResultsPageAPIs(retrofit: Retrofit) : ResultsPageAPIs {
        return  retrofit.create(ResultsPageAPIs::class.java)
    }

    @Singleton
    @Provides
    fun providesMatchCenterPageAPIs(retrofit: Retrofit) : MatchCenterPageAPIs {
        return  retrofit.create(MatchCenterPageAPIs::class.java)
    }



    @Singleton
    @Provides
    fun providesIAppBillingAPIs(): IAppBillingAPIs {
        return Retrofit.Builder().baseUrl(GlobalTVConfig.getSubscriptionBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(IAppBillingAPIs::class.java)
    }

    @Singleton
    @Provides
    fun providesTVConfigAPI(okHttpClient: OkHttpClient) : TVConfigAPI {
        return Retrofit.Builder().baseUrl(GlobalTVConfig.getBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(TVConfigAPI::class.java)
    }

    @Singleton
    @Provides
    fun providesLoginPageAPI(okHttpClient: OkHttpClient) : LoginPageAPIs {
        return Retrofit.Builder().baseUrl(GlobalTVConfig.getLoginDynamicBaseUrl())
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
            .build().create(LoginPageAPIs::class.java)
    }

    @Singleton
    @Provides
    fun providesSignUpPageAPI(okHttpClient: OkHttpClient) : SignupPageAPIs {
        return Retrofit.Builder().baseUrl(GlobalTVConfig.getLoginDynamicBaseUrl())
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
            .build().create(SignupPageAPIs::class.java)
    }

    @Singleton
    @Provides
    fun providesPlayerAPI(okHttpClient: OkHttpClient): PlayerAPIs {
        return Retrofit.Builder().baseUrl(GlobalTVConfig.getLoginDynamicBaseUrl())
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
            .build().create(PlayerAPIs::class.java)
    }

    @Singleton
    @Provides
    fun providesMainActivityAPI(okHttpClient: OkHttpClient) : MainActivityAPIs {
        return Retrofit.Builder().baseUrl(GlobalTVConfig.getLoginDynamicBaseUrl())
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
            .build().create(MainActivityAPIs::class.java)
    }

    @Singleton
    @Provides
    fun providesHelpFaqPageAPI(okHttpClient: OkHttpClient): HelpFaqPageAPIs {
        return Retrofit.Builder().baseUrl(GlobalTVConfig.getLoginDynamicBaseUrl())
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
            .build().create(HelpFaqPageAPIs::class.java)
    }

    @Singleton
    @Provides
    fun providesPointTablePageAPI(okHttpClient: OkHttpClient): PointTablePageAPIs {
        return Retrofit.Builder().baseUrl(GlobalTVConfig.getLoginDynamicBaseUrl())
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
            .build().create(PointTablePageAPIs::class.java)
    }
}