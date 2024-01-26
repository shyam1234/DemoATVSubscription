package com.willow.android.mobile.views.pages

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.willow.android.R

class WebViewPageActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_view_page_activity)

        val url: String? = intent.getStringExtra("url")

        if (url != null) {
            webView = findViewById(R.id.webView)
            webView.webViewClient = WebViewClient()
            webView.loadUrl(url)
            webView.settings.javaScriptEnabled = true
        }

        val pageTitle = intent.getStringExtra("PAGE_TITLE")
        pageTitle?.let {
            setPageTitle(it)
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack())
            webView.goBack()
        else
            super.onBackPressed()
    }

    private fun setPageTitle(title: String) {
        val header = findViewById<View>(R.id.webview_page_header)
        val headerTitle = header.findViewById<TextView>(R.id.page_header_title)
        headerTitle.text = title
    }
}