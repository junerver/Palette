package xyz.junerver.compose.palette.components.screen

actual interface PlatformActivity {
    actual fun finish()
}

class DesktopActivity(private val onClose: () -> Unit = {}) : PlatformActivity {
    override fun finish() {
        onClose()
    }
}
