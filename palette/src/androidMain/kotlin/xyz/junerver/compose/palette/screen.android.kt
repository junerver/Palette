package xyz.junerver.compose.palette

import android.app.Activity
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

/**
 * Actual implementation for Android
 */
actual interface PlatformActivity {
    fun finish()
}

/**
 * Actual implementation of getScreenWidthPx for Android
 */
@Composable
actual fun getScreenWidthPx(): Float {
    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    return displayMetrics.widthPixels.toFloat()
}

/**
 * Extension function to convert Android Activity to PlatformActivity
 */
fun Activity.asPlatformActivity(): PlatformActivity = object : PlatformActivity {
    override fun finish() = this@asPlatformActivity.finish()
}
