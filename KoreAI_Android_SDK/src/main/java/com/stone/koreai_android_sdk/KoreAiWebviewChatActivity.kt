package com.stone.koreai_android_sdk

import ai.kore.androidsdk.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import im.delight.android.webview.AdvancedWebView

@SuppressLint("SimpleDateFormat")
internal class KoreAiWebviewChatActivity : AppCompatActivity(), AdvancedWebView.Listener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kore_ai_webview_chat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        window.setStatusBarColor(ContextCompat.getColor(this, R.color.KoreAiSdkStatusBarColor))

        supportActionBar?.hide()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val webview = findViewById<AdvancedWebView>(R.id.wvChat)
        webview.setListener(this, this)
        webview.loadUrl("https://bots.kore.ai/webclient/" + KoreAiSDK.chatbotSecurityId)
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("Action", "OpenSpecificActivity")
        setResult(Activity.RESULT_OK, resultIntent)
        super.onBackPressed()
    }

    override fun onPageStarted(url: String?, favicon: Bitmap?) {
        if (url.toString().contains("wikipedia")) {
            val webview = findViewById<AdvancedWebView>(R.id.wvChat)
            webview.onPause()
        }
        Log.d("Chat", "onPageStarted-" + url)
    }

    override fun onPageFinished(url: String?) {
        Log.d("Chat", "onPageFinished-" + url)
    }

    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
        Log.d("Chat", "onPageError")
    }

    override fun onDownloadRequested(
        url: String?,
        suggestedFilename: String?,
        mimeType: String?,
        contentLength: Long,
        contentDisposition: String?,
        userAgent: String?
    ) {
        Log.d("Chat", "onDownloadRequested")
    }

    override fun onExternalPageRequest(url: String?) {
        Log.d("Chat", "onExternalPageRequest-" + url)
    }
}