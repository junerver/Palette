package xyz.junerver.compose.palette.components.screen

/**
 * wasmJs actual for [PlatformActivity]. The browser has no "finish activity" concept, so `finish()`
 * is a no-op (matching the iOS/desktop placeholders). Callers needing real behavior can supply a
 * custom [PlatformActivity] via [LocalPlatformActivity].
 */
actual interface PlatformActivity {
    actual fun finish()
}
