package com.stone.koreai_android_sdk

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import com.stone.koreai_android_sdk.messageViews.ImageMessageView
import com.stone.koreai_android_sdk.messageViews.MessageOwner
import com.stone.koreai_android_sdk.messageViews.TextMessageView
import com.stone.koreai_android_sdk.messageViews.px
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
internal class KoreAiChatActivity : AppCompatActivity() {

    private var _lastMessageOwner: MessageOwner = MessageOwner.User
    private var _lastView: ConstraintLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kore_ai_chat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        window.setStatusBarColor(ContextCompat.getColor(this, R.color.KoreAiSdkBotActionBarColor))

        var title = intent.getStringExtra("title")
        if (title.isNullOrEmpty()) {
            title = "KoreAiSDK"
        }
        val titleColor = ContextCompat.getColor(this, R.color.KoreAiSdkBotActionBarTextColor)
        val hexColor = "#" + Integer.toHexString(titleColor).substring(2)
        supportActionBar?.title = Html.fromHtml("<font color=\"$hexColor\">$title</font>")
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.KoreAiSdkBotActionBarColor)))

        val etMessage = findViewById<AppCompatEditText>(R.id.etMessage)
        val ivSendMessageButton = findViewById<AppCompatImageView>(R.id.ivSendMessageButton)

        etMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                ivSendMessageButton.performClick()
                true
            } else false
        }

        ivSendMessageButton.setOnClickListener {
            val message = etMessage?.text.toString()
            if (message.isNotEmpty()) {
                _addUserMessage(message)
                etMessage?.text?.clear()

                KoreAiService.sendMessage(message).observe(this) {
                    val list = _parseJson(it.toString())
                    for (item in list) {
                        _addMessageView(item.message, item.time, MessageOwner.Bot)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (_lastView == null) {
            super.onBackPressed()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Chat history will be lost.\nDo you wish to continue?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, _ ->
                    super.onBackPressed()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun _addUserMessage(message: String) {
        // Hide keyboard
        if (currentFocus != null && currentFocus!!.windowToken != null) {
            val inputManager:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.SHOW_FORCED)
        }

        _addMessageView(message, _getTime(Date()), MessageOwner.User)
    }

    private fun _getTime(date: Date): String {
        val currentLocale = ConfigurationCompat.getLocales(resources.configuration)[0]
        val formatter = SimpleDateFormat("HH:mm", currentLocale)
        return formatter.format(date)
    }

    private fun _parseJson(json: String): List<MessageData> {
        val list = ArrayList<MessageData>()
        try {
            val jsonObject = JSONObject(json)
            val array = jsonObject.getJSONArray("data")
            for (i in 0..array.length() - 1) {
                val item = (array[i] as JSONObject)
                list.add(MessageData(item))
            }
        }
        catch (_ : Exception) { }
        return list
    }

    private fun _addMessageView(message: String, time: String, owner: MessageOwner) {
        val ccMainLayout = findViewById<ConstraintLayout>(R.id.ccMainLayout)

        val view: ConstraintLayout
        val pos = message.indexOf("[Image]")
        if (pos >= 0) {
            val text = message.substring(0, pos)
            val imageUrl = message.substring(pos + 8, message.length - 1)
            view = ImageMessageView(this).getLayout(text, time, owner, imageUrl)
        } else {
            view = TextMessageView(this).getLayout(message, time, owner)
        }

        ccMainLayout.addView(view)

        val constraintSet = ConstraintSet()
        constraintSet.clone(ccMainLayout)
        if (owner == MessageOwner.Bot) {
            constraintSet.connect(view.getId(), ConstraintSet.START, ccMainLayout.getId(), ConstraintSet.START, 10.px)
        } else {
            constraintSet.connect(view.getId(), ConstraintSet.END, ccMainLayout.getId(), ConstraintSet.END, 10.px)
        }
        if (_lastView == null) {
            constraintSet.connect(view.getId(), ConstraintSet.TOP, ccMainLayout.getId(), ConstraintSet.TOP, 10.px)
        } else {
            val margin = if ((_lastMessageOwner == MessageOwner.Bot).xor(owner == MessageOwner.Bot)) 10.px else 4.px
            constraintSet.connect(view.getId(), ConstraintSet.TOP, _lastView!!.getId(), ConstraintSet.BOTTOM, margin)
        }

        constraintSet.applyTo(ccMainLayout)
        _lastView = view
        _lastMessageOwner = owner
    }

    inner class MessageData(data: JSONObject) {
        var message: String = ""
        var time: String = ""

        init {
            message = data.getString("val")
            time = _parseTime(data.getString("createdOn"))
        }

        private fun _parseTime(dateStr: String): String {
            val currentLocale = ConfigurationCompat.getLocales(resources.configuration)[0]
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", currentLocale)
            parser.setTimeZone(TimeZone.getTimeZone("GMT"))
            val date = parser.parse(dateStr)

            val formatter = SimpleDateFormat("HH:mm")
            formatter.setTimeZone(TimeZone.getDefault())
            return formatter.format(date)
        }
    }
}