package com.koreai.sample.ui.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.koreai.sample.R

import com.stone.koreai_android_sdk.KoreAiSDK

class TestSDKActivity : AppCompatActivity() {
    /*
    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
            }
    }
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_sdk_activity)

        val btTestSDK = findViewById<AppCompatButton>(R.id.btTestSDK)
        btTestSDK?.setOnClickListener {
            //KoreAiSDK.showChatActivity(this, startForResult)
            KoreAiSDK.showChatActivity(this)
        }
    }
}