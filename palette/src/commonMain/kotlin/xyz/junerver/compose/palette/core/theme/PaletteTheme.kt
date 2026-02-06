package xyz.junerver.compose.palette.core.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import xyz.junerver.compose.palette.core.i18n.PaletteStrings
import xyz.junerver.compose.palette.core.tokens.PaletteColors
import xyz.junerver.compose.palette.core.tokens.PaletteShapes
import xyz.junerver.compose.palette.core.tokens.PaletteSpacing
import xyz.junerver.compose.palette.core.tokens.PaletteTypography

val LocalPaletteColors = staticCompositionLocalOf { PaletteColors() }
val LocalPaletteSpacing = staticCompositionLocalOf { PaletteSpacing() }
val LocalPaletteShapes = staticCompositionLocalOf { PaletteShapes() }
val LocalPaletteTypography = staticCompositionLocalOf { PaletteTypography() }
val LocalPaletteDarkTheme = staticCompositionLocalOf { false }
val LocalPaletteStrings = staticCompositionLocalOf { PaletteStrings.zhCN() }

@Composable
fun PaletteTheme(
    colors: PaletteColors = PaletteColors(),
    spacing: PaletteSpacing = PaletteSpacing(),
    shapes: PaletteShapes = PaletteShapes(),
    typography: PaletteTypography = PaletteTypography(),
    strings: PaletteStrings = PaletteStrings.zhCN(),
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalPaletteColors provides colors,
        LocalPaletteSpacing provides spacing,
        LocalPaletteShapes provides shapes,
        LocalPaletteTypography provides typography,
        LocalPaletteStrings provides strings,
        LocalPaletteDarkTheme provides darkTheme,
        LocalContentColor provides colors.onSurface,
        LocalTextStyle provides typography.body,
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

    val strings: PaletteStrings
        @Composable
        @ReadOnlyComposable
        get() = LocalPaletteStrings.current

    val isDark: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalPaletteDarkTheme.current
}
