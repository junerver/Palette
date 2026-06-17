package xyz.junerver.compose.palette.components.searchbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.FormTokens

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
    fun height(): Dp = PaletteTheme.componentThemes.input.searchBarHeight

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.input.searchBarCornerRadius

    @Composable
    fun borderWidth(): Dp = PaletteTheme.componentThemes.input.searchBarBorderWidth

    @Composable
    fun iconSize(): Dp = PaletteTheme.componentThemes.input.searchBarIconSize

    @Composable
    fun clearButtonSize(): Dp = PaletteTheme.componentThemes.input.searchBarClearButtonSize

    @Composable
    fun clearIconSize(): Dp = PaletteTheme.componentThemes.input.searchBarClearIconSize

    @Composable
    fun contentPadding(): Dp = PaletteTheme.componentThemes.input.searchBarContentPadding

    @Composable
    fun iconTextSpacing(): Dp = PaletteTheme.componentThemes.input.searchBarIconTextSpacing

    @Composable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.input.searchBarTextStyle

    @Composable
    fun debounceWait(): Duration = PaletteTheme.componentThemes.input.searchBarDebounceWaitMillis.milliseconds

    @Composable
    fun animationDurationMillis(): Int = PaletteTheme.componentThemes.input.searchBarAnimationDurationMillis

    @Composable
    fun backgroundColor(enabled: Boolean = true): Color =
        if (enabled) PaletteTheme.componentThemes.input.searchBarBackgroundColor
        else PaletteTheme.componentThemes.input.searchBarDisabledBackgroundColor

    @Composable
    fun borderColor(
        isFocused: Boolean = false,
        isHovered: Boolean = false,
        enabled: Boolean = true,
    ): Color = when {
        !enabled -> PaletteTheme.componentThemes.input.searchBarDisabledBorderColor
        isFocused -> PaletteTheme.componentThemes.input.searchBarFocusBorderColor
        isHovered -> PaletteTheme.componentThemes.input.searchBarHoverBorderColor
        else -> PaletteTheme.componentThemes.input.searchBarBorderColor
    }

    @Composable
    fun placeholderColor(enabled: Boolean = true): Color =
        if (enabled) PaletteTheme.componentThemes.input.searchBarPlaceholderColor
        else PaletteTheme.componentThemes.input.searchBarDisabledPlaceholderColor

    @Composable
    fun textColor(enabled: Boolean = true): Color =
        if (enabled) PaletteTheme.componentThemes.input.searchBarTextColor
        else PaletteTheme.componentThemes.input.searchBarDisabledTextColor

    @Composable
    fun iconColor(enabled: Boolean = true): Color =
        if (enabled) PaletteTheme.componentThemes.input.searchBarIconColor
        else PaletteTheme.componentThemes.input.searchBarDisabledIconColor

    @Composable
    fun clearIconColor(enabled: Boolean = true): Color =
        if (enabled) PaletteTheme.componentThemes.input.searchBarClearIconColor
        else PaletteTheme.componentThemes.input.searchBarDisabledClearIconColor

    @Composable
    fun cursorColor(): Color = PaletteTheme.componentThemes.input.searchBarCursorColor

    @Deprecated("The search bar now uses a trailing clear icon instead of a cancel text action.")
    @Composable
    fun cancelColor(): Color = PaletteTheme.componentThemes.input.searchBarCancelColor
}
