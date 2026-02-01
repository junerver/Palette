package xyz.junerver.compose.palette.components.screen

import android.app.Activity

actual interface PlatformActivity {
    actual fun finish()
}

class AndroidActivity(private val activity: Activity) : PlatformActivity {
    override fun finish() {
        activity.finish()
    }
}
