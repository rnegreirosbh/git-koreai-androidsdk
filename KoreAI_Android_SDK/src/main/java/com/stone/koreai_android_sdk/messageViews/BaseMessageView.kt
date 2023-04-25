package ai.kore.androidsdk.messageViews

import android.content.res.Resources
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.roundToInt

val Int.px: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

internal enum class MessageOwner {
    Bot, User
}

internal enum class TimeMessageFormat {
    Below, Complement, Hybrid
}

internal abstract class BaseMessageView(context: AppCompatActivity) {
    protected val _messageFontSize = 13.0F
    protected val _timeFontSize = 12.0F
    protected var _context: AppCompatActivity = context
    protected var _maxSize: Int = (_context.getResources().getDisplayMetrics().widthPixels * 0.8).roundToInt()

    abstract fun getLayout(
        message: String,
        time: String,
        owner: MessageOwner,
        imageUrl: String? = null
    ): ConstraintLayout
}