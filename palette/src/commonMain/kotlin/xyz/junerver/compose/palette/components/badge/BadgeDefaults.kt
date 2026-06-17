package xyz.junerver.compose.palette.components.badge

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object BadgeDefaults {
    val Size: Dp = 16.dp

    fun size(size: ComponentSize): Dp = when (size) {
        ComponentSize.Small -> 12.dp
        ComponentSize.Medium -> 16.dp
        ComponentSize.Large -> 20.dp
    }

    @Composable
    fun defaultSize(): Dp = PaletteTheme.componentThemes.dataDisplay.badgeMediumSize

    @Composable
    fun componentSize(size: ComponentSize): Dp = when (size) {
        ComponentSize.Small -> PaletteTheme.componentThemes.dataDisplay.badgeSmallSize
        ComponentSize.Medium -> PaletteTheme.componentThemes.dataDisplay.badgeMediumSize
        ComponentSize.Large -> PaletteTheme.componentThemes.dataDisplay.badgeLargeSize
    }

    @Composable
    fun contentPaddingHorizontal(): Dp = PaletteTheme.componentThemes.dataDisplay.badgeContentPaddingHorizontal

    @Composable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.dataDisplay.badgeTextStyle

    @Composable
    fun color(): Color = PaletteTheme.componentThemes.dataDisplay.badgeContainerColor

    @Composable
    fun contentColor(): Color = PaletteTheme.componentThemes.dataDisplay.badgeContentColor
}
