package com.koreai.sample

import android.app.Application
import com.stone.koreai_android_sdk.KoreAiSDK

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        KoreAiSDK.init(
            "3a34556e90c648e8aad5377f0b956741249133d5d33841d7900851412fa968ecsta0"
        )
    }
}