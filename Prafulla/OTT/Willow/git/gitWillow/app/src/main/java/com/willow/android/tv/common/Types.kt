package com.willow.android.tv.common

class Types {

    enum class Content {
        HIGHLIGHT,
        LIVE,
        AD,
        SERIES,
        MATCH,
        UPCOMING_MATCH,
        TEAM,
        CLIP,
        REPLAY
    }

    enum class CountryCode {
        CA, US
    }

    enum class TargetAction {
        VIDEO,
        DETAIL
    }
    enum class TargetType {
        MATCH,
        SERIES,
        PLAYER,
        TEAM
    }
    enum class Card {
        LARGE_PORTRAIT,
        PORTRAIT_TO_LANDSCAPE,
        EXPANDED_LANDSCAPE,
        MEDIUM_LANDSCAPE,
        LARGE_LANDSCAPE,
        SMALL_LANDSCAPE,
        SMALL_PORTRAIT,
        BILLBOARD,
        LEADERBOARD,
        CAROUSEL,
        MEDIUM_LOGO
    }

    enum class CardRowCategory {
        VIDEO,
        SERIES,
        MATCHES,
        UPCOMING,
        TEAMS_LIST
    }

    enum class HeroBanner {
        CAROUSEL,
        VIDEO
    }

    enum class ScreenType {
        EXPLORE,
        VIDEOS,
        FIXTURES,
        RESULTS,
        PROFILE,
        HELP,
        score_url
    }

    enum class ScreenName {
        EXPLORE,
        VIDEOS,
        FIXTURES,
        RESULTS,
        PROFILE,
        HELP
    }

    enum class BaseURLTypes{
        DYNAMICURL,
        STATICURL
    }


}