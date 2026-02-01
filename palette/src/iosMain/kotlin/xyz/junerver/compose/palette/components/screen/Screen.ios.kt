package xyz.junerver.compose.palette.components.screen

actual interface PlatformActivity {
    actual fun finish()
}

class IOSPlatformActivity : PlatformActivity {
    override fun finish() {
        // iOS doesn't have a direct "finish" concept like Android
    }
}
