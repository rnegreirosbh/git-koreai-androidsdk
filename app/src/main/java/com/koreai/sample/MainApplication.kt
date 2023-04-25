package com.koreai.sample

import android.app.Application
import com.stone.koreai_android_sdk.KoreAiSDK

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        KoreAiSDK.init(
            "st-a0285dd5-5819-5427-acde-4755c7ada06e",
            "cs-7b5563e1-4548-5515-adce-476975efb41e",
            "UfUT3WYOIYi5Y0uxl+ewFlg5cDSH/fdSdXfRL/y7rCA="
        )
    }
}