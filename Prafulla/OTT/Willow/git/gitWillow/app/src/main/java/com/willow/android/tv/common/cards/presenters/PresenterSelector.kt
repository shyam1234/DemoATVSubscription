package com.willow.android.tv.common.cards.presenters

import android.widget.ImageView
import androidx.leanback.widget.Presenter
import com.willow.android.R
import com.willow.android.WillowApplication
import com.willow.android.tv.common.Types
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.data.repositories.commondatamodel.CardRow
import timber.log.Timber

object PresenterSelector {

    fun getPresenter(cardRow: CardRow, listener: KeyListener): Presenter? {
        Timber.d("PresenterSelector >>> ${cardRow.title} >> content_type: ${cardRow.items?.get(0)?.content_type} >> category: ${cardRow.getItemCategory()} >> cardType: ${cardRow.getCardType()} >> cards: ${cardRow.items?.size} ")
        return when (cardRow.getItemCategory()) {
            Types.CardRowCategory.MATCHES -> {
               MatchCardViewPresenter(listener, getCardSize(cardRow.getCardType()),cardRow)
            }
            Types.CardRowCategory.SERIES -> {
                SeriesCardViewPresenter(listener, getCardSize(cardRow.getCardType()),cardRow)
            }
            Types.CardRowCategory.UPCOMING ->{
                UpcomingCardViewPresenter(listener, getCardSize(cardRow.getCardType()),cardRow)
            }
            Types.CardRowCategory.TEAMS_LIST ->{
                TeamsCardViewPresenter(listener, getCardSize(cardRow.getCardType()),cardRow)
            }
            Types.CardRowCategory.VIDEO -> {
                VideoCardViewPresenter(listener, getCardSize(cardRow.getCardType()),cardRow)
            }else -> { null }
        }
    }


    private fun getCardSize(cardType: Types.Card?): CardSpec {
        return when (cardType) {
            Types.Card.SMALL_LANDSCAPE -> {
                val width = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_small_landscape_width) as Int
                val height = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_small_landscape_height) as Int
               CardSpec(width,height,imagePlaceHolder = R.drawable.default_small_holder)
            }
            Types.Card.LARGE_PORTRAIT -> {
                val width = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_large_portrait_width) as Int
                val height = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_large_portrait_height) as Int
               CardSpec(width,height,imagePlaceHolder = R.drawable.default_large_holder)
            }
            Types.Card.MEDIUM_LANDSCAPE -> {
                val width = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(
                    R.dimen.presenter_card_medium_landscape_width) as Int
                val height = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_medium_landscape_height) as Int
               CardSpec(width,height,imagePlaceHolder = R.drawable.default_medium_holder)
            }
            Types.Card.LEADERBOARD -> {
               // val width = Utils.getScreenWidth() - WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_leaderboard_width_offset) as Int
                val width = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_billboard_width) as Int
                val height = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_leaderboard_height) as Int
                CardSpec(width,height,0, gradient = 8,imagePlaceHolder = R.drawable.default_billboard_holder)
            }
            Types.Card.BILLBOARD -> {
                //val width = Utils.getScreenWidth() - WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_leaderboard_width_offset) as Int
                val width = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_billboard_width) as Int
                val height = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_billboard_height) as Int
                CardSpec(width,height,0, gradient = 8, imagePlaceHolder = R.drawable.default_billboard_holder)
            }
            Types.Card.MEDIUM_LOGO -> {
                val width = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_default_team_width) as Int
                val height = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_default_team_height) as Int
               CardSpec(width,height, imagePlaceHolder = R.drawable.default_team_holder)
            }
            Types.Card.PORTRAIT_TO_LANDSCAPE -> {
                val width = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_medium_portrait_expandable_before_width) as Int
                val height = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_medium_portrait_expandable_height) as Int
               CardSpec(width,height, gradient = 8,imagePlaceHolder = R.drawable.default_shrink_holder, isExpandableCard = true)
            }
            Types.Card.EXPANDED_LANDSCAPE -> {
                val width = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_medium_portrait_expandable_after_width) as Int
                val height = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_medium_portrait_expandable_height) as Int
                CardSpec(width,height, gradient = 8,imagePlaceHolder = R.drawable.default_herobanner, isExpandedCard = true)
            }
            else -> {
                val width = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_medium_landscape_width) as Int
                val height = WillowApplication.instance.applicationContext?.resources?.getDimensionPixelSize(R.dimen.presenter_card_medium_landscape_height) as Int
                CardSpec(width,height,imagePlaceHolder = R.drawable.default_medium_holder)
            }
        }
    }

    data class CardSpec(
        var width:Int,
        var height:Int,
        var marginStart:Int=0,
        var marginEnd:Int=0,
        var marginTop:Int=0,
        var marginBottom:Int=0,
        var scaleType:ImageView.ScaleType = ImageView.ScaleType.FIT_XY,
        var imagePlaceHolder:Int = R.drawable.default_medium_holder,
        var isExpandableCard :Boolean = false,
        var isExpandedCard :Boolean = false,
        var gradient:Int = 0 //0:visible, 8 : gone
    )
}