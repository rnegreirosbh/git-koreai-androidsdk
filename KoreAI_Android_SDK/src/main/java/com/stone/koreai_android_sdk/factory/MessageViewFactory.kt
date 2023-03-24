package com.stone.koreai_android_sdk.factory

import android.content.res.Resources.getSystem
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.stone.koreai_android_sdk.R
import kotlinx.coroutines.*
import java.net.URL
import kotlin.math.roundToInt

val Int.px: Int get() = (this * getSystem().displayMetrics.density).toInt()

enum class MessageOwner {
    Bot, User
}

private enum class TimeMessageFormat {
    Below, Complement, Hybrid
}

class MessageViewFactory(context: AppCompatActivity) {

    private val _messageFontSize = 13.0F
    private val _timeFontSize = 12.0F
    private var _context: AppCompatActivity = context
    private var _maxSize: Int = (_context.getResources().getDisplayMetrics().widthPixels * 0.8).roundToInt()

    fun getMessageView(message: String, time: String, owner: MessageOwner): ConstraintLayout {
        val pos = message.indexOf("[Image]")
        if (pos >= 0) {
            val text = message.substring(0, pos)
            val imageUrl = message.substring(pos + 8, message.length - 1)
            return _getImageMessageView(text, imageUrl, time, owner)
        } else {
            return _getTextMessageView(message, time, owner)
        }
    }

    private fun _getTextMessageView(message: String, time: String, owner: MessageOwner): ConstraintLayout {
        val layout = ConstraintLayout(_context)
        layout.id = View.generateViewId()
        layout.elevation = 10.0F
        layout.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (owner == MessageOwner.Bot) {
            layout.setBackground(ContextCompat.getDrawable(_context, R.drawable.layout_message_to))
        } else {
            layout.setBackground(ContextCompat.getDrawable(_context, R.drawable.layout_message_from))
        }

        val tvMessage = AppCompatTextView(_context)
        tvMessage.id = View.generateViewId()
        tvMessage.text = message
        tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, _messageFontSize)
        tvMessage.maxWidth = _maxSize
        tvMessage.setTextColor(ContextCompat.getColor(_context, R.color.KoreAiSdkMessageTextColor))
        tvMessage.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layout.addView(tvMessage)

        val tvTime = AppCompatTextView(_context)
        tvTime.id = View.generateViewId()
        tvTime.text = time
        tvTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, _timeFontSize)
        tvTime.maxWidth = _maxSize
        tvTime.setTextColor(ContextCompat.getColor(_context, R.color.KoreAiSdkTimeTextColor))
        tvTime.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layout.addView(tvTime)

        val tvError = AppCompatTextView(_context)
        tvError.id = View.generateViewId()
        tvError.text = time
        tvError.setTextSize(TypedValue.COMPLEX_UNIT_SP, _timeFontSize)
        tvError.setTextColor(ContextCompat.getColor(_context, R.color.KoreAiSdkErrorTextColor))
        tvError.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        tvError.visibility = View.GONE
        layout.addView(tvError)

        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)

        constraintSet.connect(tvMessage.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 5.px)
        constraintSet.connect(tvMessage.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, 5.px)
        when (_isTimeViewOverMessage(message)) {
            TimeMessageFormat.Below -> {
                constraintSet.connect(tvMessage.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM, 25.px)
                constraintSet.connect(tvTime.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 5.px)
            }
            TimeMessageFormat.Complement -> {
                constraintSet.connect(tvMessage.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM, 15.px)
                constraintSet.connect(tvTime.getId(), ConstraintSet.START, tvMessage.getId(), ConstraintSet.END, 5.px)
                constraintSet.connect(tvTime.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 5.px)
            }
            TimeMessageFormat.Hybrid -> {
                constraintSet.connect(tvMessage.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM, 15.px)
                constraintSet.connect(tvTime.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 5.px)
            }
        }
        constraintSet.connect(tvTime.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM, 5.px)

        constraintSet.applyTo(layout)

        return layout
    }

    private fun _getImageMessageView(message: String, imageUrl: String, time: String, owner: MessageOwner): ConstraintLayout {
        val layout = ConstraintLayout(_context)
        layout.id = View.generateViewId()
        layout.elevation = 10.0F
        layout.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (owner == MessageOwner.Bot) {
            layout.setBackground(ContextCompat.getDrawable(_context, R.drawable.layout_message_to))
        } else {
            layout.setBackground(ContextCompat.getDrawable(_context, R.drawable.layout_message_from))
        }

        val tvMessage = AppCompatTextView(_context)
        tvMessage.id = View.generateViewId()
        tvMessage.text = message
        tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, _messageFontSize)
        tvMessage.maxWidth = _maxSize
        tvMessage.setTextColor(ContextCompat.getColor(_context, R.color.KoreAiSdkMessageTextColor))
        tvMessage.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layout.addView(tvMessage)

        var bitmap: Bitmap?
        val bitmapDeferred = _downloadImage(imageUrl)
        runBlocking {
            bitmap = bitmapDeferred.await() as Bitmap?
        }
        val ivImage = AppCompatImageView(_context)
        ivImage.id = View.generateViewId()
        if (bitmap!!.width > _maxSize) {
            ivImage.layoutParams = ConstraintLayout.LayoutParams(
                _maxSize,
                (_maxSize / bitmap!!.width) * bitmap!!.height
            )
        } else {
            ivImage.layoutParams = ConstraintLayout.LayoutParams(
                bitmap!!.width,
                bitmap!!.height
            )
        }
        ivImage.setImageBitmap(bitmap!!)
        ivImage.setBackground(ContextCompat.getDrawable(_context, R.drawable.image_background))
        layout.addView(ivImage)

        val tvTime = AppCompatTextView(_context)
        tvTime.id = View.generateViewId()
        tvTime.text = time
        tvTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, _timeFontSize)
        tvTime.maxWidth = _maxSize
        tvTime.setTextColor(ContextCompat.getColor(_context, R.color.KoreAiSdkTimeTextColor))
        tvTime.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layout.addView(tvTime)

        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)

        constraintSet.connect(tvMessage.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 5.px)
        constraintSet.connect(tvMessage.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, 5.px)
        constraintSet.connect(ivImage.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 5.px)
        if (bitmap!!.width > _maxSize) {
            constraintSet.connect(ivImage.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 5.px)
        }
        constraintSet.connect(ivImage.getId(), ConstraintSet.TOP, tvMessage.getId(), ConstraintSet.BOTTOM, 5.px)
        constraintSet.connect(ivImage.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM, 5.px)
        constraintSet.connect(tvTime.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 5.px)
        constraintSet.connect(tvTime.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM, 5.px)

        constraintSet.applyTo(layout)

        return layout
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun _downloadImage(url: String) = GlobalScope.async {
        withContext(Dispatchers.IO) {
            try {
                val uri = URL(url)
                BitmapFactory.decodeStream(uri.openConnection().getInputStream())
            }
            catch(_ :Exception) {}
        }
    }

    private fun _isTimeViewOverMessage(message: String): TimeMessageFormat {
        val tv = TextView(_context)
        tv.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, _messageFontSize)
        tv.text = message
        tv.measure(
            View.MeasureSpec.makeMeasureSpec(_maxSize, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))

        if (tv.layout != null) {
            val lastLineWidth = tv.layout.getLineRight(tv.lineCount - 1) -
                                tv.layout.getLineLeft(tv.lineCount - 1)
            val overMessage = (_maxSize - lastLineWidth) < 40.px
            if (!overMessage && tv.lineCount > 1) {
                return TimeMessageFormat.Hybrid
            } else {
                if (!overMessage && tv.lineCount == 1) {
                    return TimeMessageFormat.Complement
                }
            }
        }
        return TimeMessageFormat.Below
    }
}