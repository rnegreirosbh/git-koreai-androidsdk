package com.koreai.sample.ui.activities

import ai.kore.androidsdk.KoreAiSDK
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.koreai.sample.R

class TestSDKActivity : AppCompatActivity() {

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null && result.data!!.getStringExtra("action") != null) {
                    val message = "Sucesso-" + result.data!!.getStringExtra("action")
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Sucesso- Sem action retornada", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_sdk_activity)

        val btTestSDK = findViewById<AppCompatButton>(R.id.btTestSDK)
        btTestSDK?.setOnClickListener {
            KoreAiSDK.showChatActivity(this, startForResult)
            //KoreAiSDK.showChatActivity(this)
        }
    }
}