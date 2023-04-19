package ai.kore.androidsdk

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher

object KoreAiSDK {
    private var _isSDKInitialized = false
    private var _chatbotSecurityId: String? = null

    val chatbotSecurityId get() = _chatbotSecurityId

    fun init(chatbotSecurityId: String) {
        _chatbotSecurityId = chatbotSecurityId
        _isSDKInitialized = true
    }

    fun showChatActivity(context: Context, launcher: ActivityResultLauncher<Intent>? = null) {
        if (_isSDKInitialized) {
            if (launcher != null) {
                launcher.launch(Intent(context, KoreAiChatActivity::class.java))
            } else {
                context.startActivity(Intent(context, KoreAiChatActivity::class.java))
            }
        } else {
            Log.e("Error", "SDK not initialized")
        }
    }
}