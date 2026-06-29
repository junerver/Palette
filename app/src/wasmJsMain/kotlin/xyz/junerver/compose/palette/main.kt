import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import xyz.junerver.compose.palette.components.badge.PBadge
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme

/**
 * Minimal WASM entry point: verifies palette components render in the browser.
 *
 * Intentionally lean — it exercises the theme + a couple of components to confirm the wasmJs runtime
 * path works end-to-end, independent of the full demo app. Uses ComposeViewport (the Compose 1.9+
 * web entry point; CanvasBasedWindow is deprecated/removed in 1.11).
 *
 * @see <a href="https://github.com/JetBrains/compose-multiplatform/blob/master/examples/nav_cupcake/composeApp/src/wasmJsMain/kotlin/main.kt">Official entry-point pattern</a>
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport("ComposeTarget") {
        PaletteMaterialTheme {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                PBadge(content = "Palette WASM ✓")
                PButton(text = "Click me", onClick = {})
            }
        }
    }
}
