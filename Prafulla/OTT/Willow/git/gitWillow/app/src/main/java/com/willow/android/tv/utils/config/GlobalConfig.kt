package com.willow.android.tv.utils.config

import android.text.TextUtils
import com.willow.android.tv.common.Types
import com.willow.android.tv.data.repositories.tvconfig.datamodel.APIDFPConfigDataModel
import com.willow.android.tv.data.repositories.tvconfig.datamodel.APITVConfigDataModel
import com.willow.android.tv.data.repositories.tvconfig.datamodel.InappProductDetails
import com.willow.android.tv.utils.GlobalConstants
import timber.log.Timber
import java.util.Locale


/**
 * this class will have all the constant configuration related to application
 */
object GlobalTVConfig {

    //use this in case of any failure while fetching URL
    private const val DEFAULT_STATIC_BASE_URL = "https://static.willow.tv"
    private const val DEFAULT_DYNAMIC_BASE_URL =  "https://ws.willow.tv"
    private const val DEFAULT_TV_PROVIDER_LOGO_URL = "https://img.willow.tv/apps/tveprovider_iptv_nd.png"
    private const val DEFAULT_COUNTRY_CHECK_END_POINT ="https://ws.willow.tv/countryCode.asp"
    private const val DEFAULT_SUBSCRIPTION_BASEURL ="https://www.willow.tv"
    private const val DEFAULT_ADS_CONFIG ="https://static.willow.tv/apps/conf/userAdsConfig.json"
    private const val DEFAULT_FAQ_URL ="https://static.willow.tv/apps/info/AndroidTV.json"
    //-------------------------------------------------------------------------

    private const val US_IAPCurrency: String = "USD"
    private const val CA_IAPCurrency: String = "CAD"
    private var iapCurrency = ""
    private var inAppProductDetails: List<InappProductDetails>? = null
    var currentPage = "No Data"


    internal const val DB_NAME: String = "willow_app.db"
    internal const val TV_CUSTOMER = "tv_customer"
    internal const val DIGITAL_CUSTOMER_FREE = "digital_customer_free"
    internal const val DIGITAL_CUSTOMER_SUBSCRIBED = "digital_customer_subscribed"
    internal var tvConfig: APITVConfigDataModel? = null
    internal var adsConfig: APIDFPConfigDataModel? = null
    internal var country: String = "NA"



    //Added dummy url for handling the http/https url error in case of blank or null

    fun getBaseUrl(): String {
        return DEFAULT_STATIC_BASE_URL
    }
    fun getStaticBaseUrl(): String {
        return tvConfig?.apiConfig?.staticUrl?.getSelectedBuildVariantUrl()?:DEFAULT_STATIC_BASE_URL
    }


    fun getLoginDynamicBaseUrl(): String {
        return tvConfig?.apiConfig?.dynamicUrl?.getSelectedBuildVariantUrl()?:DEFAULT_DYNAMIC_BASE_URL
    }

    fun getTVProviderLogosURL(): String {
        return tvConfig?.tveProviders?:DEFAULT_TV_PROVIDER_LOGO_URL
    }

    fun getCountryCheckEndPoint(): String {
        return tvConfig?.countryCheckEndPoint?: DEFAULT_COUNTRY_CHECK_END_POINT
    }
    fun getSubscriptionBaseUrl(): String {
        return tvConfig?.subscriptionBaseUrl?:DEFAULT_SUBSCRIPTION_BASEURL
    }
    fun getHelpAndFAQUrl(): String {
        return tvConfig?.helpAndFAQUrl?:DEFAULT_FAQ_URL
    }

    fun getDFPConfigUrl(): String {
        return tvConfig?.userAdsConfig?:DEFAULT_ADS_CONFIG
    }

    fun isPollerEnabled(): Boolean? {
        return tvConfig?.pollerEnabled
    }
    fun isContinueWatchingEnabled(): Boolean? {
        return tvConfig?.continue_watching
    }

    fun getPollerInterval(): Int?{
        return tvConfig?.pollerInterval
    }

    fun getLiveRefreshInterval(): Int?{
        return tvConfig?.liveDataRefreshInterval
    }
    fun getPollerDesc(): String? {
        return tvConfig?.messages?.pollerDescription
    }
    fun getPollerTitle(): String? {
        return tvConfig?.messages?.pollerTitle
    }
    fun getTVELoginInstruction(): String? {
        return tvConfig?.tveMsg+"\n"+ tvConfig?.messages?.tveLoginInstruction
    }

    fun getBaseUrl(baseType: String?): String? {
        return if (!TextUtils.equals(baseType, GlobalConstants.ApiConstant.staticUrl))
            tvConfig?.apiConfig?.dynamicUrl?.getSelectedBuildVariantUrl()
        else
            tvConfig?.apiConfig?.staticUrl?.getSelectedBuildVariantUrl()
    }

    //remove this once it come via payload
    fun getImageBaseUrl(): String {
        return when (tvConfig?.appEnvironment) {
            "staging" -> {
                tvConfig?.apiConfig?.imageUrl?.staging.toString()
            }
            "production" -> {
                tvConfig?.apiConfig?.imageUrl?.production.toString()
            }
            else -> {
                tvConfig?.apiConfig?.imageUrl?.development.toString()
            }
        }
    }
    fun getPollerUrl(): String {

         /**
          * UnComment when config is updated with correct poller URL
         * */
        return when (tvConfig?.appEnvironment) {
            "staging" -> {
                tvConfig?.apiConfig?.pollerUrl?.staging.toString()
            }
            "production" -> {
                tvConfig?.apiConfig?.pollerUrl?.production.toString()
            }
            else -> {
                tvConfig?.apiConfig?.pollerUrl?.development.toString()
            }
        }
       // return URL_POLLER
    }

    fun getScreenPageURL(screenType : Types.ScreenType): String?{
        tvConfig?.navigation?.navigationTabs?.forEach {
            if (it.getScreenType() == screenType) {
                return it.navUrl
            }
        }
        return null
    }


    fun getStreamingTargetURL(baseUrlType:String?, targetURL: String?): String{
        Timber.d("getStreamingTargetURL>>>>> baseUrlType ${baseUrlType.toString()}  > targetURL ${targetURL.toString()}")
       if(baseUrlType == "dynamicUrl"){
           return when (tvConfig?.appEnvironment) {
               "staging" -> {
                   tvConfig?.apiConfig?.dynamicUrl?.staging.toString()+(targetURL?:"")
               }
               "production" -> {
                   tvConfig?.apiConfig?.dynamicUrl?.production.toString()+(targetURL?:"")
               }
               else -> {
                   tvConfig?.apiConfig?.dynamicUrl?.development.toString()+(targetURL?:"")
               }
           }
       }else{
           return when (tvConfig?.appEnvironment) {
               "staging" -> {
                   tvConfig?.apiConfig?.staticUrl?.staging.toString()+(targetURL?:"")
               }
               "production" -> {
                   tvConfig?.apiConfig?.staticUrl?.production.toString() +(targetURL?:"")
               }
               else -> {
                   tvConfig?.apiConfig?.staticUrl?.development.toString() +(targetURL?:"")
               }
           }
       }
    }

    fun getSubscriptionBannerImg() = tvConfig?.subscriptionBanner?: DEFAULT_SUBSCRIPTION_BASEURL

    fun setCountryCode(dataModel: String?) {
        dataModel?.let { model ->
            country = model.lowercase(Locale.getDefault())
            setConfigData()
            Timber.d("country >> setCountryCode >> $country")
        }
    }


    //set config either US or CA
    private fun setConfigData() {
        when (country.uppercase()) {
            "US" -> {
                iapCurrency = US_IAPCurrency
                inAppProductDetails = tvConfig?.inAPProductId?.US
            }
            else -> {
                CA_IAPCurrency
                inAppProductDetails = tvConfig?.inAPProductId?.CA
            }
        }


    }
    fun getInAppProductDetails(): List<InappProductDetails> {
        setConfigData()
        return inAppProductDetails?: listOf()
    }

    /*fun getInAppProductDetails(): List<InappProductDetails> {
        return TVUserConfig.inAppProductDetails ?: TVUserConfig.getInAppProductDetails()?: listOf()
    }*/



}