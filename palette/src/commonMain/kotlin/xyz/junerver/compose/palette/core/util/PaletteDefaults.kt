package xyz.junerver.compose.palette.core.util

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object PaletteDefaults {
    val colors: PaletteColorsSnapshot
        @Composable
        @ReadOnlyComposable
        get() = PaletteColorsSnapshot(PaletteTheme.colors)

    val spacing: PaletteSpacingSnapshot
        @Composable
        @ReadOnlyComposable
        get() = PaletteSpacingSnapshot(PaletteTheme.spacing)

    val shapes: PaletteShapesSnapshot
        @Composable
        @ReadOnlyComposable
        get() = PaletteShapesSnapshot(PaletteTheme.shapes)

    val typography: PaletteTypographySnapshot
        @Composable
        @ReadOnlyComposable
        get() = PaletteTypographySnapshot(PaletteTheme.typography)

    val isDark: Boolean
        @Composable
        @ReadOnlyComposable
        get() = PaletteTheme.isDark

    val materialColors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = PaletteMaterialTheme.colorScheme

    val materialTypography: Typography
        @Composable
        @ReadOnlyComposable
        get() = PaletteMaterialTheme.typography

    val materialShapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = PaletteMaterialTheme.shapes
}

data class PaletteColorsSnapshot(
    val primary: androidx.compose.ui.graphics.Color,
    val onPrimary: androidx.compose.ui.graphics.Color,
    val border: androidx.compose.ui.graphics.Color,
    val surface: androidx.compose.ui.graphics.Color,
    val onSurface: androidx.compose.ui.graphics.Color,
    val hint: androidx.compose.ui.graphics.Color,
    val error: androidx.compose.ui.graphics.Color,
    val success: androidx.compose.ui.graphics.Color,
    val warning: androidx.compose.ui.graphics.Color,
) {
    constructor(colors: xyz.junerver.compose.palette.core.tokens.PaletteColors) : this(
        primary = colors.primary,
        onPrimary = colors.onPrimary,
        border = colors.border,
        surface = colors.surface,
        onSurface = colors.onSurface,
        hint = colors.hint,
        error = colors.error,
        success = colors.success,
        warning = colors.warning,
    )
}

data class PaletteSpacingSnapshot(
    val none: androidx.compose.ui.unit.Dp,
    val extraSmall: androidx.compose.ui.unit.Dp,
    val small: androidx.compose.ui.unit.Dp,
    val medium: androidx.compose.ui.unit.Dp,
    val large: androidx.compose.ui.unit.Dp,
    val extraLarge: androidx.compose.ui.unit.Dp,
) {
    constructor(spacing: xyz.junerver.compose.palette.core.tokens.PaletteSpacing) : this(
        none = spacing.none,
        extraSmall = spacing.extraSmall,
        small = spacing.small,
        medium = spacing.medium,
        large = spacing.large,
        extraLarge = spacing.extraLarge,
    )
}

data class PaletteShapesSnapshot(
    val small: androidx.compose.ui.graphics.Shape,
    val medium: androidx.compose.ui.graphics.Shape,
    val large: androidx.compose.ui.graphics.Shape,
) {
    constructor(shapes: xyz.junerver.compose.palette.core.tokens.PaletteShapes) : this(
        small = shapes.small,
        medium = shapes.medium,
        large = shapes.large,
    )
}

data class PaletteTypographySnapshot(
    val title: androidx.compose.ui.text.TextStyle,
    val body: androidx.compose.ui.text.TextStyle,
    val label: androidx.compose.ui.text.TextStyle,
) {
    constructor(typography: xyz.junerver.compose.palette.core.tokens.PaletteTypography) : this(
        title = typography.title,
        body = typography.body,
        label = typography.label,
    )
}

