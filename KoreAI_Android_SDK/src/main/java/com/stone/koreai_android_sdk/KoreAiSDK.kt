package com.stone.koreai_android_sdk

import android.content.Context
import android.content.Intent
import android.util.Log

object KoreAiSDK {
    private var _isSDKInitialized = false
    private var _botId: String? = null
    private var _userId: String? = null
    private var _clientId: String? = null
    private var _clientSecret: String? = null

    val isInitialized get() = _isSDKInitialized
    val botId get() = _botId
    val clientId get() = _clientId
    val clientSecret get() = _clientSecret
    var userId :String?
        get() = _userId
        set(value) { _userId = value }

    fun init(botId: String, clientId: String, clientSecret: String) {
        _botId = botId
        _clientId = clientId
        _clientSecret = clientSecret
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
}