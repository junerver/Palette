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
     * Returns [PaletteTheme.colors.textPrimary] for primary text.
     */
    @Composable
    fun color(): Color = PaletteTheme.colors.textPrimary

    /**
     * Secondary text color from the theme text hierarchy.
     * Suitable for less prominent text like descriptions or captions.
     */
    @Composable
    fun secondaryColor(): Color = PaletteTheme.colors.textSecondary

    /**
     * Disabled text color from the theme text hierarchy.
     * Suitable for disabled or inactive text.
     */
    @Composable
    fun disabledColor(): Color = PaletteTheme.colors.textDisabled

    /**
     * Default text style from the theme.
     * Returns [PaletteTheme.typography.body] as the default style.
     */
    @Composable
    fun style(): TextStyle = PaletteTheme.typography.body
}
