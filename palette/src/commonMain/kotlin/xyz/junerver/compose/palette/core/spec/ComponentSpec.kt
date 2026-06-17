package xyz.junerver.compose.palette.core.spec

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.*

enum class ComponentSize(
    val height: Dp,
    val fontSize: TextUnit,
    val iconSize: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val cornerRadius: Dp
) {
    Small(
        height = 24.dp,
        fontSize = 14.sp,
        iconSize = 16.dp,
        horizontalPadding = 12.dp,
        verticalPadding = 4.dp,
        cornerRadius = 4.dp
    ),
    Medium(
        height = 40.dp,
        fontSize = 16.sp,
        iconSize = 20.dp,
        horizontalPadding = 16.dp,
        verticalPadding = 8.dp,
        cornerRadius = 6.dp
    ),
    Large(
        height = 40.dp,
        fontSize = 18.sp,
        iconSize = 24.dp,
        horizontalPadding = 20.dp,
        verticalPadding = 12.dp,
        cornerRadius = 8.dp
    )
}

enum class ComponentStatus {
    Default,
    Success,
    Warning,
    Error;

    @Composable
    fun borderColor(): Color = when (this) {
        Default -> PaletteTheme.colors.border
        Success -> PaletteTheme.colors.success
        Warning -> PaletteTheme.colors.warning
        Error -> PaletteTheme.colors.error
    }

    @Composable
    fun backgroundColor(): Color = when (this) {
        Default -> PaletteTheme.colors.surface
        Success -> PaletteTheme.colors.success.copy(alpha = PaletteTheme.opacity.selected)
        Warning -> PaletteTheme.colors.warning.copy(alpha = PaletteTheme.opacity.selected)
        Error -> PaletteTheme.colors.error.copy(alpha = PaletteTheme.opacity.selected)
    }

    @Composable
    fun shadowColor(): Color = when (this) {
        Default -> PaletteTheme.colors.focusShadow
        Success -> PaletteTheme.componentThemes.textField.successShadowColor
        Warning -> PaletteTheme.componentThemes.textField.warningShadowColor
        Error -> PaletteTheme.componentThemes.textField.errorShadowColor
    }
}

enum class ComponentState {
    Enabled,
    Disabled,
    Readonly,
    Loading,
}

@Composable
fun ComponentSize.tokens(): PaletteControlSizeTokens = when (this) {
    ComponentSize.Small -> PaletteTheme.control.small
    ComponentSize.Medium -> PaletteTheme.control.medium
    ComponentSize.Large -> PaletteTheme.control.large
}

@Composable
fun ComponentSize.tokenHeight(): Dp = tokens().height

@Composable
fun ComponentSize.tokenFontSize(): TextUnit = tokens().fontSize

@Composable
fun ComponentSize.tokenIconSize(): Dp = tokens().iconSize

@Composable
fun ComponentSize.tokenHorizontalPadding(): Dp = tokens().horizontalPadding

@Composable
fun ComponentSize.tokenVerticalPadding(): Dp = tokens().verticalPadding

@Composable
fun ComponentSize.tokenCornerRadius(): Dp = tokens().cornerRadius
