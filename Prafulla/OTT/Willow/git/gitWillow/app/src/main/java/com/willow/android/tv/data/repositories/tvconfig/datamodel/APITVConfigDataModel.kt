package com.willow.android.tv.data.repositories.tvconfig.datamodel

import android.os.Parcelable
import com.willow.android.tv.common.Types
import com.willow.android.tv.utils.config.GlobalTVConfig
import com.willow.android.tv.utils.config.GlobalTVConfig.tvConfig
import kotlinx.parcelize.Parcelize

@Parcelize
data class APITVConfigDataModel(
    val apiConfig: ApiConfig,
    val appEnvironment: String?,
    val appVersion: String?,
    val appVersionCode: Int,
    val couponCodeDaysCount: Int,
    val couponCodeEnabled: Boolean,
    val dfpTag: String?,
    val enableLiveDfpForAll: Boolean,
    val enableVodDfpForAll: Boolean,
    val iapEnabled: Boolean,
    val iapRestoreEnabled: Boolean,
    val inAPProductId: InAPProductId,
    val messages: Messages,
    val navigation: NavigationDataModel,
    val pollerEnabled: Boolean,
    val pollerInterval: Int,
    val liveDataRefreshInterval: Int,
    val settingsPage: String?,
    val tveAuthCheckInterval: Int,
    val tveMsg: String?,
    val tveProvidersJson: String?,
    val tveProvidersList: String?,
    val subscriptionBanner: String?,
    val tveProviders: String?,
    val subscriptionBaseUrl: String?,
    val countryCheckEndPoint: String?,
    val userAdsConfig: String?,
    val helpAndFAQUrl: String?,
    val oldAppVersionNotAllowed:Boolean,
    val newAppVersion:Float,
    val show_watch_later:Boolean,
    val show_bookmark:Boolean,
    val show_cancel_subscription:Boolean,
    val show_manage_subscription:Boolean,
    val continue_watching:Boolean,
    val show_watch_later_api: String?,
    val show_bookmark_api: String?,
    val continue_watching_api: String?,
) : Parcelable

@Parcelize
data class ApiConfig(
    val deeplinkDomain: DeeplinkDomain,
    val dynamicUrl: DynamicUrl,
    val imageUrl: ImageUrl,
    val pollerUrl: PollerUrl,
    val staticUrl: StaticUrl
) : Parcelable


@Parcelize
data class DeeplinkDomain(
    val development: String?,
    val production: String?,
    val staging: String?
) : Parcelable {
    fun getSelectedBuildVariantUrl() : String? {
        return when (tvConfig?.appEnvironment) {
            "staging" -> {
                 staging
            }
            "production" -> {
                 production
            }
            else -> {
                 development
            }
        }
    }
}

@Parcelize
data class DynamicUrl(
    val development: String?,
    val production: String?,
    val staging: String?
) : Parcelable {
    fun getSelectedBuildVariantUrl() : String? {
        return when (tvConfig?.appEnvironment) {
            "staging" -> {
                 staging
            }
            "production" -> {
                 production
            }
            else -> {
                 development
            }
        }
    }
}

@Parcelize
data class ImageUrl(
    val development: String?,
    val production: String?,
    val staging: String?
) : Parcelable {
    fun getSelectedBuildVariantUrl() : String? {
        return when (tvConfig?.appEnvironment) {
            "staging" -> {
                 staging
            }
            "production" -> {
                 production
            }
            else -> {
                 development
            }
        }
    }
}

@Parcelize
data class PollerUrl(
    val development: String?,
    val production: String?,
    val staging: String?
) : Parcelable {
    fun getSelectedBuildVariantUrl() : String? {
        return when (tvConfig?.appEnvironment) {
            "staging" -> {
                 staging
            }
            "production" -> {
                 production
            }
            else -> {
                 development
            }
        }
    }
}

@Parcelize
data class StaticUrl(
    val development: String?,
    val production: String?,
    val staging: String?
) : Parcelable {
    fun getSelectedBuildVariantUrl() : String? {
        return when (tvConfig?.appEnvironment) {
            "staging" -> {
                 staging
            }
            "production" -> {
                 production
            }
            else -> {
                 development
            }
        }
    }
}

@Parcelize
data class NavigationDataModel(
    val navigationTabs: List<NavigationTabsDataModel>,
    val navigationTabsOrder: List<String>
) : Parcelable

@Parcelize
data class NavigationTabsDataModel(
    val cc: List<String>,
    val ico: Ico,
    val id: String?,
    val name: String?,
    val navUrl: String?,
    private val screenType: String?,
) : Parcelable{
    fun completeNavURL(): String {
        return GlobalTVConfig.getStaticBaseUrl() + navUrl
    }

    fun getScreenType() : Types.ScreenType?{
        return try {
            screenType?.uppercase()?.let { Types.ScreenType.valueOf(it) }
        }catch (ex:Exception){
            null
        }
    }
}



@Parcelize
data class CA(
    val productId: String?,
    val productPrice: String?
) : Parcelable

@Parcelize
data class Messages(
    val appleSigninFailure: String?,
    val cannotMakePayments: String?,
    val createAccount: String?,
    val emptyCoupon: String?,
    val emptyCredentials: String?,
    val geoBlock: String?,
    val iapDescription: String?,
    val iapDescriptionDetail: String?,
    val iapError: String?,
    val iapSuccess: String?,
    val inputEmail: String?,
    val inputPassword: String?,
    val matchCenterDataError: String?,
    val matchNotFound: String?,
    val needSubscription: String?,
    val passwordResetFail: String?,
    val playbackFailMsg: String?,
    val pollerDescription: String?,
    val pollerTitle: String?,
    val tveLoginInstruction: String?,
    val tveOnlyMessage: String?,
    val wrongCoupon: String?,
    val wrongCredentials: String?
) : Parcelable

@Parcelize
data class US(
    val productId: String?,
    val productPrice: String?
) : Parcelable

@Parcelize
data class InAPProductId(
    val CA: List<InappProductDetails>,
    val US: List<InappProductDetails>
) : Parcelable

@Parcelize
data class InappProductDetails(
    val productId: String?,
    val title: String?,
    val details: List<String>,
    val productPrice: String?
) : Parcelable
@Parcelize
data class Ico(
    val active: String?,
    val inactive: String?
) : Parcelable