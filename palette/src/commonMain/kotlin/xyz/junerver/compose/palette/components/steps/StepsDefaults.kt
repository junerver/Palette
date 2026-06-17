package xyz.junerver.compose.palette.components.steps

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object StepsDefaults {
    val DotSize: Dp = 20.dp
    val LineWidth: Dp = 2.dp

    @Composable
    fun dotSize(): Dp = PaletteTheme.componentThemes.navigationMenu.stepsDotSize

    @Composable
    fun lineWidth(): Dp = PaletteTheme.componentThemes.navigationMenu.stepsLineWidth

    @Composable
    fun lineHeight(): Dp = PaletteTheme.componentThemes.navigationMenu.stepsLineHeight

    @Composable
    fun itemSpacing(): Dp = PaletteTheme.componentThemes.navigationMenu.stepsItemSpacing

    @Composable
    fun rowSpacing(): Dp = PaletteTheme.componentThemes.navigationMenu.stepsRowSpacing

    @Composable
    fun titleTopPadding(): Dp = PaletteTheme.componentThemes.navigationMenu.stepsTitleTopPadding

    @Composable
    fun titleDescriptionSpacing(): Dp = PaletteTheme.componentThemes.navigationMenu.stepsTitleDescriptionSpacing

    @Composable
    fun titleTextStyle(): TextStyle = PaletteTheme.componentThemes.navigationMenu.stepsTitleTextStyle

    @Composable
    fun descriptionTextStyle(): TextStyle = PaletteTheme.componentThemes.navigationMenu.stepsDescriptionTextStyle

    @Composable
    fun doneColor(): Color = PaletteTheme.componentThemes.navigationMenu.stepsDoneColor

    @Composable
    fun currentColor(): Color = PaletteTheme.componentThemes.navigationMenu.stepsCurrentColor

    @Composable
    fun pendingColor(): Color = PaletteTheme.componentThemes.navigationMenu.stepsPendingColor

    @Composable
    fun dotTextColor(): Color = PaletteTheme.componentThemes.navigationMenu.stepsDotTextColor
}
