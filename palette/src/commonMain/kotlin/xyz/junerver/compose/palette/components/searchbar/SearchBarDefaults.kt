package xyz.junerver.compose.palette.components.searchbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.FormTokens
import xyz.junerver.compose.palette.core.tokens.disabledBackground
import xyz.junerver.compose.palette.core.tokens.disabledBorder
import xyz.junerver.compose.palette.core.tokens.focusBorder
import xyz.junerver.compose.palette.core.tokens.hoverBorder

object SearchBarDefaults {
    val Height: Dp = FormTokens.HeightLarge
    val CornerRadius: Dp = FormTokens.CornerRadiusLarge
    val BorderWidth: Dp = FormTokens.BorderWidthDefault
    val IconSize: Dp = ComponentSize.Medium.iconSize
    val ClearButtonSize: Dp = ComponentSize.Small.height
    val ClearIconSize: Dp = ComponentSize.Small.iconSize
    val ContentPadding: Dp = FormTokens.PaddingHorizontalSmall
    val IconTextSpacing: Dp = FormTokens.PaddingVerticalMedium
    val FontSize: TextUnit = ComponentSize.Medium.fontSize
    val DebounceWait: Duration = FormTokens.DurationNormal.milliseconds

    @Deprecated("Use ContentPadding and IconTextSpacing for the updated search bar layout.")
    val IconPadding: Dp = IconTextSpacing

    @Deprecated("The search bar now uses a trailing clear icon instead of a cancel text action.")
    val CancelFontSize: TextUnit = 16.sp

    @Composable
    fun backgroundColor(enabled: Boolean = true): Color =
        if (enabled) PaletteTheme.colors.surface else PaletteTheme.colors.disabledBackground

    @Composable
    fun borderColor(
        isFocused: Boolean = false,
        isHovered: Boolean = false,
        enabled: Boolean = true,
    ): Color = when {
        !enabled -> PaletteTheme.colors.disabledBorder
        isFocused -> PaletteTheme.colors.focusBorder
        isHovered -> PaletteTheme.colors.hoverBorder
        else -> PaletteTheme.colors.border
    }

    @Composable
    fun placeholderColor(enabled: Boolean = true): Color =
        PaletteTheme.colors.hint.copy(alpha = if (enabled) 1f else 0.55f)

    @Composable
    fun textColor(enabled: Boolean = true): Color =
        PaletteTheme.colors.onSurface.copy(alpha = if (enabled) 1f else 0.55f)

    @Composable
    fun iconColor(enabled: Boolean = true): Color =
        PaletteTheme.colors.hint.copy(alpha = if (enabled) 1f else 0.55f)

    @Composable
    fun clearIconColor(enabled: Boolean = true): Color = iconColor(enabled)

    @Composable
    fun cursorColor(): Color = PaletteTheme.colors.primary

    @Deprecated("The search bar now uses a trailing clear icon instead of a cancel text action.")
    @Composable
    fun cancelColor(): Color = PaletteTheme.colors.primary
}
