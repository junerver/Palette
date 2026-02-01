package xyz.junerver.compose.palette.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import xyz.junerver.compose.palette.core.tokens.PaletteColors
import xyz.junerver.compose.palette.core.tokens.PaletteShapes
import xyz.junerver.compose.palette.core.tokens.PaletteSpacing

val LocalPaletteColors = staticCompositionLocalOf { PaletteColors() }
val LocalPaletteSpacing = staticCompositionLocalOf { PaletteSpacing() }
val LocalPaletteShapes = staticCompositionLocalOf { PaletteShapes() }

@Composable
fun PaletteTheme(
    colors: PaletteColors = PaletteColors(),
    spacing: PaletteSpacing = PaletteSpacing(),
    shapes: PaletteShapes = PaletteShapes(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalPaletteColors provides colors,
        LocalPaletteSpacing provides spacing,
        LocalPaletteShapes provides shapes,
        content = content
    )
}

object PaletteTheme {
    val colors: PaletteColors
        @Composable
        @ReadOnlyComposable
        get() = LocalPaletteColors.current

    val spacing: PaletteSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalPaletteSpacing.current

    val shapes: PaletteShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalPaletteShapes.current
}
