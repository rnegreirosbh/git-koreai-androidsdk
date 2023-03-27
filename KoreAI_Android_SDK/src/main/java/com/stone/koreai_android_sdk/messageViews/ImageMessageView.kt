package com.stone.koreai_android_sdk.messageViews

import ai.kore.androidsdk.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import java.net.URL

internal class ImageMessageView(context: AppCompatActivity): BaseMessageView(context)  {

    override fun getLayout(
        message: String,
        time: String,
        owner: MessageOwner,
        imageUrl: String?,
    ): ConstraintLayout {
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
        if (owner == MessageOwner.Bot) {
            tvMessage.setTextColor(ContextCompat.getColor(_context, R.color.KoreAiSdkBotMessageTextColor))
        } else {
            tvMessage.setTextColor(ContextCompat.getColor(_context, R.color.KoreAiSdkUserMessageTextColor))
        }
        tvMessage.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layout.addView(tvMessage)

        var bitmap: Bitmap?
        val bitmapDeferred = _downloadImage(imageUrl!!)
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

        constraintSet.connect(tvMessage.id, ConstraintSet.START, layout.id, ConstraintSet.START, 5.px)
        constraintSet.connect(tvMessage.id, ConstraintSet.TOP, layout.id, ConstraintSet.TOP, 5.px)
        constraintSet.connect(ivImage.id, ConstraintSet.START, layout.id, ConstraintSet.START, 5.px)
        if (bitmap!!.width > _maxSize) {
            constraintSet.connect(ivImage.id, ConstraintSet.END, layout.id, ConstraintSet.END, 5.px)
        }
        constraintSet.connect(ivImage.id, ConstraintSet.TOP, tvMessage.id, ConstraintSet.BOTTOM, 5.px)
        constraintSet.connect(ivImage.id, ConstraintSet.BOTTOM, layout.id, ConstraintSet.BOTTOM, 5.px)
        constraintSet.connect(tvTime.id, ConstraintSet.END, layout.id, ConstraintSet.END, 5.px)
        constraintSet.connect(tvTime.id, ConstraintSet.BOTTOM, layout.id, ConstraintSet.BOTTOM, 5.px)

        constraintSet.applyTo(layout)

        return layout
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun _downloadImage(url: String) = GlobalScope.async {
        withContext(Dispatchers.IO) {
            try {
                val uri = URL(url)
                BitmapFactory.decodeStream(uri.openConnection().getInputStream())
            }
            catch(_ :Exception) {}
        }
    }
}