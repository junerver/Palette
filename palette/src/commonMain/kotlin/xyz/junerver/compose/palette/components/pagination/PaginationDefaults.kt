package xyz.junerver.compose.palette.components.pagination

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object PaginationDefaults {
    val MinTouchSize: Dp = 48.dp
    val Spacing: Dp = 4.dp

    @Composable
    @ReadOnlyComposable
    fun minTouchSize(): Dp = PaletteTheme.componentThemes.dataDisplay.paginationMinTouchSize

    @Composable
    @ReadOnlyComposable
    fun spacing(): Dp = PaletteTheme.componentThemes.dataDisplay.paginationSpacing

    @Composable
    @ReadOnlyComposable
    fun contentPaddingHorizontal(): Dp = PaletteTheme.componentThemes.dataDisplay.paginationContentPaddingHorizontal

    @Composable
    @ReadOnlyComposable
    fun contentPaddingVertical(): Dp = PaletteTheme.componentThemes.dataDisplay.paginationContentPaddingVertical

    @Composable
    @ReadOnlyComposable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.dataDisplay.paginationCornerRadius

    @Composable
    @ReadOnlyComposable
    fun ellipsisPaddingHorizontal(): Dp = PaletteTheme.componentThemes.dataDisplay.paginationEllipsisPaddingHorizontal

    @Composable
    @ReadOnlyComposable
    fun activeColor(): Color = PaletteTheme.componentThemes.dataDisplay.paginationActiveColor

    @Composable
    @ReadOnlyComposable
    fun textColor(): Color = PaletteTheme.componentThemes.dataDisplay.paginationTextColor

    @Composable
    @ReadOnlyComposable
    fun disabledColor(): Color = PaletteTheme.componentThemes.dataDisplay.paginationDisabledColor

    @Composable
    @ReadOnlyComposable
    fun activeBackgroundColor(): Color = PaletteTheme.componentThemes.dataDisplay.paginationActiveBackgroundColor

    @Composable
    @ReadOnlyComposable
    fun backgroundColor(): Color = PaletteTheme.componentThemes.dataDisplay.paginationBackgroundColor

    @Composable
    @ReadOnlyComposable
    fun disabledBackgroundColor(): Color = PaletteTheme.componentThemes.dataDisplay.paginationDisabledBackgroundColor

    @Composable
    @ReadOnlyComposable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.dataDisplay.paginationTextStyle

    @Composable
    fun colors(
        activeColor: Color = PaginationDefaults.activeColor(),
        textColor: Color = PaginationDefaults.textColor(),
        disabledColor: Color = PaginationDefaults.disabledColor(),
        activeBackgroundColor: Color = PaginationDefaults.activeBackgroundColor(),
        backgroundColor: Color = PaginationDefaults.backgroundColor(),
        disabledBackgroundColor: Color = PaginationDefaults.disabledBackgroundColor()
    ): PaginationColors = PaginationColors(
        activeColor = activeColor,
        textColor = textColor,
        disabledColor = disabledColor,
        activeBackgroundColor = activeBackgroundColor,
        backgroundColor = backgroundColor,
        disabledBackgroundColor = disabledBackgroundColor
    )
}

@Immutable
data class PaginationColors(
    val activeColor: Color,
    val textColor: Color,
    val disabledColor: Color,
    val activeBackgroundColor: Color,
    val backgroundColor: Color,
    val disabledBackgroundColor: Color
)
