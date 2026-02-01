package xyz.junerver.compose.palette.core.tokens

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class PaletteSemanticColors(
    val primary: Color,
    val onPrimary: Color,
    val border: Color,
    val surface: Color,
    val onSurface: Color,
    val hint: Color,
    val error: Color,
    val success: Color,
    val warning: Color,
) {
    fun toMaterialScheme(): ColorScheme = ColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primary,
        onPrimaryContainer = onPrimary,
        inversePrimary = primary,
        secondary = primary,
        onSecondary = onPrimary,
        secondaryContainer = primary,
        onSecondaryContainer = onPrimary,
        tertiary = success,
        onTertiary = onPrimary,
        tertiaryContainer = success,
        onTertiaryContainer = onPrimary,
        background = surface,
        onBackground = onSurface,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surface,
        onSurfaceVariant = onSurface,
        surfaceTint = primary,
        inverseSurface = onSurface,
        inverseOnSurface = surface,
        error = error,
        onError = onPrimary,
        errorContainer = error,
        onErrorContainer = onPrimary,
        outline = border,
        outlineVariant = border,
        scrim = Color.Black,
        surfaceBright = surface,
        surfaceDim = surface,
        surfaceContainerLowest = surface,
        surfaceContainerLow = surface,
        surfaceContainer = surface,
        surfaceContainerHigh = surface,
        surfaceContainerHighest = surface,
    )
}

fun PaletteColors.toSemanticColors(): PaletteSemanticColors = PaletteSemanticColors(
    primary = primary,
    onPrimary = onPrimary,
    border = border,
    surface = surface,
    onSurface = onSurface,
    hint = hint,
    error = error,
    success = success,
    warning = warning,
)
