package xyz.junerver.compose.palette

import platform.Foundation.NSScreen
import platform.UIKit.UIScreen
import platform.UIKit.UIScreenMode
import kotlinx.cinterop.CValue
import platform.CoreGraphics.CGSize

/**
 * Actual implementation for iOS
 */
actual interface PlatformActivity {
    fun finish()
}

/**
 * Actual implementation of getScreenWidthPx for iOS
 */
@Composable
actual fun getScreenWidthPx(): Float {
    val screen = UIScreen.mainScreen
    val scale = screen.scale
    val size = screen.bounds.size
    return (size.width * scale).toFloat()
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
