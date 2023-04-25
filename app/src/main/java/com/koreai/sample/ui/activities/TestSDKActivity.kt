package com.koreai.sample.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.koreai.sample.R

import ai.kore.androidsdk.KoreAiSDK

class TestSDKActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_sdk_activity)

        val btTestSDK = findViewById<AppCompatButton>(R.id.btTestSDK)
        btTestSDK?.setOnClickListener {
            KoreAiSDK.showChatActivity(this, "martin.bonardi+dev.2@kore.com", "Development")
        }
    }
}