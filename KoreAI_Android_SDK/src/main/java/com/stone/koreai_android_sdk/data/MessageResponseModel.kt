package com.stone.koreai_android_sdk.data

import org.json.JSONObject

data class MessageResponseModel (
    val data: List<MessageResponseDataModel>,
    val v: String,
    val endOfTask: Boolean,
    val endReason: String,
    val completedTaskID: String,
    val completedTaskName: String
) {
    companion object {
        fun fromJson(json: String): JSONObject {
            return JSONObject(json)
        }
    }
}

data class MessageResponseDataModel (
    val type: String,
    val datumVal: String,
    val createdOn: String,
    val messageID: String
)