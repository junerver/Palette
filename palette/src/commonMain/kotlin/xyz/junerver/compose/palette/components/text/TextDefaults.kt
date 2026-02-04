package xyz.junerver.compose.palette.components.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * Contains the default values used by [PText].
 */
object TextDefaults {
    /**
     * Default text color that adapts to the current theme.
     * Returns [PaletteTheme.colors.onSurface] for primary text.
     */
    @Composable
    fun color(): Color = PaletteTheme.colors.onSurface

    /**
     * Secondary text color with reduced opacity.
     * Suitable for less prominent text like descriptions or captions.
     */
    @Composable
    fun secondaryColor(): Color = PaletteTheme.colors.onSurface.copy(alpha = 0.6f)

    /**
     * Disabled text color with further reduced opacity.
     * Suitable for disabled or inactive text.
     */
    @Composable
    fun disabledColor(): Color = PaletteTheme.colors.hint

    /**
     * Default text style from the theme.
     * Returns [PaletteTheme.typography.body] as the default style.
     */
    @Composable
    fun style(): TextStyle = PaletteTheme.typography.body
}
