package com.stone.koreai_android_sdk

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import com.stone.koreai_android_sdk.messageViews.*
import com.stone.koreai_android_sdk.messageViews.BaseMessageView
import com.stone.koreai_android_sdk.messageViews.ImageMessageView
import com.stone.koreai_android_sdk.messageViews.MessageOwner
import com.stone.koreai_android_sdk.messageViews.TextMessageView
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

        ivSendMessageButton.setOnClickListener {
            val message = etMessage?.text.toString()
            if (message.isNotEmpty()) {
                val view = _addUserMessage(message)
                etMessage?.text?.clear()
                _sendMessage(message, view)
            }
        }

        _sendMessage("Vamos conversar!")
    }

    private fun _sendMessage(message: String, view: ConstraintLayout? = null) {
        KoreAiService.sendMessage(message).observe(this) {
            if (it == null) {
                if (view != null) {
                    _errorMark(view)
                }
            } else {
                val list = _parseJson(it.toString())
                for (item in list) {
                    _addMessageView(item.message, item.time, MessageOwner.Bot)
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

    private fun _addUserMessage(message: String): ConstraintLayout {
        // Hide keyboard
        if (currentFocus != null && currentFocus!!.windowToken != null) {
            val inputManager:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.SHOW_FORCED)
        }

        return _addMessageView(message, _getTime(Date()), MessageOwner.User)
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

    private fun _addMessageView(message: String, time: String, owner: MessageOwner): ConstraintLayout {
        val ccMainLayout = findViewById<ConstraintLayout>(R.id.ccMainLayout)

        val layout: ConstraintLayout
        val pos = message.indexOf("[Image]")
        if (pos >= 0) {
            val text = message.substring(0, pos)
            val imageUrl = message.substring(pos + 8, message.length - 1)
            layout = ImageMessageView(this).getLayout(text, time, owner, imageUrl)
        } else {
            layout = TextMessageView(this).getLayout(message, time, owner)
        }

        ccMainLayout.addView(layout)

        val constraintSet = ConstraintSet()
        constraintSet.clone(ccMainLayout)
        if (owner == MessageOwner.Bot) {
            constraintSet.connect(layout.getId(), ConstraintSet.START, ccMainLayout.getId(), ConstraintSet.START, 10.px)
        } else {
            constraintSet.connect(layout.getId(), ConstraintSet.END, ccMainLayout.getId(), ConstraintSet.END, 10.px)
        }
        if (_lastView == null) {
            constraintSet.connect(layout.getId(), ConstraintSet.TOP, ccMainLayout.getId(), ConstraintSet.TOP, 10.px)
        } else {
            val margin = if ((_lastMessageOwner == MessageOwner.Bot).xor(owner == MessageOwner.Bot)) 10.px else 4.px
            constraintSet.connect(layout.getId(), ConstraintSet.TOP, _lastView!!.getId(), ConstraintSet.BOTTOM, margin)
        }

        constraintSet.applyTo(ccMainLayout)

        val svChat = findViewById<ScrollView>(R.id.svChat)
        svChat.post {
            svChat.fullScroll(View.FOCUS_DOWN)
        }

        _lastView = layout
        _lastMessageOwner = owner
        return layout
    }

    private fun _errorMark(view: ConstraintLayout) {
        val ccMainLayout = findViewById<ConstraintLayout>(R.id.ccMainLayout)

        val ivImage = AppCompatImageView(this)
        ivImage.id = View.generateViewId()
        ivImage.setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.stat_notify_error))
        ivImage.setColorFilter(ContextCompat.getColor(this, R.color.KoreAiSdkErrorTextColor));

        ccMainLayout.addView(ivImage)

        val constraintSet = ConstraintSet()
        constraintSet.clone(ccMainLayout)
        constraintSet.connect(ivImage.getId(), ConstraintSet.END, view.getId(), ConstraintSet.START, 3.px)
        constraintSet.connect(ivImage.getId(), ConstraintSet.TOP, view.getId(), ConstraintSet.TOP, 3.px)

        constraintSet.applyTo(ccMainLayout)
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

/*
    val tvError = AppCompatTextView(_context)
    tvError.id = View.generateViewId()
    tvError.text = _context.getString(R.string.KoreAiSdkErrorMessage)
    tvError.setTextSize(TypedValue.COMPLEX_UNIT_SP, _timeFontSize)
    tvError.setTextColor(ContextCompat.getColor(_context, R.color.KoreAiSdkErrorTextColor))
    tvError.layoutParams = ConstraintLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    tvError.visibility = View.GONE
    layout.addView(tvError)

    constraintSet.connect(tvError.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 5.px)
    constraintSet.connect(tvError.getId(), ConstraintSet.TOP, tvTime.getId(), ConstraintSet.BOTTOM, 5.px)
    constraintSet.connect(tvError.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM, 5.px)
 */