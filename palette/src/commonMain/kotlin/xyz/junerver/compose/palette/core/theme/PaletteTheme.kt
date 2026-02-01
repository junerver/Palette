package xyz.junerver.compose.palette.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import xyz.junerver.compose.palette.core.tokens.PaletteColors
import xyz.junerver.compose.palette.core.tokens.PaletteShapes
import xyz.junerver.compose.palette.core.tokens.PaletteSpacing
import xyz.junerver.compose.palette.core.tokens.PaletteTypography

val LocalPaletteColors = staticCompositionLocalOf { PaletteColors() }
val LocalPaletteSpacing = staticCompositionLocalOf { PaletteSpacing() }
val LocalPaletteShapes = staticCompositionLocalOf { PaletteShapes() }
val LocalPaletteTypography = staticCompositionLocalOf { PaletteTypography() }
val LocalPaletteDarkTheme = staticCompositionLocalOf { false }

@Composable
fun PaletteTheme(
    colors: PaletteColors = PaletteColors(),
    spacing: PaletteSpacing = PaletteSpacing(),
    shapes: PaletteShapes = PaletteShapes(),
    typography: PaletteTypography = PaletteTypography(),
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalPaletteColors provides colors,
        LocalPaletteSpacing provides spacing,
        LocalPaletteShapes provides shapes,
        LocalPaletteTypography provides typography,
        LocalPaletteDarkTheme provides darkTheme,
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

    val typography: PaletteTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalPaletteTypography.current

    val isDark: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalPaletteDarkTheme.current
}
