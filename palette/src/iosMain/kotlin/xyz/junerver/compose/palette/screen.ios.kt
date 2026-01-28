package xyz.junerver.compose.palette

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIScreen

/**
 * Actual implementation for iOS
 */
actual interface PlatformActivity {
    actual fun finish()
}

/**
 * Actual implementation of getScreenWidthPx for iOS
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun getScreenWidthPx(): Float {
    val screen = UIScreen.mainScreen
    val scale = screen.scale
    val width = screen.bounds.useContents { size.width }
    return (width * scale).toFloat()
}

/**
 * iOS implementation of PlatformActivity
 */
class IOSPlatformActivity : PlatformActivity {
    override fun finish() {
        // iOS doesn't have a direct "finish" concept like Android
        // This can be implemented based on navigation patterns
    }
}
