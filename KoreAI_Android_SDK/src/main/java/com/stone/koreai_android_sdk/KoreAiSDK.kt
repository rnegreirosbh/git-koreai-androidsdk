package com.stone.koreai_android_sdk

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher

object KoreAiSDK {
    private var _isSDKInitialized = false
    private var _chatbotSecurityId: String? = null
    private var _userId: String? = null

    val chatbotSecurityId get() = _chatbotSecurityId
    var userId :String?
        get() = _userId
        set(value) { _userId = value }

    fun init(chatbotSecurityId: String) {
        _chatbotSecurityId = chatbotSecurityId
        _isSDKInitialized = true
    }

    fun showChatActivity(context: Context, userId: String, title: String) {
        if (_isSDKInitialized) {
            _userId = userId
            val intent = Intent(context, KoreAiChatActivity::class.java)
            intent.putExtra("title", title)
            context.startActivity(intent)
        } else {
            Log.e("Error", "SDK not initialized")
        }
    }

    fun showWebviewChatActivity(context: Context, launcher: ActivityResultLauncher<Intent>) {
        if (_isSDKInitialized) {
            launcher.launch(Intent(context, KoreAiWebviewChatActivity::class.java))
        } else {
            Log.e("Error", "SDK not initialized")
        }
    }
}