package com.willow.android.tv.data.repositories.player

import android.app.Application
import com.willow.android.tv.data.repositories.player.remote.PlayerRemoteDataSource

class PlayerRepository{

    /**
     * Return a GetLoginData from specific repository implementation.
     */
    fun getPlayerData(application: Application): IPlayerConfig {

        return  PlayerRemoteDataSource(application)

    }

}