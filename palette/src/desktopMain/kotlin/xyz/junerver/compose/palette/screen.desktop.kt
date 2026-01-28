package xyz.junerver.compose.palette

import androidx.compose.runtime.Composable
import java.awt.Toolkit

actual interface PlatformActivity {
    actual fun finish()
}

@Composable
actual fun getScreenWidthPx(): Float {
    val toolkit = Toolkit.getDefaultToolkit()
    val screenSize = toolkit.screenSize
    return screenSize.width.toFloat()
}

class DesktopActivity(private val onClose: () -> Unit = {}) : PlatformActivity {
    override fun finish() {
        onClose()
    }
}
