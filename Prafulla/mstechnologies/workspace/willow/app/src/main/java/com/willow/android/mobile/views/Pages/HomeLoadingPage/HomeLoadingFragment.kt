package com.willow.android.mobile.views.pages.homeLoadingPage

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.willow.android.R
import com.willow.android.mobile.MainActivity
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.models.PreferencesModel
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.services.StorageService



class HomeLoadingFragment : Fragment() {

    companion object {
        fun newInstance() = HomeLoadingFragment()
    }

    private lateinit var viewModel: HomeLoadingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.home_loading_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeLoadingViewModel::class.java)

        context?.let {
            StorageService.init(it)
            UserModel.initStoredProperties()
            PreferencesModel.initShowValues()
            getConfigData()
            getDfpConfigData()
        }
    }

    fun getConfigData() {
        viewModel.getConfigData(requireContext())
        viewModel.gotConfigData.observe(viewLifecycleOwner, Observer {
            getCountryCode()
        })
    }

    fun getCountryCode() {
        viewModel.getCountryCode(requireContext())
        viewModel.countryCode.observe(viewLifecycleOwner, Observer {
            UserModel.setCountryCode(it)

            checkGeoBlocking()
        })
    }

    fun getDfpConfigData() {
        viewModel.getDfpConfigData(requireContext())
    }

    fun checkGeoBlocking() {
        val allowedCountries = "us, ca"

        if (allowedCountries.contains(UserModel.cc.lowercase(), ignoreCase = true)) {
            checkUserSubscription()
        } else {
            showMessage(MessageConfig.geoblock)
//            showTestWebView()
        }
    }

    fun checkUserSubscription() {
        if (shouldCheckSubscription()) {
            viewModel.getCheckSubscriptionUserData(requireContext())
            viewModel.checkSubscriptionUserData.observe(viewLifecycleOwner, Observer {
                UserModel.setCheckSubscriptionRespData(it)
                launchHomePage()
            })
        } else {
            launchHomePage()
        }
    }

    fun shouldCheckSubscription(): Boolean {
        if ((UserModel.isLoggedIn()) && (!UserModel.isTVEUser)) {
            return true
        }

        return false
    }

    fun launchHomePage() {
        val intent = Intent(context, MainActivity::class.java)
        context?.startActivity(intent)
    }

    fun showMessage(message: String) {
        val messageText = activity?.findViewById<TextView>(R.id.message_text)
        messageText?.visibility = View.VISIBLE
        messageText?.text = message
    }

    // Test Webview
    fun showTestWebView() {
        val appledialog = Dialog(requireContext())
        val webView = WebView(requireContext())
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = SampleAppleWebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("www.willow.tv")
        appledialog.setContentView(webView)
        appledialog.show()
    }

    @Suppress("OverridingDeprecatedMember")
    inner class SampleAppleWebViewClient : WebViewClient() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val requestUrl = request?.url
            if (request?.url.toString().contains("www.willow.tv/tvchannel")) {

                handleUrl(request?.url.toString())
                
                return true
            }
            return true
        }

        // For API 19 and below
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url == "https://www.willow.tv/tvchannel") {

                handleUrl(url)

                return true
            }
            return false
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            println("https://www.willow.tv/")
        }

        // Check WebView url for access token code or error
        @SuppressLint("LongLogTag")
        private fun handleUrl(url: String) {


        }
    }

}