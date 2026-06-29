package xyz.junerver.compose.palette

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

/**
 * WASM entry point for the :app web target.
 *
 * Renders the **same [App] the android and desktop builds use** — the full component demo with the
 * sidebar, theme switcher, and every component showcase. This makes :app a true three-platform
 * sample (android + desktop + web) sharing one codebase; the wasmJs build additionally powers the
 * docs-site playground (embedded via iframe in docs-site/docs/playground.md).
 *
 * Uses ComposeViewport (the Compose 1.9+ web entry point; CanvasBasedWindow is deprecated/removed
 * in 1.11).
 *
 * @see <a href="https://github.com/JetBrains/compose-multiplatform/blob/master/examples/nav_cupcake/composeApp/src/wasmJsMain/kotlin/main.kt">Official entry-point pattern</a>
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport("ComposeTarget") {
        App()
    }
}
