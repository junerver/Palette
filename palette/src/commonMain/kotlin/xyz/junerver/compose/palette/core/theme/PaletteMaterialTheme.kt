package xyz.junerver.compose.palette.core.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import xyz.junerver.compose.palette.core.i18n.PaletteStrings
import xyz.junerver.compose.palette.core.tokens.PaletteColors
import xyz.junerver.compose.palette.core.tokens.PaletteShapes
import xyz.junerver.compose.palette.core.tokens.PaletteSpacing
import xyz.junerver.compose.palette.core.tokens.PaletteTypography
import xyz.junerver.compose.palette.core.tokens.toSemanticColors

private val LocalMaterialColorScheme = staticCompositionLocalOf<ColorScheme?> { null }
private val LocalMaterialTypography = staticCompositionLocalOf<Typography?> { null }
private val LocalMaterialShapes = staticCompositionLocalOf<Shapes?> { null }

@Composable
fun PaletteMaterialTheme(
    colors: PaletteColors = PaletteColors(),
    spacing: PaletteSpacing = PaletteSpacing(),
    shapes: PaletteShapes = PaletteShapes(),
    typography: PaletteTypography = PaletteTypography(),
    strings: PaletteStrings = PaletteStrings.zhCN(),
    darkTheme: Boolean = false,
    materialColors: ColorScheme = colors.toSemanticColors().toMaterialScheme(),
    materialTypography: Typography = MaterialTheme.typography,
    materialShapes: Shapes = MaterialTheme.shapes,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = materialColors,
        typography = materialTypography,
        shapes = materialShapes,
    ) {
        CompositionLocalProvider(
            LocalPaletteColors provides colors,
            LocalPaletteSpacing provides spacing,
            LocalPaletteShapes provides shapes,
            LocalPaletteTypography provides typography,
            LocalPaletteStrings provides strings,
            LocalPaletteDarkTheme provides darkTheme,
            LocalMaterialColorScheme provides materialColors,
            LocalMaterialTypography provides materialTypography,
            LocalMaterialShapes provides materialShapes,
            LocalContentColor provides materialColors.onSurface,
            content = content
        )
    }
}

object PaletteMaterialTheme {
    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalMaterialColorScheme.current ?: MaterialTheme.colorScheme

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalMaterialTypography.current ?: MaterialTheme.typography

    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = LocalMaterialShapes.current ?: MaterialTheme.shapes
}
