package com.stone.koreai_android_sdk.messageViews

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
import com.stone.koreai_android_sdk.R
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
        tvMessage.setTextColor(ContextCompat.getColor(_context, R.color.KoreAiSdkMessageTextColor))
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

    override fun updateToErrorState() { }

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