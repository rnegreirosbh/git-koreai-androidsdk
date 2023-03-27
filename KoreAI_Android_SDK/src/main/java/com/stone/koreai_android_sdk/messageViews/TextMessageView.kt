package com.stone.koreai_android_sdk.messageViews

import ai.kore.androidsdk.R
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat

internal class TextMessageView(context: AppCompatActivity): BaseMessageView(context)  {

    private var _parentView: ConstraintLayout? = null
    
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

        _parentView = layout
        return layout
    }

   override fun updateToErrorState() {
        //if (_parentView != null) {
        //    var errorView = _parentView.findViewById<View>(R.id.)
        //    _erroView!!.visibility = View.VISIBLE
        //    _erroView!!.invalidate()
        //}
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