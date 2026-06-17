package xyz.junerver.compose.palette.components.contextmenu

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object ContextMenuDefaults {
    val MenuWidth: Dp = 160.dp
    val ItemHeight: Dp = 50.dp
    val CornerRadius: Dp = 4.dp
    val ContentPadding: Dp = 8.dp
    val ItemPaddingHorizontal: Dp = 12.dp
    val FontSize: TextUnit = 15.sp
    val AnimationDuration: Int = 150
    val DismissAnimationDuration: Int = 160
    val ShadowElevation: Dp = 8.dp

    @Composable
    fun menuWidth(): Dp = PaletteTheme.componentThemes.navigationMenu.contextMenuWidth

    @Composable
    fun itemHeight(): Dp = PaletteTheme.componentThemes.navigationMenu.contextMenuItemHeight

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.navigationMenu.contextMenuCornerRadius

    @Composable
    fun contentPadding(): Dp = PaletteTheme.componentThemes.navigationMenu.contextMenuContentPadding

    @Composable
    fun itemPaddingHorizontal(): Dp = PaletteTheme.componentThemes.navigationMenu.contextMenuItemPaddingHorizontal

    @Composable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.navigationMenu.contextMenuTextStyle

    @Composable
    fun fontSize(): TextUnit = PaletteTheme.componentThemes.navigationMenu.contextMenuTextStyle.fontSize

    @Composable
    fun animationDurationMillis(): Int = PaletteTheme.componentThemes.navigationMenu.contextMenuAnimationDurationMillis

    @Composable
    fun dismissAnimationDurationMillis(): Int =
        PaletteTheme.componentThemes.navigationMenu.contextMenuDismissAnimationDurationMillis

    @Composable
    fun shadowElevation(): Dp = PaletteTheme.componentThemes.navigationMenu.contextMenuShadowElevation

    @Composable
    fun disabledAlpha(): Float = PaletteTheme.componentThemes.navigationMenu.contextMenuDisabledAlpha

    @Composable
    fun containerColor(): Color = PaletteTheme.componentThemes.navigationMenu.containerColor

    @Composable
    fun textColor(): Color = PaletteTheme.componentThemes.navigationMenu.textColor

    @Composable
    fun disabledTextColor(): Color = PaletteTheme.componentThemes.navigationMenu.disabledTextColor
}
